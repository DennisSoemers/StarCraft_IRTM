package irtm.starcraft;

import irtm.starcraft.game.StarcraftStrategy;
import irtm.starcraft.textmining.StarcraftTextMiner;

import java.io.File;
import java.io.IOException;

/**
 * Class with main method to launch the application
 * 
 * @author Dennis Soemers
 *
 */
public class Launch {
	
	public static void main(String[] args) throws IOException{
		String[] filepaths = {
								"TerranVsTerran/1 Fact FE (vs. Terran)",
								"TerranVsTerran/1 Port Wraith (vs. Terran)",
								"TerranVsTerran/1 Rax FE (vs. Terran)",
								"TerranVsTerran/14 CC (vs. Terran)"
							};
		StarcraftTextMiner textMiner = new StarcraftTextMiner();
		
		for(String filepath : filepaths){
			System.out.println("processing " + "corpus/" + filepath + ".txt" + "...");
			StarcraftStrategy strategy = textMiner.processFile(new File("corpus/" + filepath + ".txt"));
			strategy.serialize(new File("extractedStrategies/" + filepath + ".xml"));
		}
		
		System.out.println("Finished!");
	}

}
