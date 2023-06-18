package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;
import java.util.Set;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class EchoParams implements IWebWorker{

	@Override
	public void processRequest(RequestContext context) throws Exception {
		
		try {
			context.setMimeType("text/html");
			context.write("<html><body>");
			Set<String> paramNames = context.getParameterNames();
			if(!paramNames.isEmpty()) {
				context.write("<table>");
			}
			for(String name : context.getParameterNames()) {
				context.write("<tr><td>" + name + "</td><td>" + context.getParameter(name) + "</td></tr>");
			}
			if(!paramNames.isEmpty()) {
				context.write("</table>");
			}
			context.write("</body></html>");
		} catch(IOException ex) {
			// Log exception to servers log...
			ex.printStackTrace();
		}
	}

}
