package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.text.DecimalFormat;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;

public class SmartScriptEngine {
	private DocumentNode documentNode;
	private RequestContext requestContext;
	private ObjectMultistack multistack = new ObjectMultistack();
	
	private INodeVisitor visitor = new INodeVisitor() {

		@Override
		public void visitTextNode(TextNode node) {
			String text = node.getText();
			try {
				requestContext.write(text);
			} catch (IOException e) {
				System.out.println("Error while writing text to output stream.");
			}
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			ElementVariable var = node.getVariable();
			ValueWrapper startValue = new ValueWrapper(node.getStartExpression().asText());
			ValueWrapper endValue = new ValueWrapper(node.getEndExpression().asText());
			ValueWrapper stepValue = new ValueWrapper(node.getStepExpression().asText());

			// pushamo inicijalnu vrijednost varijable na stog
			multistack.push(var.asText(), new ValueWrapper(startValue.getValue()));
		
			ValueWrapper current;
			while(true) {
				// uzimamo vrijednost varijable sa stoga
				// i usporedujemo je s krajnjom vrijednosti, ako je veca, izlazimo iz petlje
				current = multistack.peek(var.asText());
				if(current.numCompare(endValue.getValue()) > 0) {
					break;
				}
				// zovemo accept nad svom djecom
				for(int i=0; i<node.numberOfChildren(); i++) {
					node.getChild(i).accept(this);
				}
				// updateamo vrijednost varijable na stogu tako da ju uvecamo za korak
				current.add(stepValue.getValue());

			}
			// ispražnimo stog
			multistack.pop(var.asText());
		}	

		@Override
		public void visitEchoNode(EchoNode node) {
			ObjectMultistack tempStack = new ObjectMultistack();
			Element current;
			String currentText;
			ValueWrapper helperValue1;
			ValueWrapper helperValue2;
			String key = "temp";

			for(int i=0; i<node.getElements().length; i++) {
				current = node.getElements()[i];
				currentText = current.asText();
				if(current instanceof ElementString || current instanceof ElementConstantDouble || current instanceof ElementConstantInteger) {
					helperValue1 = new ValueWrapper(currentText);
					tempStack.push(key, helperValue1);
				}
				else if(current instanceof ElementVariable) {
					helperValue1 = new ValueWrapper(multistack.peek(currentText).getValue());
					tempStack.push(key, helperValue1);
				}
				else if(current instanceof ElementOperator){
					// uzimamo dva elementa sa stoga i izvrsavamo operaciju
					// rezultat spremamo na stog
					helperValue2 = tempStack.pop(key);
					helperValue1 = tempStack.pop(key);
					if(currentText.equals("+")) {
						// value1 + value2
						helperValue1.add(helperValue2.getValue());
						tempStack.push(key, helperValue1);
					}
					else if(currentText.equals("-")) {
						// value1 - value2
						helperValue1.subtract(helperValue2.getValue());
						tempStack.push(key, helperValue1);
					}
					else if(currentText.equals("*")) {
						// value1 * value2
						helperValue1.multiply(helperValue2.getValue());
						tempStack.push(key, helperValue1);
					}
					else if(currentText.equals("/")) {
						// value1 / value2
						helperValue1.divide(helperValue2.getValue());
						tempStack.push(key, helperValue1);
					}
					else{
						// ne podrzan operator
						throw new IllegalArgumentException("Invalid operator!");
					}
				}
				else if(current instanceof ElementFunction) {
					// provjerimo koja je funkcija, izvrsimo ju
					if(currentText.equals("@sin")){
						// uzimamo vrijednost sa stoga, izvrsimo sin i stavimo rezultat nazad na stog
						helperValue1 = tempStack.pop(key);
						try{
							helperValue1.setValue(Math.sin(Math.toRadians((double)helperValue1.getValue())));
							tempStack.push(key, helperValue1);
						}
						catch(Exception e) {
							try{
								//helperValue1.setValue(Math.sin(Math.toDegrees((int)helperValue1.getValue())));
								helperValue1.setValue(Math.sin(Math.toRadians((int)helperValue1.getValue())));
								tempStack.push(key, helperValue1);
							}catch(Exception e2){
								throw new IllegalArgumentException("Invalid argument for sin function!");
							}
						}
					}
					else if(currentText.equals("@decfmt")){
						// uzimamo vrijednost sa stoga, formatiramo je i stavljamo nazad na stog
						// format takoder uzimamo sa stoga
						helperValue2 = tempStack.pop(key); //format
						helperValue1 = tempStack.pop(key); //broj
						try{
							// postavimo decimalni format, uzimamo ga sa stoga
							DecimalFormat df = new DecimalFormat(helperValue2.getValue().toString());
							// formatiramo vrijednost sa stoga po proslom formatu i stavljamo je nazad stog
							helperValue1.setValue(df.format(helperValue1.getValue()));
							tempStack.push(key, helperValue1);
						}
						catch(Exception e) {
							throw new IllegalArgumentException("Invalid argument for decfmt function!");
						}
					}
					else if(currentText.equals("@dup")){
						// uzimamo vrijednost sa stoga i stavljamo je dvaput na stog
						helperValue1 = tempStack.pop(key);
						helperValue2 = new ValueWrapper(helperValue1.getValue());
						tempStack.push(key, helperValue1);
						tempStack.push(key, helperValue2);
					}
					else if(currentText.equals("@swap")){
						// uzimamo dvije vrijednosti sa stoga i stavljamo ih na stog u obrnutom redoslijedu
						helperValue1 = tempStack.pop(key);
						helperValue2 = tempStack.pop(key);
						tempStack.push(key, helperValue1);
						tempStack.push(key, helperValue2);
					}
					else if(currentText.equals("@setMimeType")){
						// uzimamo vrijednost sa stoga i postavljamo je kao mime type
						// u request contextu
						helperValue1 = tempStack.pop(key);
						requestContext.setMimeType(helperValue1.getValue().toString());
					}
					else if(currentText.equals("@paramGet")){
						// uzimamo dvije vrijednosti sa stoga, prva je ime parametra, druga je default vrijednost
						// ako postoji parametar sa tim imenom u request contextu, stavljamo njegovu vrijednost na stog
						// ako ne postoji, stavljamo default vrijednost na stog
						helperValue2 = tempStack.pop(key); //defValue
						helperValue1 = tempStack.pop(key); //name
						String value = requestContext.getParameter(helperValue1.getValue().toString());
						if(value == null) {
							helperValue1.setValue(helperValue2.getValue().toString());
						}
						else {
							helperValue1.setValue(value);
						}
						tempStack.push(key, helperValue1);
					}
					else if(currentText.equals("@pparamGet")){
						// isto kao paramGet ali za persistent parametre
						helperValue2 = tempStack.pop(key); //defValue
						helperValue1 = tempStack.pop(key); //name
						String value = requestContext.getPersistentParameter(helperValue1.getValue().toString());
						if(value == null) {
							helperValue1.setValue(helperValue2.getValue());
						}
						else {
							helperValue1.setValue(value);
						}
						tempStack.push(key, helperValue1);
					}
					else if(currentText.equals("@pparamSet")){
						// uzimamo dvije vrijednosti sa stoga, prva je ime parametra, druga je vrijednost
						// postavljamo persistent parametar u request contextu
						helperValue2 = tempStack.pop(key); //name
						helperValue1 = tempStack.pop(key); //value
						requestContext.setPersistentParameter(helperValue2.getValue().toString(), helperValue1.getValue().toString());
					}
					else if(currentText.equals("@pparamDel")){
						// uzimamo vrijednost sa stoga, brisemo persistent parametar iz request contexta
						// koji ima tu vrijednost kao kljuc
						helperValue1 = tempStack.pop(key);
						requestContext.removePersistentParameter(helperValue1.getValue().toString());
					}
					else if(currentText.equals("@tparamGet")){
						// isto kao paramGet ali za temporary parametre
						helperValue2 = tempStack.pop(key); //defValue
						helperValue1 = tempStack.pop(key); //name
						String value = requestContext.getTemporaryParameter(helperValue1.getValue().toString());
						if(value == null) {
							helperValue1.setValue(helperValue2.getValue().toString());
						}
						else {
							helperValue1.setValue(value);
						}
						tempStack.push(key, helperValue1);
					}
					else if(currentText.equals("@tparamSet")){
						// isto kao pparamSet ali za temporary parametre
						helperValue2 = tempStack.pop(key); //name
						helperValue1 = tempStack.pop(key); //value
						requestContext.setTemporaryParameter(helperValue2.getValue().toString(), helperValue1.getValue().toString());
					}
					else if(currentText.equals("@tparamDel")){
						// isto kao pparamDel ali za temporary parametre
						helperValue1 = tempStack.pop(key);
						requestContext.removeTemporaryParameter(helperValue1.getValue().toString());
					}
					else{
						// ako nismo nasli nista, bacamo iznimku
						throw new IllegalArgumentException("Function " + currentText + " not defined");
					}
					
				}
			}
			if(!tempStack.isEmpty(key)){
				String obrnutiKey = "tempObrnuto";
				while(!tempStack.isEmpty(key)){
					// prvo moramo obrnuti stog
					helperValue1 = tempStack.pop(key);
					tempStack.push(obrnutiKey, helperValue1);
				}
				while(!tempStack.isEmpty(obrnutiKey)){
					// onda ispisujemo sve elemente stoga
					helperValue1 = tempStack.pop(obrnutiKey);
					try{
						requestContext.write(helperValue1.getValue().toString());
					} catch (IOException e) {
						System.out.println("Greska pri pisanju u request context");
					}
				}
			}
			
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			
			// posjecujemo svu njegovu djecu
			for(int i=0; i<node.numberOfChildren(); i++){
				node.getChild(i).accept(this);
			}
			//System.out.println(text);	
		}
	};
	
	public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
		this.documentNode = documentNode;
		this.requestContext = requestContext;
	}
	
	
	public void execute() {
		documentNode.accept(visitor);
	}
}