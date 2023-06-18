package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class BgColorWorker implements IWebWorker{

	@Override
	public void processRequest(RequestContext context) throws Exception {
		
		String color = context.getParameter("bgcolor");
		
		if(color != null) {
			for(char c : color.toCharArray()) {
				if ((c<0 && c>9) && (c<'A' && c>'F') && (c<'a' && c>'f')) {
					generateInvalid(context);
					return;
				}
			}
			context.setPersistentParameter("bgcolor", color);
			generateValid(context);
			return;
		}
		generateInvalid(context);
		return;
	}
	
	private void generateInvalid(RequestContext context) throws IOException {
		context.write("<html><body>");
		context.write("Color is <b>not</b> updated<br/>");
		context.write("<a href=\"/index2.html\">Home</a>");
	}
	
	private void generateValid(RequestContext context) throws IOException {
		context.write("<html><body>");
		context.write("Color was updated<br/>");
		context.write("<a href=\"/index2.html\">Home</a>");
	}
}
