package hr.fer.zemris.java.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;
import hr.fer.zemris.java.webserver.workers.BgColorWorker;
import hr.fer.zemris.java.webserver.workers.Home;
import hr.fer.zemris.java.webserver.workers.SumWorker;

public class SmartHttpServer {
    private String address;
    private String domainName;
    private int port;
    private int workerThreads;
    private int sessionTimeout;
    private Map<String,String> mimeTypes = new HashMap<String, String>();
    private ServerThread serverThread;
    private ExecutorService threadPool;
    private Path documentRoot;
    
    private Map<String,IWebWorker> workersMap;
    private Map<String, SessionMapEntry> sessions = new HashMap<String, SmartHttpServer.SessionMapEntry>();
    private Random sessionRandom = new Random();
    
    private Thread cleaningThread;

    public SmartHttpServer(String configFileName) {

        Properties prop = new Properties();
        try {
            // read properties from file and set them
            prop.load(new FileInputStream(configFileName));
            this.address = prop.getProperty("server.address");
            this.domainName = prop.getProperty("server.domainName");
            this.port = Integer.parseInt(prop.getProperty("server.port"));
            this.workerThreads = Integer.parseInt(prop.getProperty("server.workerThreads"));
            this.sessionTimeout = Integer.parseInt(prop.getProperty("session.timeout"));
            this.documentRoot = Path.of(prop.getProperty("server.documentRoot"));
            String mimeTypesFileName = prop.getProperty("server.mimeConfig");
            String workersFileName = prop.getProperty("server.workers");
            prop.clear();
            // load mime types
            prop.load(new FileInputStream(mimeTypesFileName));
            for (String key : prop.stringPropertyNames()) {
                mimeTypes.put(key, prop.getProperty(key));
            }
            prop.clear();
            
            // load server workers
            prop.load(new FileInputStream(workersFileName));
            workersMap = new HashMap<String, IWebWorker>();

            String fqcn;
            for (String path : prop.stringPropertyNames()){
                fqcn = prop.getProperty(path);

                if(workersMap.get(fqcn) != null) {
                    throw new RuntimeException("Multiple lines with same path.");
                }
                
                try {
                    Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
                    Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
                    IWebWorker iww = (IWebWorker)newObject;
                    workersMap.put(path, iww);
                }catch(Exception e) {
                    throw new RuntimeException("Problem getting worker class " + fqcn);
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error while reading properties file.");
            System.exit(1);
        }

    }
    protected synchronized void start() {
        // … start server thread if not already running …
        // … init threadpool by Executors.newFixedThreadPool(...); …
        if (serverThread == null) {
            serverThread = new ServerThread();
            serverThread.start();

            // demonska dretva koja cisti zastarjele cookije
            cleaningThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(5 * 60 * 1000); // 5 minuta
                    } catch (InterruptedException ignoreable) {}

                    long currentTime = System.currentTimeMillis();
                    long cookieTime;
                    synchronized (sessions) {
                        for (String key : sessions.keySet()) {
                            cookieTime = sessions.get(key).validUntil;
                            if (currentTime - cookieTime > 0) {
                                sessions.remove(key);
                            }
                        }
                    }
                }
            });
            cleaningThread.setDaemon(true);
            cleaningThread.start();
        }
        this.threadPool = Executors.newFixedThreadPool(this.workerThreads);
    }
    protected synchronized void stop() {
        // … signal server thread to stop running …
        // … shutdown threadpool …
        // https://stackoverflow.com/questions/10961714/how-to-properly-stop-the-thread-in-java
        // "Using Thread.interrupt() is a perfectly acceptable way of doing this."
        serverThread.interrupt();
        threadPool.shutdown();
    }

    protected class ServerThread extends Thread {
        @Override
            public void run() {
                // given in pesudo-code:
                // open serverSocket on specified port
                // while(true) {
                // Socket client = serverSocket.accept();
                // ClientWorker cw = new ClientWorker(client);
                // submit cw to threadpool for execution
                // }
                ServerSocket serverSocket = null;
                try{
                    serverSocket = new ServerSocket(port);
                } catch (IOException e) {
                    System.out.println("Error while opening server socket.");
                    System.exit(1);
                }
                while (true) {
                    Socket client = null;
                    try {
                        client = serverSocket.accept();
                    } catch (IOException e) {
                        System.out.println("Error while accepting client.");
                        continue;
                    }
                    ClientWorker cw = new ClientWorker(client);
                    threadPool.submit(cw);
                }
                //serverSocket.close();
            }
        }
        private class ClientWorker implements Runnable, IDispatcher {

            private Socket csocket;
            private InputStream istream;
            private OutputStream ostream;
            private String version;
            private String method;
            private String host;
            private Map<String,String> params = new HashMap<String, String>();
            private Map<String,String> tempParams = new HashMap<String, String>();
            private Map<String,String> permParams = new HashMap<String, String>();
            private List<RCCookie> outputCookies = new ArrayList<RequestContext.RCCookie>();
            private String SID;
            private RequestContext context = null;
            public ClientWorker(Socket csocket) {
                super();
                this.csocket = csocket;
            }

            @Override
            public void run() {
                istream = null;
                ostream = null;
                try{
                    // obtain input stream from socket
                    // obtain output stream from socket
                    istream = csocket.getInputStream();
                    ostream = csocket.getOutputStream();
                }
                catch (IOException e) {
                    //System.out.println("Error while getting input/output stream.");
                    writeToOutput("HTTP/1.1 400 Bad Request\r\n");
                    return;
                }
                // Then read complete request header from your client in separate method...
                List<String> request = readRequest();
                 // If header is invalid (less then a line at least) return response status 400
                if (request.size() < 1) {
                    //System.out.println("Request is empty.");
                    writeToOutput("HTTP/1.1 400 Bad Request\r\n");
                    return;
                }
                String firstLine = request.get(0);
                // Extract (method, requestedPath, version) from firstLine
                String[] firstLineSplitted = firstLine.split(" ");
                if (firstLineSplitted.length != 3) {
                    //System.out.println("First line expected 3 elements but got " + firstLineSplitted.length + ".");
                    writeToOutput("HTTP/1.1 400 Bad Request\r\n");
                    return;
                }
                String method = firstLineSplitted[0];
                String requestedPath = firstLineSplitted[1];
                String version = firstLineSplitted[2];

                this.method = method;
                this.version = version;

                // if method not GET or version not HTTP/1.0 or HTTP/1.1 return response status 400
                if (!method.equals("GET")) {
                    //System.out.println("Method " + method + " is not supported.");
                    writeToOutput("HTTP/1.1 400 Bad Request\r\n");
                    return;
                }
                if (!version.equals("HTTP/1.1") && !version.equals("HTTP/1.0")) {
                    writeToOutput("HTTP/1.1 400 Bad Request\r\n");
                    return;
                }

                // Go through headers, and if there is header “Host: xxx”, assign host property
                // to trimmed value after “Host:”; else, set it to server’s domainName
                // If xxx is of form some-name:number, just remember “some-name”-part
                for (String headerLine : request){
                    if (headerLine.startsWith("Host:")) {
                        String host = headerLine.split(" ")[1];
                        if (host.contains(":")) {
                            host = host.split(":")[0].trim();
                        }
                        this.host = host;
                    }
                }

                if (this.host == null) {
                    this.host = domainName;
                }

                //when you process clients request, before doing anything else (before calling parseParameters) call
                //the method checkSession with a list of header lines
                synchronized (sessions) {
                    checkSession(request);
                }

                String path; String paramString;
                // (path, paramString) = split requestedPath to path and parameterString
                
                if (requestedPath.contains("?")) {
                    String[] requestedPathSplitted = requestedPath.split("\\?");
                    path = requestedPathSplitted[0];
                    paramString = requestedPathSplitted[1];
                } else {
                    path = requestedPath;
                    paramString = null;
                }
                // parseParameters(paramString); ==> your method to fill map parameters
                try {
                    parseParameters(paramString);
                } catch (IllegalArgumentException e) {
                    return;
                }

                
                try {
					internalDispatchRequest(path, true);
				} catch (Exception e) {
					return;
				}
            }
            
            private void internalDispatchRequest(String urlPath, boolean directCall) throws Exception {
                
                if (urlPath.startsWith("/")) {
                    urlPath = urlPath.substring(1);
                }


                String urlStart = "/" + urlPath.split("/")[0];
                
                if (urlStart.equals("/private") && directCall) {
                    writeToOutput("HTTP/1.1 404 Not found Error\r\n");
                    return;
                }

                if (urlStart.equals("/ext")){
                    String className = urlPath.substring(4);
                    String fqcn = "hr.fer.zemris.java.webserver.workers." + className;
                    try {
                        Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
                        Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
                        IWebWorker iww = (IWebWorker)newObject;
                        if (context == null) {
                            context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, "");
                        }
                        iww.processRequest(context);
                        ostream.flush();
                        ostream.close();
                        return;
                    } catch (Exception e) {
                        writeToOutput("HTTP/1.1 404 Not found Error\r\n");
                        return;
                    }
                }
                
                if (urlStart.equals("/calc")){
                    SumWorker iww = new SumWorker();
                    if (context == null) {
                        context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, "");
                    }
                    iww.processRequest(context);
                    ostream.flush();
                    ostream.close();
                    return;
                }
                
                if(urlStart.equals("/index2.html")) {
                    Home iww = new Home();
                    if (context == null) {
                        context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, "");
                    }
                    iww.processRequest(context);
                    ostream.flush();
                    ostream.close();
                    return;
                }
                
                if(urlStart.equals("/setbgcolor")) {
                	BgColorWorker iww = new BgColorWorker();
                	if (context == null) {
                        context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, "");
                    }
                	iww.processRequest(context);
                    ostream.flush();
                    ostream.close();
                    return;
                }


                IWebWorker worker = workersMap.get(urlStart);
                if (worker != null) {
                    if (context == null) {
                        context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, "");
                    }
                    worker.processRequest(context);
                    ostream.flush();
                    ostream.close();
                    return;
                }
            
            
                File file = checkIfFileOkay(urlPath);
                
                if(file == null) {
                    return;
                }
                
                String[] fileSplitted = file.getName().split("\\.");
                String fileExtension = fileSplitted[fileSplitted.length - 1];
                
                
                // if file is smartscript, execute it so it writes into output stream
                if(fileExtension.equals("smscr")) {
                    String documentBody = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    DocumentNode node = new SmartScriptParser(documentBody).getDocumentNode();
                    RequestContext context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, "");
                    SmartScriptEngine engine = new SmartScriptEngine(node, context);
                    engine.execute();
                    ostream.flush();
                    ostream.close();
                    return;
                }

                // else treat it as normal file

                // find in mimeTypes map appropriate mimeType for current file extension
                // (you filled that map during the construction of SmartHttpServer from mime.properties)
                // if no mime type found, assume application/octet-stream
                String mimeType = mimeTypes.get(fileExtension);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                // create a rc = new RequestContext(...); set mime-type; set status to 200
                // If you want, you can modify RequestContext to allow you to add additional headers
                // so that you can add “Content-Length: 12345” if you know that file has 12345 bytes
                // open file, read its content and write it to rc (that will generate header and send
                // file bytes to client)

                RequestContext rc = new RequestContext(ostream, params, permParams, outputCookies);
                rc.setMimeType(mimeType);
                rc.setStatusCode(200);

                try {
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    rc.write(fileBytes);
                    ostream.flush();
                    ostream.close();
                    //csocket.close();
                } catch (IOException e) {
                    writeToOutput("HTTP/1.1 500 Internal Server Error\r\n");
                    return;
                }
            }
            
            public void dispatchRequest(String urlPath) throws Exception {
                internalDispatchRequest(urlPath, false);
            }

            private File checkIfFileOkay(String urlPath) {
            	// requestedPath = resolve path with respect to documentRoot
                String requestedPath = documentRoot.resolve(urlPath).toString();
                File file = new File(requestedPath);
                String normalizedString = file.toPath().normalize().toString();
                // if requestedPath is not below documentRoot, return response status 403 forbidden
                // https://stackoverflow.com/questions/4746671/how-to-check-if-a-given-path-is-possible-child-of-another-path
                if(!normalizedString.startsWith(documentRoot.normalize().toString())) {
                    System.out.println("here");
                    writeToOutput("HTTP/1.1 403 Forbidden\r\n");
                    return null;
                }
                // check if requestedPath exists, is file and is readable; if not, return status 404
                // else extract file extension
                if(!file.exists() || !file.isFile() || !file.canRead()) {
                    writeToOutput("HTTP/1.1 404 Not Found\r\n");
                    return null;
                }
                return file;
            }

            private void writeToOutput(String s) {
                try {
                    ostream.write(s.getBytes(StandardCharsets.US_ASCII));
                    ostream.flush();
                    ostream.close();
                    //csocket.close();
                } catch (IOException e) {
                    System.out.println("Error while writing to output stream.");
                }
            }

            private List<String> readRequest() {
                List<String> toReturn = new ArrayList<String>();
                // https://stackoverflow.com/questions/34954630/java-read-line-using-inputstream
                BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
                // cita liniju po liniju dok ne dodje do prazne linije
                // prazna linija oznacava kraj zaglavlja
                while (true) {
                    String line = null;
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        //System.out.println("Error while reading request.");
                        //return toReturn;
                    }
                    if (line == null || line.equals("") || line.equals("\n") || line.equals("\r\n")) {
                        break;
                    }
                    toReturn.add(line);
                }
                return toReturn;
            }

            // fill parameters map
            private void parseParameters(String paramString) throws IllegalArgumentException{
                if (paramString == null) {
                    return;
                }
                String[] params = paramString.split("&");
                for (String param : params) {
                    String[] paramSplitted = param.split("=");
                    if (paramSplitted.length != 2) {
                        writeToOutput("HTTP/1.1 400 Bad Request\r\n");
                        throw new IllegalArgumentException("Cannot parse params");
                    }
                    this.params.put(paramSplitted[0], paramSplitted[1]);
                }
            }

            private String generateSid() {
                String sid = "";
                for (int i = 0; i < 20; i++) {
                    sid += (char)('A' + sessionRandom.nextInt(26));
                }
                return sid;
            }
    
            private void checkSession(List<String> headers){
                for (String line : headers){
                    if (!line.startsWith("Cookie:")){
                        continue;
                    }
                    line = line.substring(7).trim(); // preskacemo "Cookie:"
                    String[] cookies = line.split(";");
                    for (String cookie : cookies){
                        String[] cookieSplitted = cookie.split("=");
                        if (cookieSplitted.length != 2){
                            continue;
                        }
                        if (cookieSplitted[0].trim().equals("sid")){
                            String sidCandidate = cookieSplitted[1].trim().replace("\"", "");
                            SessionMapEntry entry = sessions.get(sidCandidate);
                            if (entry == null || !this.host.equals(entry.host) || entry.validUntil < System.currentTimeMillis()){
                                if (entry != null && entry.validUntil < System.currentTimeMillis()){
                                    sessions.remove(sidCandidate);
                                }
                                String newSid = generateSid();
                                entry = new SessionMapEntry(newSid, this.host, System.currentTimeMillis() + sessionTimeout * 1000, new ConcurrentHashMap<String, String>());
                                sessions.put(newSid, entry);
                                outputCookies.add(new RCCookie("sid", newSid, null, this.host, "/"));
                                //context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, newSid);
                            }else{
                                entry.validUntil = System.currentTimeMillis() + sessionTimeout * 1000;
                                //sessions.put(sidCandidate, entry);
                            }
                            this.SID = entry.sid;
                            this.permParams = entry.map;
                            return;
                        }
                    }
                }
                // ako nismo nasli sid u cookiesima, generiramo novi
                String newSid = generateSid();
                SessionMapEntry entry = new SessionMapEntry(newSid, this.host, System.currentTimeMillis() + sessionTimeout * 1000, new ConcurrentHashMap<String, String>());
                sessions.put(newSid, entry);
                outputCookies.add(new RCCookie("sid", newSid, null, this.host, "/"));
                //context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, newSid);
                this.SID = entry.sid;
                this.permParams = entry.map;
            }
        }
        
        private static class SessionMapEntry {
            String sid;
            String host;
            long validUntil;
            Map<String, String> map;

            public SessionMapEntry(String sid, String host, long validUntil, Map<String, String> map) {
                this.sid = sid;
                this.host = host;
                this.validUntil = validUntil;
                this.map = map;
            }
        }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Expected 1 argument (server properties), got " + args.length + ".");
            System.exit(1);
        }
        String serverProps = args[0];
        SmartHttpServer server = new SmartHttpServer(serverProps);
        server.start();
    }
}
    
