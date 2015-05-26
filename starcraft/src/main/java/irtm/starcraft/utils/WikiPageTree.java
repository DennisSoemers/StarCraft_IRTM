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
			else if(HtmlUtils.isListTag(tagName) && !element.parent().tagName().equals("li")){
				currentHeaderNode.addChild(new WikiPageNode(currentHeaderNode, NodeTypes.List, element));
			}
			else if(tagName.equals("p")){
				currentHeaderNode.addChild(new WikiPageNode(currentHeaderNode, NodeTypes.Text, element));
			}
		}
	}
	
	/**
	 * Collects all header nodes of the entire tree
	 * 
	 * @return
	 */
	public ArrayList<WikiPageNode> collectHeaderNodes(){
		ArrayList<WikiPageNode> headers = new ArrayList<WikiPageNode>();
		collectHeaderNodes(rootNode, headers);
		return headers;
	}
	
	/**
	 * Collects all leaf nodes of the entire tree
	 * 
	 * @return
	 */
	public ArrayList<WikiPageNode> collectLeafNodes(){
		ArrayList<WikiPageNode> leaves = new ArrayList<WikiPageNode>();
		collectLeafNodes(rootNode, leaves);
		return leaves;
	}
	
	/**
	 * Returns the depth of the given node. Returns -1 if the node is not in this tree.
	 * 
	 * @param node
	 * @return
	 */
	public int getDepth(WikiPageNode node){
		return getDepth(node, rootNode, 0);
	}
	
	/**
	 * Returns HTML code to render the text that survived the conversion from
	 * .html document to WikiPageTree
	 * 
	 * @return
	 */
	public String getHtml(){
		return getHtmlRecursion(rootNode, "");
	}
	
	/**
	 * Returns a list of all nodes at the given depth. It is assumed that the root
	 * node is at depth = 0
	 * 
	 * @param depth
	 * @return
	 */
	public ArrayList<WikiPageNode> getNodesAtDepth(int depth){
		ArrayList<WikiPageNode> nodes = new ArrayList<WikiPageNode>();
		getNodesAtDepth(depth, 0, rootNode, nodes);
		return nodes;
	}
	
	/**
	 * Returns this tree's root node
	 * 
	 * @return
	 */
	public WikiPageNode getRoot(){
		return rootNode;
	}
	
	/**
	 * Prints a description of the tree to System.out
	 */
	public void printTree(){
		printTreeRecursion(rootNode, "");
	}
	
	/**
	 * Collects all header nodes below the given node
	 * 
	 * @param node
	 * @param leaves The ArrayList to store new leaves in
	 */
	private void collectHeaderNodes(WikiPageNode node, ArrayList<WikiPageNode> headers){
		if(node.getNodeType() == NodeTypes.Header){
			headers.add(node);
			
			for(WikiPageNode child : node.children()){
				collectHeaderNodes(child, headers);
			}
		}
	}
	
	/**
	 * Collects all leaf nodes below the given node
	 * 
	 * @param node
	 * @param leaves The ArrayList to store new leaves in
	 */
	private void collectLeafNodes(WikiPageNode node, ArrayList<WikiPageNode> leaves){
		if(node.getNodeType() == NodeTypes.Header){
			for(WikiPageNode child : node.children()){
				collectLeafNodes(child, leaves);
			}
		}
		else{
			leaves.add(node);
		}
	}
	
	private int getDepth(WikiPageNode targetNode, WikiPageNode subtreeRoot, int currentDepth){
		if(targetNode == subtreeRoot){
			return currentDepth;
		}
		else{
			for(WikiPageNode child : subtreeRoot.children()){
				int depth = getDepth(targetNode, child, currentDepth + 1);
				
				if(depth >= 0){
					return depth;
				}
			}
			
			return -1;
		}
	}
	
	private String getHtmlRecursion(WikiPageNode node, String textSoFar){
		NodeTypes nodeType = node.getNodeType();
		Element nodeElement = node.getElement();
		
		textSoFar += nodeElement.outerHtml();
		if(nodeType == NodeTypes.Header){
			ArrayList<WikiPageNode> children = node.children();
			
			for(WikiPageNode child : children){
				textSoFar = getHtmlRecursion(child, textSoFar);
			}
		}

		return textSoFar;
	}
	
	private void getNodesAtDepth(int targetDepth, int currentDepth, WikiPageNode subtreeRoot, ArrayList<WikiPageNode> output){
		if(currentDepth == targetDepth){
			output.add(subtreeRoot);
		}
		else{
			for(WikiPageNode child : subtreeRoot.children()){
				getNodesAtDepth(targetDepth, currentDepth + 1, child, output);
			}
		}
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
