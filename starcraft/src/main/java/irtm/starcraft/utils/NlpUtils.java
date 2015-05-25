package irtm.starcraft.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Class with some utility functions for the Stanford NLP library and other String manipulation
 *
 * @author Dennis Soemers
 */
public class NlpUtils {
	
	private static StanfordCoreNLP nlpPipeline;
	
	static{
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		nlpPipeline = new StanfordCoreNLP(props);
	}
	
	/**
	 * From the given sequence of tokens, extracts all n-grams such that n >= minN && n <= maxN.
	 * 
	 * @param minN
	 * @param maxN
	 * @param tokenSequence
	 * @return
	 */
	public static ArrayList<String> extractNGrams(int minN, int maxN, ArrayList<String> tokenSequence){
		ArrayList<String> nGrams = new ArrayList<String>();
		
		for(int n = minN; n <= maxN; ++n){
			int extraTokens = n - 1;
			
			for(int i = 0; i < tokenSequence.size() && i + extraTokens < tokenSequence.size(); ++i){
				String nGram = tokenSequence.get(i);
				
				for(int j = i + 1; j <= i + extraTokens && j < tokenSequence.size(); ++j){
					nGram += " " + tokenSequence.get(j);
				}
				
				nGrams.add(nGram);
			}
		}
		
		return nGrams;
	}
	
	public static StanfordCoreNLP getNlpPipeline(){
		return nlpPipeline;
	}
	
	/**
	 * If the given String s is a word representing a single digit (zero, ..., nine) in English,
	 * converts it into the numeric variant (0, ..., 9) and returns it.
	 * 
	 * @param s
	 * @return
	 */
	public static String textDigitToNumeric(String s){
		final String[] textVersions = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
		final String[] numericVersions = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		
		for(int i = 0; i < textVersions.length; ++i){
			if(s.toLowerCase().trim().equals(textVersions[i])){
				return numericVersions[i];
			}
		}
		
		return s;
	}

	/**
	 * Writes the given list of tokens with given default NER annotations to the file
	 * with the given fileName.
	 * 
	 * The tokens should have been obtained through a Stanford NLP pipeline that has,
	 * at least, the annotators 'tokenize' and 'ner'
	 * 
	 * The output file can then be manually checked/changed and used for training/testing
	 * a new classifier.
	 * 
	 * @param tokens
	 * @param fileName
	 */
	public static void writeDefaultAnnotatedNerTokens(List<CoreLabel> tokens, String fileName){
		try {
			File file = new File(fileName);
			if(!file.exists()){
				file.createNewFile();
			}

			FileWriter writer = new FileWriter(fileName, true);
			
			for(CoreLabel token : tokens){
				String line = token.get(TextAnnotation.class) + "\t" + token.get(NamedEntityTagAnnotation.class);
				writer.write(line + "\n");
			}
			
			writer.close();
		} 
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
}
