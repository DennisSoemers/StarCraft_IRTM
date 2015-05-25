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
		String filepath = "TerranVsTerran/1 Port Wraith (vs. Terran)";
		
		StarcraftTextMiner textMiner = new StarcraftTextMiner();
		StarcraftStrategy strategy = textMiner.processFile(new File("corpus/" + filepath + ".txt"));
		strategy.serialize(new File("extractedStrategies/" + filepath + ".xml"));
	}

}
