package irtm.starcraft.utils;

import java.util.ArrayList;

import org.jsoup.nodes.Element;

/**
 * A node of a WikiPageTree
 * 
 * @author Dennis Soemers
 */
public class WikiPageNode {
	
	/**
	 * Enum of the different types of nodes that can exist in a WikiPageTree
	 * 
	 * @author Dennis Soemers
	 */
	public enum NodeTypes{
		Header,
		List,
		Text
	}
	
	/**
	 * Enum of the different types of lists we expect to be able to find on wiki pages
	 *
	 * @author Dennis Soemers
	 */
	public enum ListTypes{
		Unknown,
		BuildOrder,
		CounteredBySoft,
		CounteredByHard,
		CounterToSoft,
		CounterToHard,
		StrongMaps,
		WeakMaps
	}
	
	private WikiPageNode parentNode;
	private ArrayList<WikiPageNode> childNodes;
	private Element element;
	
	private NodeTypes nodeType;
	private ListTypes listType;
	
	/** Header elements that are above this node in the hierarchy and therefore describe this node */
	private ArrayList<Element> descriptiveHeaders;
	/** Text elements that add context to / describe this element in natural language */
	private ArrayList<Element> descriptiveText;
	
	public WikiPageNode(WikiPageNode parentNode, NodeTypes nodeType, Element element){
		this.parentNode = parentNode;
		childNodes = new ArrayList<WikiPageNode>();
		this.nodeType = nodeType;
		this.element = element;
		this.descriptiveHeaders = new ArrayList<Element>();
		this.descriptiveText = new ArrayList<Element>();
		this.listType = ListTypes.Unknown;
	}
	
	public void addChild(WikiPageNode newChild){
		childNodes.add(newChild);
	}
	
	public void addDescriptiveHeader(Element headerElement){
		descriptiveHeaders.add(headerElement);
	}
	
	public void addDescriptiveText(Element textElement){
		descriptiveText.add(textElement);
	}
	
	/**
	 * Returns the node's list of child nodes
	 * 
	 * @return
	 */
	public ArrayList<WikiPageNode> children(){
		return childNodes;
	}
	
	/**
	 * Returns the list of header elements that describe this node
	 * 
	 * @return
	 */
	public ArrayList<Element> getDescriptiveHeaders(){
		return descriptiveHeaders;
	}
	
	/**
	 * Returns the list of text elements that describe this node
	 * 
	 * @return
	 */
	public ArrayList<Element> getDescriptiveText(){
		return descriptiveText;
	}
	
	/**
	 * Returns the HTML element belonging to this node
	 * 
	 * @return
	 */
	public Element getElement(){
		return element;
	}
	
	/**
	 * Returns the node's list type
	 * 
	 * @return
	 */
	public ListTypes getListType(){
		return listType;
	}
	
	/**
	 * Returns this node's type
	 * 
	 * @return
	 */
	public NodeTypes getNodeType(){
		return nodeType;
	}
	
	/**
	 * Returns the node's parent node
	 * 
	 * @return
	 */
	public WikiPageNode parent(){
		return parentNode;
	}
	
	/**
	 * Sets the node's list type to the given new type
	 * 
	 * @param newType
	 */
	public void setListType(ListTypes newType){
		listType = newType;
	}

}
