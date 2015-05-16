package irtm.starcraft.textmining;

import irtm.starcraft.game.StarcraftStrategy;
import irtm.starcraft.utils.HtmlUtils;
import irtm.starcraft.utils.WikiPageNode;
import irtm.starcraft.utils.WikiPageNode.NodeTypes;
import irtm.starcraft.utils.WikiPageTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
		documentTree.printTree();
		
		// work some Stanford NLP magic
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    ArrayList<WikiPageNode> leafNodes = documentTree.collectLeafNodes();
	    for(WikiPageNode leaf : leafNodes){
	    	// run stanford NLP pipeline on every leaf node's text
	    	Element htmlElement = leaf.getElement();
	    	String text = "";
	    	
	    	if(leaf.getNodeType() == NodeTypes.Text){	// can simply directly take the text
	    		text = htmlElement.text();
	    	}
	    	else if(leaf.getNodeType() == NodeTypes.List){	// construct a proper list String consisting of multiple lines
	    		Elements listElements = htmlElement.children();
	    		
	    		for(Element listElement : listElements){
	    			text += listElement.text() + "\n";
	    		}
	    	}
	    	
	    	// create an empty Annotation just with the given text
		    Annotation document = new Annotation(text);
		    
		    // run all Annotators on this text
		    pipeline.annotate(document);
		    
		    // these are all the sentences in this document
		    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
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
		    		//System.out.print(word + " ");
		    	}
		    	
		    	//System.out.println();
		    }
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
				HtmlUtils.isHeaderTag(tagName) ||
				tagName.equals("p") ||
				tagName.equals("ul") ||
				tagName.equals("ol")
				);
	}
}
