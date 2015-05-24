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
	
	/**
	 * Traverses the tree in a depth-first manner and informs every element of which other elements add
	 * add important information/context to it.
	 * 
	 * @param node 
	 * @param headers
	 * @param text
	 */
	/*private void computeDescriptiveElements(WikiPageNode node, ArrayList<Element> headers, ArrayList<Element> text){
		NodeTypes nodeType = node.getNodeType();
		
		if(nodeType != NodeTypes.Header){	// let headers provide context to non-header nodes
			for(Element headerElement : headers){
				node.addDescriptiveHeader(headerElement);
			}
			
			if(nodeType == NodeTypes.List){		// also let text provide context to list nodes
				for(Element textElement : text){
					node.addDescriptiveText(textElement);
					System.out.println("TEXT: " + textElement.text() + " describes NODE: " + node.getElement().text());
				}
			}
		}
		
		if(nodeType == NodeTypes.Header){	// not a leaf node, so need to deal with everything below it
			// this header will be relevant to every element below it
			headers.add(node.getElement());
			
			// gather all text nodes on the next level
			int previousNumTextNodes = text.size();
			ArrayList<WikiPageNode> children = node.children();
			for(WikiPageNode child : children){
				if(child.getNodeType() == NodeTypes.Text){
					text.add(child.getElement());
				}
			}
			
			// recursively process all children
			for(WikiPageNode child : children){
				computeDescriptiveElements(child, headers, text);
			}
			
			// remove all text nodes gathered from the next level again
			while(text.size() > previousNumTextNodes){
				text.remove(text.size() - 1);
			}
			
			// going back up the tree now, so this header will no longer be relevant
			headers.remove(node.getElement());
		}
	}*/
	
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
