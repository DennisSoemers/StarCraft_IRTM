package irtm.starcraft.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Class with some utility functions for the Stanford NLP library and other String manipulation
 *
 * @author Dennis Soemers
 */
public class NlpUtils {
	
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
