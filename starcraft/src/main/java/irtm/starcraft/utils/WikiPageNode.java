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
	 * Enum of the different types of content we expect to be able to find on wiki pages
	 *
	 * @author Dennis Soemers
	 */
	public enum ContentTypes{
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
	private ContentTypes contentType;
	
	public WikiPageNode(WikiPageNode parentNode, NodeTypes nodeType, Element element){
		this.parentNode = parentNode;
		childNodes = new ArrayList<WikiPageNode>();
		this.nodeType = nodeType;
		this.element = element;
		this.contentType = ContentTypes.Unknown;
	}
	
	public void addChild(WikiPageNode newChild){
		childNodes.add(newChild);
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
	 * Returns the node's content type
	 * 
	 * @return
	 */
	public ContentTypes getContentType(){
		return contentType;
	}
	
	/**
	 * Returns a list of header elements that describe this node
	 * 
	 * @return
	 */
	public ArrayList<Element> getDescriptiveHeaders(){
		ArrayList<Element> descriptiveHeaders = new ArrayList<Element>();
		
		// simply follow chain of parents up
		WikiPageNode parent = parentNode;
		while(parent != null){
			if(parent.getNodeType() == NodeTypes.Header){
				descriptiveHeaders.add(parent.getElement());
			}
			
			parent = parent.parent();
		}
		
		return descriptiveHeaders;
	}
	
	/**
	 * Returns a list of text elements that describe this node
	 * 
	 * @return
	 */
	public ArrayList<Element> getDescriptiveText(){
		ArrayList<Element> descriptiveText = new ArrayList<Element>();
		
		if(nodeType != NodeTypes.List){
			System.err.println("Descriptive text undefined for nodes other than list nodes");
		}
		else if(parentNode != null){
			boolean foundOtherList = false;
			boolean foundSelf = false;
			ArrayList<WikiPageNode> siblings = parentNode.children();
			ArrayList<Element> tentativeDescriptiveText = new ArrayList<Element>();
			
			for(int i = 0; i < siblings.size(); ++i){
				WikiPageNode sibling = siblings.get(i);
				
				if(sibling.getNodeType() == NodeTypes.Header){
					// TODO gather all text under this header?
				}
				else if(sibling == this){
					foundSelf = true;
					foundOtherList = false;
				}
				else{
					if(!foundOtherList){
						foundOtherList = (sibling.getNodeType() == NodeTypes.List);
						
						if(foundOtherList && !foundSelf){
							// found another list before finding this node, so remove all previous descriptive text again
							descriptiveText.clear();
							foundOtherList = false;
						}
					}
					
					if(sibling.getNodeType() == NodeTypes.Text){
						if(!foundSelf){
							descriptiveText.add(sibling.getElement());
						}
						else if(!foundOtherList){
							tentativeDescriptiveText.add(sibling.getElement());
						}
					}
				}
			}
			
			if(!foundOtherList){	// found no list after this node, so add any text after this node
				descriptiveText.addAll(tentativeDescriptiveText);
			}
		}		
		
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
	 * Sets the node's content type to the given new type
	 * 
	 * @param newType
	 */
	public void setContentType(ContentTypes newType){
		contentType = newType;
	}

}
