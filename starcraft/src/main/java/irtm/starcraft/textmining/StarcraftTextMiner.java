package irtm.starcraft.textmining;

import irtm.starcraft.game.StarcraftBuildOrder;
import irtm.starcraft.game.StarcraftStrategy;
import irtm.starcraft.utils.HtmlUtils;
import irtm.starcraft.utils.WikiPageNode;
import irtm.starcraft.utils.WikiPageNode.ListTypes;
import irtm.starcraft.utils.WikiPageNode.NodeTypes;
import irtm.starcraft.utils.WikiPageTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Class that performs the required text mining (and information retrieval) to
 * analyze a file describing a StarCraft strategy and convert it into a well-structured
 * Java object.
 * 
 * @author Soemers
 *
 */
public class StarcraftTextMiner{
	
	// variables to keep track of whether these special cases were already found and removed
	// this is important because we need to be sure that both of these elements are found EXACTLY once per document
	private boolean categoryLinksFound;
	private boolean footerFound;
	private boolean leftColumnFound;
	private boolean tableOfContentsFound;
	
	// some important terms that we need to compare to often
	private final String TERM_MAP = "map";
	private final String TERM_STRONG = "strong";
	private final String TERM_WEAK = "weak";
	private final String TERM_COUNTER = "counter";
	private final String TERM_COUNTERED = "countered";
	private final String TERM_SOFT = "soft";
	private final String TERM_HARD = "hard";
	private final String TERM_BY = "by";
	private final String TERM_TO = "to";
	
	public StarcraftTextMiner(){
	}
	
