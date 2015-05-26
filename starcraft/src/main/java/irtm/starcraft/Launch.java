package irtm.starcraft;

import irtm.starcraft.gui.StarcraftIrtmFrame;

import java.io.IOException;

import javax.swing.UIManager;

/**
 * Class with main method to launch the application
 * 
 * @author Dennis Soemers
 *
 */
public class Launch {
	
	public static void main(String[] args) throws IOException{
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
		
		new StarcraftIrtmFrame().init();
	}

}
