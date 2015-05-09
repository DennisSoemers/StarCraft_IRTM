package irtm.starcraft.utils;

import irtm.starcraft.utils.WikiPageNode.NodeTypes;

import java.util.ArrayList;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Class that holds the relevant information of a teamliquid wiki page about the
 * game of StarCraft: Brood War in a tree structure.
 * 
 * @author Soemers
 *
 */
public class WikiPageTree {
	
	private WikiPageNode rootNode;
	
	/**
	 * Constructs a tree representing the document given a list of relevant text elements
	 * 
	 * @param relevantElements
	 */
	public WikiPageTree(ArrayList<Element> relevantElements){
		Element root = relevantElements.get(0);
		
		if(!HtmlUtils.isHeaderTag(root.tagName())){
			System.err.println("First relevant element is not a header tag!");
			return;
		}
		
		rootNode = new WikiPageNode(null, NodeTypes.Header, root);
		
		WikiPageNode currentHeaderNode = rootNode;
		for(int i = 1; i < relevantElements.size(); ++i){
			Element element = relevantElements.get(i);
			String tagName = element.tagName();
			
			if(HtmlUtils.isHeaderTag(tagName)){
				// follow chain of parents back up until our new header is of a smaller size than the current header node
				while(element.tagName().compareTo(currentHeaderNode.getElement().tagName()) <= 0){
					currentHeaderNode = currentHeaderNode.parent();
				}
				
				WikiPageNode newNode = new WikiPageNode(currentHeaderNode, NodeTypes.Header, element);
				currentHeaderNode.addChild(newNode);
				currentHeaderNode = newNode;
			}
			else if(tagName.equals("ul")){
				currentHeaderNode.addChild(new WikiPageNode(currentHeaderNode, NodeTypes.List, element));
			}
			else if(tagName.equals("p")){
				currentHeaderNode.addChild(new WikiPageNode(currentHeaderNode, NodeTypes.Text, element));
			}
			else{
				System.err.println("WikiPageTree::WikiPageTree(): Unknown tag name!");
			}
		}
	}
	
	/**
	 * Prints a description of the tree to System.out
	 */
	public void printTree(){
		printTreeRecursion(rootNode, "");
	}
	
	/**
	 * Recursively performs the printing of the tree
	 * 
	 * @param node
	 */
	private void printTreeRecursion(WikiPageNode node, String indentation){
		NodeTypes nodeType = node.getNodeType();
		Element nodeElement = node.getElement();
		
		if(nodeType == NodeTypes.Header){
			System.out.println(indentation + nodeElement.text());
			ArrayList<WikiPageNode> children = node.children();
			
			for(WikiPageNode child : children){
				printTreeRecursion(child, indentation + "    ");
			}
		}
		else if(nodeType == NodeTypes.List){
			System.out.println(indentation + "List:");
			Elements listElements = nodeElement.children();
			
			for(Element listElement : listElements){
				System.out.println(indentation + "    - " + listElement.text());
			}
		}
		else if(nodeType == NodeTypes.Text){
			System.out.println(indentation + nodeElement.text());
		}
	}

}
