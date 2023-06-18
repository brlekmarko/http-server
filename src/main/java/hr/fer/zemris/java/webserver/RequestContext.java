package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestContext {
	
	private OutputStream outputStream;
	private Charset charset;
	
	private String encoding = "UTF-8";
	private int statusCode = 200;
	private String statusText = "OK";
	private String mimeType = "text/html";
	private Long contentLength = null;
	private String SID;
	
	private Map<String, String> parameters;
	private Map<String, String> temporaryParameters = new HashMap<String, String>();
	private Map<String, String> persistentParameters;
	private List<RCCookie> outputCookies;
	
	private boolean headerGenerated = false;
	
	private IDispatcher dispatcher;
	
	public RequestContext(OutputStream outputStream, Map<String, String> parameters, 
			Map<String, String> persistentParameters, List<RCCookie> outputCookies) {
		this.outputStream = outputStream;
		this.parameters = parameters;
		this.persistentParameters = persistentParameters;
		this.outputCookies = outputCookies;
	}
	
	public RequestContext(OutputStream outputStream, Map<String, String> parameters, 
			Map<String, String> persistentParameters, List<RCCookie> outputCookies, 
			Map<String,String> temporaryParameters, IDispatcher dispatcher, String SID) {
		this(outputStream, parameters, persistentParameters, outputCookies);
		this.temporaryParameters = temporaryParameters;
		this.dispatcher = dispatcher;
		this.SID = SID;
	}
	
	
	public void setEncoding(String encoding) {
		if (headerGenerated) throw new RuntimeException("Cannot change now.");
		this.encoding = encoding;
	}
	
	public void setStatusCode(int statusCode) {
		if (headerGenerated) throw new RuntimeException("Cannot change now.");
		this.statusCode = statusCode;
	}
	
	public void setStatusText(String statusText) {
		if (headerGenerated) throw new RuntimeException("Cannot change now.");
		this.statusText = statusText;
	}
	
	public void setMimeType(String mimeType) {
		if (headerGenerated) throw new RuntimeException("Cannot change now.");
		this.mimeType = mimeType;
	}
	
	public void setContentLength(Long contentLength) {
		if (headerGenerated) throw new RuntimeException("Cannot change now.");
		this.contentLength = contentLength;
	}
	
	public void addRCCookie(RCCookie cookie) {
		if (headerGenerated) throw new RuntimeException("Cannot change now.");
		outputCookies.add(cookie);
	}
	
	public void removeRCCookie(RCCookie cookie) {
		if (headerGenerated) throw new RuntimeException("Cannot change now.");
		outputCookies.remove(cookie);
	}
	
	public List<RCCookie> getOutputCookies(){
		return Collections.unmodifiableList(outputCookies);
	}
	
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	public Set<String> getParameterNames(){
		return Collections.unmodifiableSet(parameters.keySet());
	}
	
	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}
	
	public Set<String> getPersistentParameterNames(){
		return Collections.unmodifiableSet(persistentParameters.keySet());
	}
	
	public void setPersistentParameter(String name, String value) {
		persistentParameters.put(name, value);
	}
	
	public void removePersistentParameter(String name) {
		persistentParameters.remove(name);
	}
	
	public String getTemporaryParameter(String name) {
		return temporaryParameters.get(name);
	}
	
	public Set<String> getTemporaryParameterNames(){
		return Collections.unmodifiableSet(temporaryParameters.keySet());
	}
	
	public String getSessionID() {
		return this.SID;
	}
	
	public void setTemporaryParameter(String name, String value) {
		temporaryParameters.put(name, value);
	}
	
	public void removeTemporaryParameter(String name) {
		temporaryParameters.remove(name);
	}
	
	public IDispatcher getDispatcher() {
		return this.dispatcher;
	}
	
	
	// ako header nije generiran, generira ga
	// zatim zapisuje podatke u output stream 
	// poziva metodu write(byte[] data, int offset, int len)
	public RequestContext write(byte[] data) throws IOException{
		if (!headerGenerated) {
			charset = Charset.forName(encoding);
			createHeader();
		}
		return write(data, 0, data.length);
	}

	// ako header nije generiran, generira ga
	// zatim zapisuje podatke u output stream
	public RequestContext write(byte[] data, int offset, int len) throws IOException{
		if (!headerGenerated) {
			charset = Charset.forName(encoding);
			createHeader();
		}
		outputStream.write(data, offset, len);
		return this;
	}

	// ako header nije generiran, generira ga
	// zatim zapisuje podatke u output stream
	// poziva metodu write(byte[] data)
	public RequestContext write(String text) throws IOException{
		if (!headerGenerated) {
			charset = Charset.forName(encoding);
			createHeader();
		}

		byte[] data = text.getBytes(charset);
		return write(data);
	}

	// generira header sa poljima opisanim u zadatku
	// kodiranje znakova je ISO-8859-2 (charset)
	private void createHeader() throws IOException{
		headerGenerated = true;

		String header = "HTTP/1.1 {statusCode} {statusText}\r\n";
		if(mimeType.startsWith("text/")) {
			header += "Content-Type: {mimeType}; charset={encoding}\r\n";
		} else {
			header += "Content-Type: {mimeType}\r\n";
		}
		if(contentLength != null) {
			header += "Content-Length: " + contentLength + "\r\n";
		}

		for(RCCookie cookie : outputCookies) {
			header+="Set-Cookie: "+ cookie.getName() +"=\""+ cookie.getValue() +"\"";
			if(cookie.getDomain() != null) {
				header+="; Domain="+cookie.getDomain();
			}
			if(cookie.getPath() != null) {
				header+="; Path="+cookie.getPath();
			}
			if(cookie.getMaxAge() != null) {
				header+="; Max-Age="+cookie.getMaxAge();
			}
			header+="; HttpOnly";
			header+="\r\n";
		}
		header += "\r\n";
		header = header.replace("{statusCode}", Integer.toString(statusCode)).replace("{statusText}", statusText)
				.replace("{mimeType}", mimeType).replace("{encoding}", encoding);


		byte[] data = header.getBytes(StandardCharsets.ISO_8859_1);

		outputStream.write(data);
	}
	
	public static class RCCookie{
		
		private String name, value, domain, path;
		private Integer maxAge;

		public RCCookie(String name, String value, Integer maxAge, String domain, String path) {
			this.name = name;
			this.value = value;
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
		}

		public RCCookie(String name, String value, Integer maxAge, String domain) {
			this(name, value, maxAge, domain, null);
		}

		public RCCookie(String name, String value, Integer maxAge) {
			this(name, value, maxAge, null, null);
		}

		public RCCookie(String name, String value) {
			this(name, value, null, null, null);
		}

		// domain, path, maxAge su opcionalni parametri

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public String getDomain() {
			return domain;
		}

		public String getPath() {
			return path;
		}

		public Integer getMaxAge() {
			return maxAge;
		}

	}

}
