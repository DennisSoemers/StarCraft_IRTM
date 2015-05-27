package irtm.starcraft.game;

import irtm.starcraft.game.StarcraftKnowledgeBase.Matchups;
import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.viz.ColorImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

/**
 * A graph that can be used to visualize counter relations between strategies in Gephi
 *
 * @author Dennis Soemers
 */
public class StarcraftStrategyGraph {
	
	private Gexf gexf;
	private int nodeIndex;
	
	private HashMap<String, Node> namesToNodes;
	private HashMap<Node, Matchups> nodesToMatchups;
	
	public StarcraftStrategyGraph(){
		gexf = new GexfImpl();
		Calendar date = Calendar.getInstance();
		
		gexf.getMetadata()
			.setLastModified(date.getTime())
			.setCreator("Dennis Soemers")
			.setDescription("StarCraft: Brood War - Strategies");
		gexf.setVisualization(true);

		Graph graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.DIRECTED).setMode(Mode.STATIC);
		
		nodeIndex = 0;
		namesToNodes = new HashMap<String, Node>();
		nodesToMatchups = new HashMap<Node, Matchups>();
	}
	
	public void addCounter(String counterName, String counteredName, float counterStrength, Matchups matchup){
		Node counterNode = namesToNodes.get(counterName);
		
		if(counterNode == null){
			// first see if we can find a similar name
			for(String existing : namesToNodes.keySet()){
				Node oldNode = namesToNodes.get(existing);
				
				if(matchup == nodesToMatchups.get(oldNode)){
					if(counterName.contains(existing)){		// something already exists that is a substring of the new name
						namesToNodes.remove(existing);
						oldNode.setLabel(counterName);
						counterNode = oldNode;
						namesToNodes.put(counterName, counterNode);
						System.out.println("replacing " + existing + " with " + counterName);
						break;
					}
					else if(existing.contains(counterName)){	// new name is a substring of something that already exists
						counterNode = namesToNodes.get(existing);
						System.out.println("using " + existing + " instead of " + counterName);
						break;
					}
				}
			}
			
			if(counterNode == null){	// if still null, create a new node
				counterNode = gexf.getGraph().createNode(String.valueOf(nodeIndex++));
				counterNode.setLabel(counterName);
				counterNode.setColor(getColor(matchup));
				namesToNodes.put(counterName, counterNode);
				nodesToMatchups.put(counterNode, matchup);
				System.out.println("adding new strategy node: " + counterName);
			}
		}
		
		Node counteredNode = namesToNodes.get(counteredName);
		
		if(counteredNode == null){
			// first see if we can find a similar name
			for(String existing : namesToNodes.keySet()){
				Node oldNode = namesToNodes.get(existing);
				
				if(StarcraftKnowledgeBase.getOppositeMatchup(matchup) == nodesToMatchups.get(oldNode)){
					if(counteredName.contains(existing)){		// something already exists that is a substring of the new name
						namesToNodes.remove(existing);
						oldNode.setLabel(counteredName);
						counteredNode = oldNode;
						namesToNodes.put(counteredName, counteredNode);
						System.out.println("replacing " + existing + " with " + counteredName);
						break;
					}
					else if(existing.contains(counteredName)){	// new name is a substring of something that already exists
						counteredNode = namesToNodes.get(existing);
						System.out.println("using " + existing + " instead of " + counteredName);
						break;
					}
				}
			}
			
			if(counteredNode == null){	// if still null, create a new node
				counteredNode = gexf.getGraph().createNode(String.valueOf(nodeIndex++));
				counteredNode.setLabel(counteredName);
				counteredNode.setColor(getColor(StarcraftKnowledgeBase.getOppositeMatchup(matchup)));
				namesToNodes.put(counteredName, counteredNode);
				nodesToMatchups.put(counteredNode, matchup);
				System.out.println("adding new strategy node: " + counteredName);
			}
		}
		
		if(!counterNode.hasEdgeTo(counteredNode.getId())){
			Edge connection = counterNode.connectTo(counteredNode);
			connection.setEdgeType(EdgeType.DIRECTED);
			connection.setLabel((counterStrength > 0.5) ? "strong counter" : "weak counter");
		}
	}
	
	public it.uniroma1.dis.wsngroup.gexf4j.core.viz.Color getColor(Matchups matchup){
		it.uniroma1.dis.wsngroup.gexf4j.core.viz.Color returnColor = new ColorImpl();
		
		if(matchup == Matchups.PvP || matchup == Matchups.PvT || matchup == Matchups.PvZ){
			java.awt.Color green = java.awt.Color.GREEN;
			returnColor.setB(green.getBlue());
			returnColor.setG(green.getGreen());
			returnColor.setR(green.getRed());
		}
		else if(matchup == Matchups.TvP || matchup == Matchups.TvT || matchup == Matchups.TvZ){
			java.awt.Color blue = java.awt.Color.BLUE.darker();
			returnColor.setB(blue.getBlue());
			returnColor.setG(blue.getGreen());
			returnColor.setR(blue.getRed());
		}
		else if(matchup == Matchups.ZvP || matchup == Matchups.ZvT || matchup == Matchups.ZvZ){
			returnColor.setB(255);
			returnColor.setG(0);
			returnColor.setR(255);
		}
		
		return returnColor;
	}
	
	public void serialize(File directory){
		System.out.println();
		System.out.println("ALL STRATEGY NAMES:");
		ArrayList<String> strategyNames = new ArrayList<String>();
		strategyNames.addAll(namesToNodes.keySet());
		Collections.sort(strategyNames);
		for(String name : strategyNames){
			System.out.println(name);
		}
		
		StaxGraphWriter graphWriter = new StaxGraphWriter();
		File f = new File(directory.getAbsoluteFile() + File.separator + "strategy_graph.gexf");
		Writer out;
		try {
			out =  new FileWriter(f, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
			System.out.println(f.getAbsolutePath());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
