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
								"TerranVsTerran/14 CC (vs. Terran)",
								"TerranVsTerran/2 Fact Vults (vs. Terran)",
								"TerranVsTerran/2 Port Wraith",
								"TerranVsTerran/Barracks Barracks Supply (vs. Terran)"
							};
		StarcraftTextMiner textMiner = new StarcraftTextMiner();
		
		for(String filepath : filepaths){
			System.out.println("processing " + "corpus/" + filepath + ".txt" + "...");
			StarcraftStrategy strategy = textMiner.processFile(new File("corpus/" + filepath + ".txt"));
			
			if(strategy != null){
				strategy.serialize(new File("extractedStrategies/" + filepath + ".xml"));
			}
		}
		
		System.out.println("Finished!");
	}

}