	/**
	 * Processes the given file to construct a single Starcraft Strategy object from
	 * the text in the file, and returns the constructed strategy.
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public StarcraftStrategy processFile(File file) throws IOException{
		// reset status variables
		categoryLinksFound = false;
		footerFound = false;
		leftColumnFound = false;
		tableOfContentsFound = false;
		
		// gonna assume for now that all files originate from http://wiki.teamliquid.net
		Document htmlDocument = Jsoup.parse(file, "UTF-8", "http://wiki.teamliquid.net");
		ArrayList<Element> relevantElements = new ArrayList<Element>();
		collectRelevantElements(htmlDocument, relevantElements);
		
		// veryify that all the special cases that need to be found and removed were indeed found
		if(!categoryLinksFound || !footerFound || !leftColumnFound || !tableOfContentsFound){
			System.err.println("Did not find all of the required special cases! (" + 
								categoryLinksFound + ", " + footerFound + ", " + leftColumnFound + ", " + tableOfContentsFound + ")");
		}
		
		WikiPageTree documentTree = new WikiPageTree(relevantElements);
		//documentTree.printTree();
		
		// initialize Stanford NLP pipeline
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit"/*, pos, lemma, ner"*/);
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    ArrayList<WikiPageNode> leafNodes = documentTree.collectLeafNodes();
	    HashMap<WikiPageNode, Annotation> leafNodeAnnotations = new HashMap<WikiPageNode, Annotation>();
	    for(WikiPageNode leaf : leafNodes){
	    	// run stanford NLP pipeline on every leaf node's text
	    	Element htmlElement = leaf.getElement();
	    	NodeTypes nodeType = leaf.getNodeType();
	    	String text = "";
	    	
	    	if(nodeType == NodeTypes.Text){	// can simply directly take the text
	    		text = htmlElement.text();
	    	}
	    	else if(nodeType == NodeTypes.List){	// construct a proper list String consisting of multiple lines
	    		Elements listElements = htmlElement.children();
	    		
	    		for(Element listElement : listElements){
	    			text += listElement.text() + ".\n";		// adding the '.' allows stanford NLP parser to recognize every element as a sentence
	    		}
	    	}
	    	
	    	// create an empty Annotation just with the given text
		    Annotation document = new Annotation(text);
		    
		    // run all Annotators on this text
		    pipeline.annotate(document);
		    
		    // store the annotations
		    leafNodeAnnotations.put(leaf, document);
		    
		    // in the case of leaf nodes, use some simple rules to try to classify the list
		    if(nodeType == NodeTypes.List){
		    	boolean foundTermMap = false;
		    	boolean foundTermStrong = false;
		    	boolean foundTermWeak = false;
		    	boolean foundTermCounter = false;
		    	boolean foundTermCountered = false;
		    	boolean foundTermSoft = false;
		    	boolean foundTermHard = false;
		    	boolean foundTermBy = false;
		    	boolean foundTermTo = false;
		    	
		    	ArrayList<Element> descriptiveHeaders = leaf.getDescriptiveHeaders();
		    	
		    	for(Element header : descriptiveHeaders){
		    		String headerText = header.text().trim().toLowerCase();
		    		
		    		foundTermMap = foundTermMap || headerText.contains(TERM_MAP);
		    		foundTermStrong = foundTermStrong || headerText.contains(TERM_STRONG);
		    		foundTermWeak = foundTermWeak || headerText.contains(TERM_WEAK);
		    		foundTermCounter = foundTermCounter || headerText.contains(TERM_COUNTER);
		    		foundTermCountered = foundTermCountered || headerText.contains(TERM_COUNTERED);
		    		foundTermSoft = foundTermSoft || headerText.contains(TERM_SOFT);
		    		foundTermHard = foundTermHard || headerText.contains(TERM_HARD);
		    		foundTermBy = foundTermBy || headerText.contains(TERM_BY);
		    		foundTermTo = foundTermTo || headerText.contains(TERM_TO);
		    	}
		    	
		    	if(foundTermMap){
		    		if(foundTermStrong){
		    			System.out.println("Classifying STRONG MAPS list: [" + htmlElement.text() + "]");
		    			leaf.setListType(ListTypes.StrongMaps);
		    		}
		    		else if(foundTermWeak){
		    			System.out.println("Classifying WEAK MAPS list: [" + htmlElement.text() + "]");
		    			leaf.setListType(ListTypes.WeakMaps);
		    		}
		    		else{
		    			System.err.println("DONT KNOW HOW TO CLASSIFY MAPS list: [" + htmlElement.text() + "]");
		    		}
		    	}
		    	else if(foundTermCountered && foundTermBy){
		    		if(foundTermSoft){
		    			System.out.println("Classifying COUNTERED BY SOFT list: [" + htmlElement.text() + "]");
		    			leaf.setListType(ListTypes.CounteredBySoft);
		    		}
		    		else if(foundTermHard){
		    			System.out.println("Classifying COUNTERED BY HARD list: [" + htmlElement.text() + "]");
		    			leaf.setListType(ListTypes.CounteredByHard);
		    		}
		    		else{
		    			System.err.println("DONT KNOW HOW TO CLASSIFY COUNTERED BY list: [" + htmlElement.text() + "]");
		    		}
		    	}
		    	else if(foundTermCounter && foundTermTo){
		    		if(foundTermSoft){
		    			System.out.println("Classifying COUNTER TO SOFT list: [" + htmlElement.text() + "]");
		    			leaf.setListType(ListTypes.CounterToSoft);
		    		}
		    		else if(foundTermHard){
		    			System.out.println("Classifying COUNTER TO HARD list: [" + htmlElement.text() + "]");
		    			leaf.setListType(ListTypes.CounterToHard);
		    		}
		    		else{
		    			System.err.println("DONT KNOW HOW TO CLASSIFY COUNTER TO list: [" + htmlElement.text() + "]");
		    		}
		    	}
		    	else{
		    		System.out.println("Classifying BUILD ORDER list: [" + htmlElement.text() + "]");
		    		leaf.setListType(ListTypes.BuildOrder);
		    	}
		    	
		    	if(leaf.getListType() == ListTypes.BuildOrder){		// we have a Build Order list, so create a build order
		    		StarcraftBuildOrder buildOrder = new StarcraftBuildOrder(leaf, leafNodeAnnotations.get(leaf));
		    	}
		    }
		    
		    // these are all the sentences in this document
		    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		    /*List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		    List<CoreLabel> tokens = new ArrayList<CoreLabel>();
		    
		    for(CoreMap sentence : sentences){
		    	tokens.addAll(sentence.get(TokensAnnotation.class));
		    }
		    
		    for(CoreMap sentence : sentences) {
		    	// traversing the words in the current sentence
		    	// a CoreLabel is a CoreMap with additional token-specific methods
		    	for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
		    		// this is the text of the token
		    		String word = token.get(TextAnnotation.class);
		    		System.out.print(word + " ");
		    	}
		    	
		    	System.out.println();
		    }*/
	    }
		
		return null;
	}
	
	/**
	 * Recursively collects all relevant elements from the given HTML element,
	 * and stores them as pure Strings in the given collection.
	 * 
	 * @param element
	 * @param collection
	 */
	public void collectRelevantElements(Element element, ArrayList<Element> collection){		
		// special case: we don't care about the category links
		if(element.tagName().equals("div") && element.id().equals("catlinks")){
			if(categoryLinksFound){
				System.err.println("Found more than one category links element!");
			}
			
			categoryLinksFound = true;
			return;
		}
		
		// special case: we don't care about the footer
		if(element.tagName().equals("div") && element.id().equals("footer")){
			if(footerFound){
				System.err.println("Found more than one footer element!");
			}
					
			footerFound = true;
			return;
		}
		
		// special case: we don't care about all the text in the left-hand column
		if(element.tagName().equals("div") && element.id().equals("column-one")){
			if(leftColumnFound){
				System.err.println("Found more than one left-hand column element!");
			}
			
			leftColumnFound = true;
			return;
		}
		
		// special case: we don't care about the table of contents
		if(element.tagName().equals("div") && element.id().equals("toc")){
			if(tableOfContentsFound){
				System.err.println("Found more than one table of contents element!");
			}
			
			tableOfContentsFound = true;
			return;
		}
		
		if(isRelevantElement(element)){
			String text = element.text();
			
			if(text.endsWith("[edit]")){		// get rid of the [edit] buttons on the wiki
				text = text.substring(0, text.length() - 6).trim();
				element.text(text);
			}
			
			if(!text.equals("")){		// no point in including empty-text elements				
				collection.add(element);
			}
		}
		
		for(Element child : element.children()){
			collectRelevantElements(child, collection);
		}
	}
	
	/**
	 * Returns true only for HTML elements that are relevant for information retrieval and text mining
	 * 
	 * @param element
	 * @return
	 */
	public boolean isRelevantElement(Element element){
		String tagName = element.tagName();
		
		return (
				HtmlUtils.isHeaderTag(tagName)	||
				HtmlUtils.isListTag(tagName)	||
				tagName.equals("p")
				);
	}
}
