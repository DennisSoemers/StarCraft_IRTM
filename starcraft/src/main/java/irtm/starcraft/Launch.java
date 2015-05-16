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
		StarcraftTextMiner textMiner = new StarcraftTextMiner();
		StarcraftStrategy strategy = textMiner.processFile(new File("corpus/TerranVsZerg/1 Rax FE (vs. Zerg).txt"));
	}

}
