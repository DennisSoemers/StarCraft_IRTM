package irtm.starcraft.starcraft.stanfordnlpexamples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * Some example code to get used to the Stanford NLP libraries
 * Already using what I've learned from messing around with Jsoup in
 * the JSoupExamples class in order to obtain the text to be processed.
 * 
 * @author Soemers
 *
 */
public class StanfordNlpExamples {
	
	public static void main(String[] args) throws IOException{
		// let JSoup load file and parse the HTML in it
		File inputFile = new File("corpus/TerranVsProtoss/One Factory Double Expand.txt");
		Document htmlDocument = Jsoup.parse(inputFile, "UTF-8", "http://wiki.teamliquid.net");
		//Document document = Jsoup.connect("http://wiki.teamliquid.net/starcraft/1_Factory_Double_Expand_%28vs._Protoss%29").get();
		
		// let Jsoup collect the paragraphs of text for us
		ArrayList<String> textParagraphs = collectTextParagraphs(htmlDocument);
		
		// convert the paragraphs into a single String
		if(textParagraphs.size() == 0){
			return;
		}
		
		String text = "";
		for(String paragraph : textParagraphs){
			text += paragraph + "\n";
		}
		
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	    	// traversing the words in the current sentence
	    	// a CoreLabel is a CoreMap with additional token-specific methods
	    	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	    		// this is the text of the token
	    		String word = token.get(TextAnnotation.class);
	    		// this is the POS tag of the token
	    		String pos = token.get(PartOfSpeechAnnotation.class);
	    		// this is the NER label of the token
	    		String ne = token.get(NamedEntityTagAnnotation.class);     
	    		
	    		System.out.print(word + "\\" + pos + "\\" + ne + " ");
	    	}
	    	System.out.println();

	    	// this is the parse tree of the current sentence
	    	Tree tree = sentence.get(TreeAnnotation.class);

	    	// this is the Stanford dependency graph of the current sentence
	    	SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
	}
	
	/**
	 * Collects all text paragraphs in the given element.
	 * 
	 * @param element
	 * @return
	 */
	public static ArrayList<String> collectTextParagraphs(Element element){
		ArrayList<String> textParagraphs = new ArrayList<String>();
		Elements children = element.children();
		
		for(Element child : children){
			if(child.tagName().equals("p")){
				textParagraphs.add(child.text());
			}
			else{
				textParagraphs.addAll(collectTextParagraphs(child));
			}
		}
		
		return textParagraphs;
	}

}
