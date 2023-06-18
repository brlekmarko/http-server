package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class SumWorker implements IWebWorker{

	@Override
	public void processRequest(RequestContext context) throws Exception {
		
		int varA = 1;
		int varB = 2;
		
		// dohvacanje parametara
		String paramA = context.getParameter("a");
		String paramB = context.getParameter("b");
		
		if(paramA != null) {
			try {
				varA = Integer.parseInt(paramA);
			} catch (Exception ignorable) {
			}
		}
		
		if(paramB != null) {
			try {
				varB = Integer.parseInt(paramB);
			} catch (Exception ignorable) {
			}
		}
		/////////////////////////////////
		
		int sum = varA + varB;
		
		context.setTemporaryParameter("zbroj", sum + "");
		context.setTemporaryParameter("varA", varA + "");
		context.setTemporaryParameter("varB", varB + "");
		
		if(sum % 2 == 0) {
			context.setTemporaryParameter("imgName", "bateman1.jpg");
		}else {
			context.setTemporaryParameter("imgName", "bateman2.jpg");
		}
		context.getDispatcher().dispatchRequest("/private/pages/calc.smscr");
		
	}

}
