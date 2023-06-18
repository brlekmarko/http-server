package hr.fer.zemris.java.custom.scripting.demo;

import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

public class TreeWriter {
	
	private static class WriterVisitor implements INodeVisitor{

		private String text = "";

		@Override
		public void visitTextNode(TextNode node) {
			text+=node.getText();
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			text+= "{$FOR ";
			text += node.getVariable().asText() + " ";
			text += node.getStartExpression().asText() + " ";
			text += node.getEndExpression().asText() + " ";
			if(node.getStepExpression() != null) {
				text += node.getStepExpression().asText() + " ";
			}
			text+="$}";

			// for loop moze imati djecu pa posjecujemo njih
			for(int i=0; i<node.numberOfChildren(); i++) {
				node.getChild(i).accept(this);
			}
			
			text+="{$END$}";
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			text+= "{$= ";
			Element[] elem = node.getElements();
			for(int j=0; j<elem.length; j++) {
				if(elem[j] instanceof ElementString) {
					text+= "\"" + elem[j].asText() + "\" ";
				}
				else {
					text += elem[j].asText() + " ";
				}
			}
			text+= "$}";
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			Node current;
			
			// posjecujemo svu njegovu djecu
			for(int i=0; i<node.numberOfChildren(); i++){
				current = node.getChild(i);
				current.accept(this);
			}
			System.out.println(text);			
		}
		
	}
	
	
	public static void main(String[] args) {
		
		if(args.length != 1) {
			System.out.println("Expected one argument.");
			System.exit(1);
		}

		String fileName = args[0];

		try{
			String docBody = new String(Files.readAllBytes(Paths.get(fileName)), "UTF-8");
			SmartScriptParser p = new SmartScriptParser(docBody);
			WriterVisitor visitor = new WriterVisitor();
			p.getDocumentNode().accept(visitor);
			// ovako se prije ispisivalo
			//System.out.println(p.getDocumentNode().toString());
		} catch (Exception e) {
			System.out.println("Error while reading file.");
			System.exit(1);
		}
	}
}
