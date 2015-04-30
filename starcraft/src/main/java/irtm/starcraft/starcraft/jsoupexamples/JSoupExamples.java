package irtm.starcraft.starcraft.jsoupexamples;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Example code of the JSoup library for parsing HTML
 * 
 * Useful links:
 * 	- Parsing only a fragment of a body instead of a full HTML file: http://jsoup.org/cookbook/input/parse-body-fragment
 * 	- Directly loading HTML from internet instead of from a .txt file: http://jsoup.org/cookbook/input/load-document-from-url
 * 	- Finding data when structure of HTML is known: http://jsoup.org/cookbook/extracting-data/dom-navigation
 * 	- Selecting all elements that satisfy some conditions: http://jsoup.org/cookbook/extracting-data/selector-syntax
 * 	- Extract data from elements: http://jsoup.org/cookbook/extracting-data/attributes-text-html
 * 
 * @author Soemers
 *
 */
public class JSoupExamples {
	
	public static void main(String[] args) throws IOException{
		// let JSoup load file and parse the HTML in it
		File inputFile = new File("corpus/TerranVsProtoss/One Factory Double Expand.txt");
		Document document = Jsoup.parse(inputFile, "UTF-8", "http://wiki.teamliquid.net");
		//Document document = Jsoup.connect("http://wiki.teamliquid.net/starcraft/1_Factory_Double_Expand_%28vs._Protoss%29").get();
		
		printAllText(document);
	}
	
	/**
	 * Example code that prints a list of all the links found in the given document.
	 * Based on: http://jsoup.org/cookbook/extracting-data/example-list-links
	 * 
	 * @param document
	 */
	public static void printAllLinks(Document document){
		Elements links = document.select("link[href]");
		
		for(Element link : links){
			System.out.println("<" + link.attr("abs:href") + "> (" + link.text() + ")");
		}
	}
	
	public static void printAllTables(Document document){
		Elements tables = document.select("table");
		
		for(Element table : tables){
			System.out.println(table.text());
		}
	}
	
	public static void printAllChildren(Element element){
		Elements children = element.children();
		
		for(Element child : children){
			System.out.println(child.tagName());
		}
	}
	
	public static void printAllText(Element element){
		Elements children = element.children();
		
		for(Element child : children){
			if(child.tagName().equals("p")){
				System.out.println(child.text());
			}
			else{
				printAllText(child);
			}
		}
	}

}
