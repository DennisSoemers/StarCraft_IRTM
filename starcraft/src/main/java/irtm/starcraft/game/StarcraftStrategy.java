package irtm.starcraft.game;

import irtm.starcraft.game.StarcraftBuildOrderInstruction.InstructionTypes;
import irtm.starcraft.game.StarcraftPrecondition.PreconditionTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * Class for a single StarCraft Strategy which is obtained by reading
 * and processing a single file.
 * 
 * @author Dennis Soemers
 *
 */
public class StarcraftStrategy {
	
	private String strategyName;
	private ArrayList<StarcraftBuildOrder> buildOrders = new ArrayList<StarcraftBuildOrder>();
	private ArrayList<String> counteredBySoft = new ArrayList<String>();
	private ArrayList<String> counteredByHard = new ArrayList<String>();
	private ArrayList<String> counterToSoft = new ArrayList<String>();
	private ArrayList<String> counterToHard = new ArrayList<String>();
	private ArrayList<String> weakMaps = new ArrayList<String>();
	private ArrayList<String> strongMaps = new ArrayList<String>();
	
	public StarcraftStrategy(String strategyName){
		this.strategyName = strategyName;
	}
	
	public void addBuildOrder(StarcraftBuildOrder buildOrder){
		buildOrders.add(buildOrder);
	}
	
	public void addCounteredBySoft(String softCounter){
		counteredBySoft.add(softCounter);
	}
	
	public void addCounteredByHard(String hardCounter){
		counteredByHard.add(hardCounter);
	}
	
	public void addCounterToSoft(String softCounter){
		counterToSoft.add(softCounter);
	}
	
	public void addCounterToHard(String hardCounter){
		counterToHard.add(hardCounter);
	}
	
	public void addWeakMap(String mapName){
		weakMaps.add(mapName);
	}
	
	public void addStrongMap(String mapName){
		strongMaps.add(mapName);
	}
	
	/**
	 * Serializes the strategy into XML
	 * @param file
	 * @throws IOException
	 */
	public void serialize(File file) throws IOException{
		if(!file.exists()){
			file.createNewFile();
		}
		
		// need to pre-process strategy name since many strategy names are illegal in XML
		String strategyNameLegal = "strategy " + strategyName;
		strategyNameLegal = legalizeString(strategyNameLegal);
		
		Element root = new Element(strategyNameLegal);
		
		// create build order elements
		for(StarcraftBuildOrder buildOrder : buildOrders){
			Element buildOrderElement = new Element("build_order");
			
			ArrayList<StarcraftBuildOrderInstruction> instructions = buildOrder.getInstructions();
			// create instruction (= building or unit) elements
			for(StarcraftBuildOrderInstruction instruction : instructions){
				Element instructionElement;
				
				if(instruction.getType() == InstructionTypes.BUILDING){
					instructionElement = new Element("building");
				}
				else if(instruction.getType() == InstructionTypes.UNIT){
					instructionElement = new Element("unit");
				}
				else{
					System.err.println("StarcraftStrategy::serialize(): unknown instruction type!");
					continue;
				}
				
				// create precondition elements
				for(StarcraftPrecondition precondition : instruction.getPreconditions()){
					Element preconditionElement;
					
					if(precondition.getType() == PreconditionTypes.SUPPLY){
						preconditionElement = new Element("supply");
					}
					else if(precondition.getType() == PreconditionTypes.MINERALS){
						preconditionElement = new Element("minerals");
					}
					else if(precondition.getType() == PreconditionTypes.GAS){
						preconditionElement = new Element("gas");
					}
					else{
						System.err.println("StarcraftStrategy::serialize(): unknown precondition type");
						continue;
					}
					
					preconditionElement.appendChild("" + precondition.getValue());
					
					instructionElement.appendChild(preconditionElement);
				}
				
				// create the element containing building or unit type
				Element typeElement = new Element("type");
				typeElement.appendChild(instruction.getInstructionText());
				instructionElement.appendChild(typeElement);
				
				buildOrderElement.appendChild(instructionElement);
			}
			
			root.appendChild(buildOrderElement);
		}
		
		// create countered by element
		Element counteredByElement = new Element("countered_by");

		for(String softCounter : counteredBySoft){
			Element counterElement = new Element("soft");
			counterElement.appendChild(softCounter);
			counteredByElement.appendChild(counterElement);
		}
		
		for(String hardCounter : counteredByHard){
			Element counterElement = new Element("hard");
			counterElement.appendChild(hardCounter);
			counteredByElement.appendChild(counterElement);
		}
		
		root.appendChild(counteredByElement);
		
		// create counter to element
		Element counterToElement = new Element("counter_to");

		for(String softCounter : counterToSoft){
			Element counterElement = new Element("soft");
			counterElement.appendChild(softCounter);
			counterToElement.appendChild(counterElement);
		}
		
		for(String hardCounter : counterToHard){
			Element counterElement = new Element("hard");
			counterElement.appendChild(hardCounter);
			counterToElement.appendChild(counterElement);
		}
		
		root.appendChild(counterToElement);
		
		// create maps element
		Element mapsElement = new Element("maps");
		
		for(String strongMap : strongMaps){
			Element mapElement = new Element("strong");
			mapElement.appendChild(strongMap);
			mapsElement.appendChild(mapElement);
		}
		
		for(String weakMap : weakMaps){
			Element mapElement = new Element("weak");
			mapElement.appendChild(weakMap);
			mapsElement.appendChild(mapElement);
		}
		
		root.appendChild(mapsElement);
		
		Document doc = new Document(root);
		Serializer serializer = new Serializer(new FileOutputStream(file), "UTF-8");
		serializer.setIndent(4);
		serializer.setMaxLength(80);
		serializer.write(doc);
	}

	/**
	 * Returns a copy of the given string that is legal to use as name for XML elements
	 * 
	 * @param s
	 * @return
	 */
	private String legalizeString(String s){
		s = s.replaceAll("\\p{P}", " ").replaceAll(" ", "_");
		
		try{
			Integer.parseInt(s.substring(0, 1));
			
			// if we didn't catch any exceptions, means the string starts with a number, so add an underscore
			s = "_" + s;
		}
		catch(NumberFormatException exception){}
		
		return s;
	}

}
