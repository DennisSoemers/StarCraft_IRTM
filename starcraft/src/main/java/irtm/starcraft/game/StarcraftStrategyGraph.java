package irtm.starcraft.game;

import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;

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
	}
	
	public void addCounter(String counterName, String counteredName, float counterStrength){
		Node counterNode = namesToNodes.get(counterName);
		
		if(counterNode == null){
			counterNode = gexf.getGraph().createNode(String.valueOf(nodeIndex++));
			counterNode.setLabel(counterName);
			namesToNodes.put(counterName, counterNode);
		}
		
		Node counteredNode = namesToNodes.get(counteredName);
		
		if(counteredNode == null){
			counteredNode = gexf.getGraph().createNode(String.valueOf(nodeIndex++));
			counteredNode.setLabel(counteredName);
			namesToNodes.put(counteredName, counteredNode);
		}
		
		if(!counterNode.hasEdgeTo(counteredNode.getId())){
			Edge connection = counterNode.connectTo(counteredNode);
			connection.setEdgeType(EdgeType.DIRECTED);
			connection.setLabel((counterStrength > 0.5) ? "strong counter" : "weak counter");
		}
	}
	
	public void serialize(File directory){
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
