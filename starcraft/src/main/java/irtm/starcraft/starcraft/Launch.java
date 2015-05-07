package irtm.starcraft.starcraft;

import java.io.File;
import java.io.IOException;

/**
 * Class with main method to launch the application
 * 
 * @author Soemers
 *
 */
public class Launch {
	
	public static void main(String[] args) throws IOException{
		StarcraftTextMiner textMiner = new StarcraftTextMiner();
		StarcraftStrategy strategy = textMiner.processFile(new File("corpus/TerranVsTerran/14 CC (vs. Terran).txt"));
	}

}
