package irtm.starcraft.starcraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
		ArrayList<String> relevantText = new ArrayList<String>();
		collectRelevantElements(htmlDocument, relevantText);
		
		// veryify that all the special cases that need to be found and removed were indeed found
		if(!categoryLinksFound || !footerFound || !leftColumnFound || !tableOfContentsFound){
			System.err.println("Did not find all of the required special cases! (" + 
								categoryLinksFound + ", " + footerFound + ", " + leftColumnFound + ", " + tableOfContentsFound + ")");
		}
		
		for(String text : relevantText){
			System.out.println(text);
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
	public void collectRelevantElements(Element element, ArrayList<String> collection){		
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
			collection.add(element.tagName() + ": " + element.text());
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
				tagName.equals("h1") ||
				tagName.equals("h2") ||
				tagName.equals("h3") ||
				tagName.equals("h4") ||
				tagName.equals("h5") ||
				tagName.equals("h6") ||
				tagName.equals("p") ||
				tagName.equals("ul")
				);
	}
}
