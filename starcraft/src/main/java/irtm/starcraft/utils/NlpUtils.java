package irtm.starcraft.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Class with some utility functions for the Stanford NLP library
 *
 * @author Dennis Soemers
 */
public class NlpUtils {

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
