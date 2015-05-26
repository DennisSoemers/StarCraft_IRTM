package irtm.starcraft.gui;

import irtm.starcraft.game.StarcraftStrategy;
import irtm.starcraft.textmining.StarcraftTextMiner;
import irtm.starcraft.utils.WikiPageTree;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;

/**
 * Main frame for the application's GUI
 *
 * @author Dennis Soemers
 */
public class StarcraftIrtmFrame extends JFrame{
	
	private JMenuBar menuBar;
		private JMenu fileMenu;
			private JMenuItem loadDataMenuItem;
			
	private JSplitPane mainPane;
		private JScrollPane leftPane;
			private JEditorPane leftTextPane;
		private JScrollPane rightPane;
			private JEditorPane rightTextPane;
	
	private JFileChooser fileChooser;
	
	public StarcraftIrtmFrame(){
		super("StarCraft: Brood War - Strategy Retriever");
		
		// set up menu bar
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		loadDataMenuItem = new JMenuItem("Load Data");
				
		fileMenu.add(loadDataMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		
		leftPane = new JScrollPane();
		leftPane.setBackground(Color.WHITE);
		leftTextPane = new JEditorPane();
		leftPane.setViewportView(leftTextPane);
		rightPane = new JScrollPane();
		rightPane.setBackground(Color.WHITE);
		rightTextPane = new JEditorPane();
		rightTextPane.setContentType("text/html");
		rightPane.setViewportView(rightTextPane);
		
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPane.setBackground(Color.WHITE);
		mainPane.setLeftComponent(leftPane);
		mainPane.setRightComponent(rightPane);
		setContentPane(mainPane);
		
		// set up file chooser
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File file) {
				return (file.isDirectory() || file.getName().endsWith(".txt") || file.getName().endsWith(".html"));
			}

			@Override
			public String getDescription() {
				return "wiki page files (*.html *.txt)";
			}
					
		});
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		// set up listeners
		setupListeners();
		
		// finalize frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void init(){
		mainPane.setDividerLocation(0.5);
	}
	
	private void setupListeners(){
		loadDataMenuItem.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent event) {
				int returnValue = fileChooser.showOpenDialog(StarcraftIrtmFrame.this);
				
				if(returnValue == JFileChooser.APPROVE_OPTION){
					File openedFile = fileChooser.getSelectedFile();
					try {
						if(openedFile.isDirectory()){
							// TODO process entire corpus
						}
						else if(openedFile.exists()){
							StarcraftTextMiner textMiner = new StarcraftTextMiner();
							StarcraftStrategy strategy = textMiner.processFile(openedFile);
							
							if(strategy != null){
								WikiPageTree documentTree = textMiner.getLastDocumentTree();
								rightTextPane.setText(documentTree.getHtml());
							}
						}
					} 
					catch (IOException exception) {
						exception.printStackTrace();
					}
				}
			}
			
		});
	}

}
