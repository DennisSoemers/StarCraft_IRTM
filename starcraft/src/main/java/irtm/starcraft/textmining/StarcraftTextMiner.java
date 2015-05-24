package irtm.starcraft.textmining;

import irtm.starcraft.game.StarcraftBuildOrder;
import irtm.starcraft.game.StarcraftKnowledgeBase;
import irtm.starcraft.game.StarcraftStrategy;
import irtm.starcraft.utils.HtmlUtils;
import irtm.starcraft.utils.WikiPageNode;
import irtm.starcraft.utils.WikiPageNode.ContentTypes;
import irtm.starcraft.utils.WikiPageNode.NodeTypes;
import irtm.starcraft.utils.WikiPageTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

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
		StarcraftStrategy strategy = new StarcraftStrategy(documentTree.getRoot().getElement().text());
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
		    if(nodeType == NodeTypes.List || nodeType == NodeTypes.Text){
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
		    	
		    	if(nodeType == NodeTypes.List){
		    		// list nodes can also have descriptive text (not just headers)
		    		
		    		ArrayList<Element> descriptiveText = leaf.getDescriptiveText();
			    	for(Element textElement : descriptiveText){
			    		String s = textElement.text().trim().toLowerCase();
			    		//System.out.println("TEXT: " + s + " DESCRIBES NODE: " + leaf.getElement().text());
			    		
			    		// also need to check text for these terms for wiki pages such as 
			    		// http://wiki.teamliquid.net/starcraft/1_Fact_FE_%28vs._Terran%29
			    		foundTermSoft = foundTermSoft || s.contains(TERM_SOFT);
			    		foundTermHard = foundTermHard || s.contains(TERM_HARD);
			    	}
		    	}
		    	
		    	// for text nodes, we currently only look for strong/weak maps and nothing else
		    	if(nodeType == NodeTypes.Text){
		    		foundTermCounter = false;
			    	foundTermCountered = false;
			    	foundTermSoft = false;
			    	foundTermHard = false;
			    	foundTermBy = false;
			    	foundTermTo = false;
		    	}
		    	
		    	if(foundTermMap){
		    		if(foundTermStrong){
		    			System.out.println("Classifying STRONG MAPS list: [" + htmlElement.text() + "]");
		    			leaf.setContentType(ContentTypes.StrongMaps);
		    		}
		    		else if(foundTermWeak){
		    			System.out.println("Classifying WEAK MAPS list: [" + htmlElement.text() + "]");
		    			leaf.setContentType(ContentTypes.WeakMaps);
		    		}
		    		else{
		    			System.err.println("DONT KNOW HOW TO CLASSIFY MAPS list: [" + htmlElement.text() + "]");
		    		}
		    	}
		    	else if(foundTermCountered && foundTermBy){
		    		if(foundTermSoft){
		    			System.out.println("Classifying COUNTERED BY SOFT list: [" + htmlElement.text() + "]");
		    			leaf.setContentType(ContentTypes.CounteredBySoft);
		    		}
		    		else if(foundTermHard){
		    			System.out.println("Classifying COUNTERED BY HARD list: [" + htmlElement.text() + "]");
		    			leaf.setContentType(ContentTypes.CounteredByHard);
		    		}
		    		else{
		    			System.err.println("DONT KNOW HOW TO CLASSIFY COUNTERED BY list: [" + htmlElement.text() + "]");
		    		}
		    	}
		    	else if(foundTermCounter && foundTermTo){
		    		if(foundTermSoft){
		    			System.out.println("Classifying COUNTER TO SOFT list: [" + htmlElement.text() + "]");
		    			leaf.setContentType(ContentTypes.CounterToSoft);
		    		}
		    		else if(foundTermHard){
		    			System.out.println("Classifying COUNTER TO HARD list: [" + htmlElement.text() + "]");
		    			leaf.setContentType(ContentTypes.CounterToHard);
		    		}
		    		else{
		    			System.err.println("DONT KNOW HOW TO CLASSIFY COUNTER TO list: [" + htmlElement.text() + "]");
		    		}
		    	}
		    	else if(nodeType == NodeTypes.List){	// currently only HTML lists can be build orders
		    		System.out.println("Classifying BUILD ORDER list: [" + htmlElement.text() + "]");
		    		leaf.setContentType(ContentTypes.BuildOrder);
		    	}
		    	
		    	// add info from the leaf lists into the strategy according to content type
		    	ContentTypes leafContentType = leaf.getContentType();
		    	if(leafContentType == ContentTypes.BuildOrder){
		    		strategy.addBuildOrder(new StarcraftBuildOrder(leaf, leafNodeAnnotations.get(leaf)));
		    	}
		    	else{
		    		ArrayList<Element> elements = new ArrayList<Element>();
		    		if(nodeType == NodeTypes.List){
		    			for(Element listElement : leaf.getElement().children()){
		    				elements.add(listElement);
		    			}
		    		}
		    		else if(nodeType == NodeTypes.Text){
		    			elements.add(leaf.getElement());
		    		}
		    		
		    		for(Element element : elements){
		    			String listElementText = element.text();
		    			
		    			if(leafContentType == ContentTypes.CounteredByHard)
		    			{
		    				strategy.addCounteredByHard(listElementText);
		    			}
		    			else if(leafContentType == ContentTypes.CounteredBySoft){
		    				strategy.addCounteredBySoft(listElementText);
		    			}
		    			else if(leafContentType == ContentTypes.CounterToHard){
		    				strategy.addCounterToHard(listElementText);
		    			}
		    			else if(leafContentType == ContentTypes.CounterToSoft){
		    				strategy.addCounterToSoft(listElementText);
		    			}
		    			else if(leafContentType == ContentTypes.StrongMaps){
		    				ArrayList<String> strongMapNames = extractMapNames(listElementText, leafNodeAnnotations.get(leaf));
		    				
		    				for(String mapName : strongMapNames){
		    					strategy.addStrongMap(mapName);
		    				}
		    			}
		    			else if(leafContentType == ContentTypes.WeakMaps){
		    				ArrayList<String> weakMapNames = extractMapNames(listElementText, leafNodeAnnotations.get(leaf));
		    				
		    				for(String mapName : weakMapNames){
		    					strategy.addWeakMap(mapName);
		    				}
		    			}
		    		}
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
		
		return strategy;
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
	 * Extracts all map names that can be matched to known maps in the knowledge base
	 * from the given text.
	 * 
	 * @param text
	 * @param annotation
	 * @return
	 */
	public ArrayList<String> extractMapNames(String text, Annotation annotation){
		ArrayList<String> mapNames = new ArrayList<String>();
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		
		for(CoreMap sentence : sentences) {
			//System.out.println("NEW SENTENCE");
	    	List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
	    	
	    	for(int i = 0; i < tokens.size(); ++i){
	    		//System.out.println("TOKEN: " + tokens.get(i).get(TextAnnotation.class));
	    		
	    		// construct 1-gram, 2-gram, ... up to 5-gram
	    		String[] nGrams = new String[5];
	    		int numExtraTokens = 0;
	    		String nGram = "";
	    		
	    		while(i + numExtraTokens < tokens.size()){
	    			nGram += tokens.get(i + numExtraTokens).get(TextAnnotation.class) + " ";
	    			nGrams[numExtraTokens] = nGram;
	    			++numExtraTokens;
	    			
	    			if(numExtraTokens == 5){	// don't want more than 5 tokens
	    				break;
	    			}
	    		}
	    		
	    		for(int n = 0; n < numExtraTokens - 1; ++n){
	    			// try the n-gram
	    			if(StarcraftKnowledgeBase.isMapName(nGrams[n])){
	    				mapNames.add(nGrams[n]);
	    				//System.out.println("SUCCESFUL " + (n + 1) + "-gram: " + nGrams[n]);
	    				
	    				// when succesful for n, we can immediately skip the next n tokens since they won't be part of another map name
	    				i += n;
	    				break;
	    			}
	    		}
	    	}
	    }
		
		return mapNames;
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
