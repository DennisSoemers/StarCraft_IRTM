package irtm.starcraft.textmining;

import irtm.starcraft.game.StarcraftBuildOrder;
import irtm.starcraft.game.StarcraftKnowledgeBase;
import irtm.starcraft.game.StarcraftStrategy;
import irtm.starcraft.utils.HtmlUtils;
import irtm.starcraft.utils.NlpUtils;
import irtm.starcraft.utils.WikiPageNode;
import irtm.starcraft.utils.WikiPageNode.ContentTypes;
import irtm.starcraft.utils.WikiPageNode.NodeTypes;
import irtm.starcraft.utils.WikiPageTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

/**
 * Class that performs the required text mining (and information retrieval) to
 * analyze a file describing a StarCraft strategy and convert it into a well-structured
 * Java object.
 * 
 * @author Dennis Soemers
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
	private final String TERM_BUILD = "build";
	
	/** The last strategy that was found by processing files */
	private StarcraftStrategy lastStrategy;
	/** The last document tree that was found by processing files */
	private WikiPageTree lastDocumentTree;
	
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
			System.err.println("Rejecting badly formatted wiki page: " + file.getAbsolutePath() + "!");
			return null;
		}
		
		lastDocumentTree = new WikiPageTree(relevantElements);
		lastStrategy = new StarcraftStrategy(lastDocumentTree.getRoot().getElement().text());
		//lastDocumentTree.printTree();
		
		// count how many map / counter to / countered by headers we have. If only 1, it will apply to all build orders
		int numMapHeaders = 0;
		int numCounteredByHeaders = 0;
		int numCounterToHeaders = 0;
		
		for(WikiPageNode node : lastDocumentTree.collectHeaderNodes()){
			String text = node.getElement().text().trim().toLowerCase();
			
			if(text.contains(TERM_MAP)){
				++numMapHeaders;
			}
			else if(text.contains(TERM_COUNTERED)){
				++numCounteredByHeaders;
			}
			else if(text.contains(TERM_COUNTER)){
				++numCounterToHeaders;
			}
		}
	    
	    ArrayList<WikiPageNode> leafNodes = lastDocumentTree.collectLeafNodes();
	    HashMap<WikiPageNode, Annotation> leafNodeAnnotations = new HashMap<WikiPageNode, Annotation>();
	    
	    // first we loop through all leaf nodes and try to classify what type of content they contain
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
		    NlpUtils.getNlpPipeline().annotate(document);
		    
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
		    	boolean foundTermBuild = false;
		    	
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
		    		foundTermBuild = foundTermBuild || headerText.contains(TERM_BUILD);
		    	}
		    	
		    	if(nodeType == NodeTypes.List){
		    		// list nodes can also have descriptive text (not just headers)
		    		
		    		ArrayList<Element> descriptiveText = leaf.getDescriptiveText();
			    	for(Element textElement : descriptiveText){
			    		String s = textElement.text().trim().toLowerCase();
			    		
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
		    			leaf.setContentType(ContentTypes.StrongMaps);
		    		}
		    		else if(foundTermWeak){
		    			leaf.setContentType(ContentTypes.WeakMaps);
		    		}
		    		else{
		    			// not printing the error here, sometimes there simply isn't a clear list of strong or weak maps
		    			// (see: http://wiki.teamliquid.net/starcraft/2_Port_Wraith )
		    			//System.err.println("DONT KNOW HOW TO CLASSIFY MAPS list: [" + htmlElement.text() + "]");
		    		}
		    	}
		    	else if(foundTermCountered && foundTermBy){
		    		if(foundTermSoft){
		    			leaf.setContentType(ContentTypes.CounteredBySoft);
		    		}
		    		else if(foundTermHard){
		    			leaf.setContentType(ContentTypes.CounteredByHard);
		    		}
		    		else{
		    			// simply gonna assume it's a soft counter if not specified
		    			leaf.setContentType(ContentTypes.CounteredBySoft);
		    		}
		    	}
		    	else if(foundTermCounter && foundTermTo){
		    		if(foundTermSoft){
		    			leaf.setContentType(ContentTypes.CounterToSoft);
		    		}
		    		else if(foundTermHard){
		    			leaf.setContentType(ContentTypes.CounterToHard);
		    		}
		    		else{
		    			// simply gonna assume it's a soft counter if not specified
		    			leaf.setContentType(ContentTypes.CounterToSoft);
		    		}
		    	}
		    	else if(nodeType == NodeTypes.List && foundTermBuild){	// currently only HTML lists can be build orders
		    		leaf.setContentType(ContentTypes.BuildOrder);
		    	}
		    }
	    }
	    
	    // now we loop through all leaf nodes a second them, and use the information we've gathered
	    // on them to build a strategy
	    StarcraftBuildOrder lastBuildOrder = null;
	    for(WikiPageNode leaf : leafNodes){
	    	NodeTypes nodeType = leaf.getNodeType();
	    	
	    	// add info from the leaf lists into the strategy according to content type
	    	ContentTypes leafContentType = leaf.getContentType();
	    	if(leafContentType == ContentTypes.BuildOrder){
	    		lastBuildOrder = new StarcraftBuildOrder(leaf, leafNodeAnnotations.get(leaf));
	    		lastStrategy.addBuildOrder(lastBuildOrder);
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
	    			
	    			// not very interesting to add ''None'' if there are no counters or strong/weak maps
	    			if(listElementText.toLowerCase().trim().equals("none")){
	    				continue;
	    			}
	    			
	    			if(leafContentType == ContentTypes.CounteredByHard)
	    			{
	    				ArrayList<String> counters = extractStrategyNames(listElementText);
	    				
	    				if(numCounteredByHeaders == 1 || lastBuildOrder == null){		// add counters to entire strategy
	    					for(String strategyName : counters){
	    						lastStrategy.addCounteredByHard(strategyName);
		    				}
	    				}
	    				else{								// add counters to last build order
	    					for(String strategyName : counters){
		    					lastBuildOrder.addCounteredByHard(strategyName);
		    				}
	    				}
	    			}
	    			else if(leafContentType == ContentTypes.CounteredBySoft){
	    				ArrayList<String> counters = extractStrategyNames(listElementText);
	    				
	    				if(numCounteredByHeaders == 1 || lastBuildOrder == null){		// add counters to entire strategy
	    					for(String strategyName : counters){
	    						lastStrategy.addCounteredBySoft(strategyName);
		    				}
	    				}
	    				else{								// add counters to last build order
	    					for(String strategyName : counters){
		    					lastBuildOrder.addCounteredBySoft(strategyName);
		    				}
	    				}
	    			}
	    			else if(leafContentType == ContentTypes.CounterToHard){
	    				ArrayList<String> counters = extractStrategyNames(listElementText);
	    				
	    				if(numCounterToHeaders == 1 || lastBuildOrder == null){	// add counters to entire strategy
	    					for(String strategyName : counters){
	    						lastStrategy.addCounterToHard(strategyName);
		    				}
	    				}
	    				else{							// add counters to last build order
	    					for(String strategyName : counters){
		    					lastBuildOrder.addCounterToHard(strategyName);
		    				}
	    				}
	    			}
	    			else if(leafContentType == ContentTypes.CounterToSoft){
	    				ArrayList<String> counters = extractStrategyNames(listElementText);
	    				
	    				if(numCounterToHeaders == 1 || lastBuildOrder == null){	// add counters to entire strategy
	    					for(String strategyName : counters){
	    						lastStrategy.addCounterToSoft(strategyName);
		    				}
	    				}
	    				else{							// add counters to last build order
	    					for(String strategyName : counters){
		    					lastBuildOrder.addCounterToSoft(strategyName);
		    				}
	    				}
	    			}
	    			else if(leafContentType == ContentTypes.StrongMaps){
	    				ArrayList<String> strongMapNames = extractMapNames(listElementText);
	    				
	    				if(numMapHeaders == 1 || lastBuildOrder == null){		// add maps to entire strategy
	    					for(String mapName : strongMapNames){
	    						lastStrategy.addStrongMap(mapName);
		    				}
	    				}
	    				else{						// add maps to last build order
	    					for(String mapName : strongMapNames){
		    					lastBuildOrder.addStrongMap(mapName);
		    				}
	    				}
	    			}
	    			else if(leafContentType == ContentTypes.WeakMaps){
	    				ArrayList<String> weakMapNames = extractMapNames(listElementText);
	    				
	    				if(numMapHeaders == 1 || lastBuildOrder == null){		// add maps to entire strategy
	    					for(String mapName : weakMapNames){
	    						lastStrategy.addWeakMap(mapName);
		    				}
	    				}
	    				else{						// add maps to last build order
	    					for(String mapName : weakMapNames){
		    					lastBuildOrder.addWeakMap(mapName);
		    				}
	    				}
	    			}
	    		}
	    	}
	    }
		
		return lastStrategy;
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
	 * @return
	 */
	public ArrayList<String> extractMapNames(String text){
	    Annotation annotation = new Annotation(text);
	    NlpUtils.getNlpPipeline().annotate(annotation);
		
		ArrayList<String> mapNames = new ArrayList<String>();
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		
		for(CoreMap sentence : sentences) {
	    	List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
	    	ArrayList<String> tokenSequence = new ArrayList<String>();
	    	
	    	for(CoreLabel token : tokens){
	    		tokenSequence.add(token.get(TextAnnotation.class));
	    	}
	    	
	    	// use at most 5 tokens to recognize a single map name
	    	ArrayList<String> nGrams = NlpUtils.extractNGrams(1, 5, tokenSequence);
	    	
	    	for(String nGram : nGrams){
	    		if(StarcraftKnowledgeBase.isMapName(nGram)){
	    			mapNames.add(nGram);
	    		}
	    	}
	    }
		
		return mapNames;
	}

	/**
	 * Attempts to extract all strategy names from the given String.
	 * Currently very much heuristic-based and assumes the given Strings already contain
	 * almost only strategy names
	 * 
	 * @param text
	 * @param annotation
	 * @return
	 */
	public ArrayList<String> extractStrategyNames(String text){
		ArrayList<String> strategyNames = new ArrayList<String>();
		
		// first split on whitespace and replace any digits written in text by their numeric variants
		String[] tokens = text.split(" ");
		text = "";
		
		for(String token : tokens){
			text += NlpUtils.textDigitToNumeric(token) + " ";
		}
		
		text = text.trim();
		
		// these tokens are likely to be in between different strategy names
		String[] splitTokens = {", ", " or ", " and ", "/"};
		
		// first make sure there's properly a space after every comma
		for(int i = 0; i < text.length(); ++i){
			if(text.substring(i, i+1).equals(",") && (i + 1 == text.length() || !text.substring(i, i+2).equals(", "))){
				text = text.substring(0, i) + ", " + text.substring(i+1, text.length());
			}
		}
		
		for(int i = 1; i < splitTokens.length; ++i){
			text = text.replaceAll(splitTokens[i], splitTokens[0]);
		}
		
		String[] strategyTokenSequences = text.split(splitTokens[0]);
		for(int i = 0; i < strategyTokenSequences.length; ++i){
			String strategyTokenSequence = strategyTokenSequences[i];
			
			// split up every sequence of tokens once more so we can get rid of those that:
			//	- dont start with a number AND
			//	- dont start with a capital letter
			//
			// We'll also simply get rid of sequences where we had to remove 2 or more words in a row
			String[] strategyTokens = strategyTokenSequence.split(" ");
			strategyTokenSequence = "";
			int removalCounter = 0;
			for(String token : strategyTokens){
				if(token.equals("")){
					continue;
				}
				
				if(StringUtils.isCapitalized(token) || StringUtils.isNumeric(token.substring(0, 1))){
					strategyTokenSequence += token + " ";
					removalCounter = 0;
				}
				else{
					++removalCounter;
					
					if(removalCounter >= 2 && !strategyTokenSequence.equals("")){
						break;
					}
				}
			}
			
			strategyTokenSequence = strategyTokenSequence.trim();
			
			if(i < strategyTokenSequences.length - 1){
				try{
					int increment = 1;
					
					// deal with cases like http://wiki.teamliquid.net/starcraft/1_Fact_FE_%28vs._Terran%29
					// where ''One or two Starport Wraiths'' should be converted to two separate strategy names
					while(true){	// need the loop in case there's x/y/z STRATEGY with x, y and z all numeric
						if(i + increment >= strategyTokenSequences.length){
							break;
						}
						
						Integer.parseInt(strategyTokenSequence);
						String nextTokenSequence = strategyTokenSequences[i + increment];
						int secondNumber = Integer.parseInt(nextTokenSequence.split(" ")[0]);
						
						// if we didn't catch any exceptions yet, it means we have a case like the above example
						if(StringUtils.isNumeric(nextTokenSequence)){	// another number, so keep looping
							++increment;
							continue;
						}
						
						nextTokenSequence = nextTokenSequence.substring(String.valueOf(secondNumber).length() + 1);
						
						// split up every sequence of tokens once more so we can get rid of those that:
						//	- dont start with a number AND
						//	- dont start with a capital letter
						String[] nextSequenceStrategyTokens = nextTokenSequence.split(" ");
						nextTokenSequence = "";
						for(String token : nextSequenceStrategyTokens){
							if(StringUtils.isCapitalized(token) || StringUtils.isNumeric(token.substring(0, 1))){
								nextTokenSequence += token + " ";
							}
						}
						nextTokenSequence = nextTokenSequence.trim();
						
						// put the number of the first part in front of the non-numeric second part
						strategyTokenSequence = strategyTokenSequence + " " + nextTokenSequence;
						
						// no longer need to continue the loop
						break;
					}
				}
				catch(NumberFormatException exception){/**/}
			}
			
			if(!strategyTokenSequence.equals("")){
				strategyNames.add(strategyTokenSequence);
			}
		}
		
		return strategyNames;
	}
	
	/**
	 * Returns the last document tree that was computed whilst processing files
	 * 
	 * @return
	 */
	public WikiPageTree getLastDocumentTree(){
		return lastDocumentTree;
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
