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
	
	public ArrayList<String> getCounteredBySoft(){
		return counteredBySoft;
	}
	
	public ArrayList<String> getCounteredByHard(){
		return counteredByHard;
	}
	
	public ArrayList<String> getCounterToSoft(){
		return counterToSoft;
	}
	
	public ArrayList<String> getCounterToHard(){
		return counterToHard;
	}
	
	public String getName(){
		return strategyName;
	}
	
	public ArrayList<String> getWeakMaps(){
		return weakMaps;
	}
	
	public ArrayList<String> getStrongMaps(){
		return strongMaps;
	}
	
	/**
	 * Serializes the strategy into XML
	 * @param file
	 * @throws IOException
	 */
	public void serialize(File file) throws IOException{
		if(!file.exists()){
			if(file.getParentFile() != null){
				file.getParentFile().mkdirs();
			}
			
			file.createNewFile();
		}
		
		// need to pre-process strategy name since many strategy names are illegal in XML
		String strategyNameLegal = "strategy " + strategyName;
		strategyNameLegal = legalizeString(strategyNameLegal);
		
		Element root = new Element(strategyNameLegal);
		
		// create build order elements
		for(StarcraftBuildOrder buildOrder : buildOrders){
			if(buildOrder.isEmpty()){
				continue;		// no point in serializing a build order that has no instructions
			}
			
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
				else if(instruction.getType() == InstructionTypes.SCOUT){
					instructionElement = new Element("scout");
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
					else if(precondition.getType() == PreconditionTypes.PERCENTAGE){
						// special case because we have more than a single value
						// gonna handle this here and continue the loop
						preconditionElement = new Element("percentage");
						
						Element percentageElement = new Element("pct");
						percentageElement.appendChild("" + precondition.getValue());
						Element typeElement = new Element("type");
						typeElement.appendChild(precondition.getUnitOrBuildingType());
						
						preconditionElement.appendChild(percentageElement);
						preconditionElement.appendChild(typeElement);
						
						instructionElement.appendChild(preconditionElement);
						continue;
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
			
			// create countered by element
			Element counteredByElement = new Element("countered_by");

			// build-order specific
			for(String softCounter : buildOrder.getCounteredBySoft()){
				Element counterElement = new Element("soft");
				counterElement.appendChild(softCounter);
				counteredByElement.appendChild(counterElement);
			}
			
			// strategy-wide
			for(String softCounter : getCounteredBySoft()){
				Element counterElement = new Element("soft");
				counterElement.appendChild(softCounter);
				counteredByElement.appendChild(counterElement);
			}
			
			// build-order specific
			for(String hardCounter : buildOrder.getCounteredByHard()){
				Element counterElement = new Element("hard");
				counterElement.appendChild(hardCounter);
				counteredByElement.appendChild(counterElement);
			}
			
			// strategy-wide
			for(String hardCounter : getCounteredByHard()){
				Element counterElement = new Element("hard");
				counterElement.appendChild(hardCounter);
				counteredByElement.appendChild(counterElement);
			}
			
			buildOrderElement.appendChild(counteredByElement);
			
			// create counter to element
			Element counterToElement = new Element("counter_to");

			// build-order specific
			for(String softCounter : buildOrder.getCounterToSoft()){
				Element counterElement = new Element("soft");
				counterElement.appendChild(softCounter);
				counterToElement.appendChild(counterElement);
			}
			
			// strategy-wide
			for(String softCounter : getCounterToSoft()){
				Element counterElement = new Element("soft");
				counterElement.appendChild(softCounter);
				counterToElement.appendChild(counterElement);
			}
			
			// build-order specific
			for(String hardCounter : buildOrder.getCounterToHard()){
				Element counterElement = new Element("hard");
				counterElement.appendChild(hardCounter);
				counterToElement.appendChild(counterElement);
			}
			
			// strategy-wide
			for(String hardCounter : getCounterToHard()){
				Element counterElement = new Element("hard");
				counterElement.appendChild(hardCounter);
				counterToElement.appendChild(counterElement);
			}
			
			buildOrderElement.appendChild(counterToElement);
			
			// create maps element
			Element mapsElement = new Element("maps");
			
			// build-order specific
			for(String strongMap : buildOrder.getStrongMaps()){
				Element mapElement = new Element("strong");
				mapElement.appendChild(strongMap);
				mapsElement.appendChild(mapElement);
			}
			
			// strategy-wide
			for(String strongMap : getStrongMaps()){
				Element mapElement = new Element("strong");
				mapElement.appendChild(strongMap);
				mapsElement.appendChild(mapElement);
			}
			
			// build-order specific
			for(String weakMap : buildOrder.getWeakMaps()){
				Element mapElement = new Element("weak");
				mapElement.appendChild(weakMap);
				mapsElement.appendChild(mapElement);
			}
			
			// strategy-wide
			for(String weakMap : getWeakMaps()){
				Element mapElement = new Element("weak");
				mapElement.appendChild(weakMap);
				mapsElement.appendChild(mapElement);
			}
			
			buildOrderElement.appendChild(mapsElement);
			
			root.appendChild(buildOrderElement);
		}
		
		Document doc = new Document(root);
		Serializer serializer = new Serializer(new FileOutputStream(file), "UTF-8");
		serializer.setIndent(4);
		serializer.setMaxLength(80);
		serializer.write(doc);
	}
	
	/**
	 * Produces simple HTML text to describe the strategy
	 * 
	 * @return
	 */
	public String toHtml(){
		String html = "";
		html += "<h1>" + strategyName + "</h1>";
		
		for(StarcraftBuildOrder buildOrder : buildOrders){
			if(buildOrder.isEmpty()){
				continue;		// no point in serializing a build order that has no instructions
			}
			
			html += "<h2>Build Order</h2>";
			
			ArrayList<StarcraftBuildOrderInstruction> instructions = buildOrder.getInstructions();
			html += "<ul>";
			for(StarcraftBuildOrderInstruction instruction : instructions){
				html += "<li>";
				
				String instructionText = "";
				for(int i = 0; i < instruction.getPreconditions().size(); ++i){
					StarcraftPrecondition precondition = instruction.getPreconditions().get(i);

					if(precondition.getType() == PreconditionTypes.SUPPLY){
						instructionText += "Required Supply: " + precondition.getValue();
					}
					else if(precondition.getType() == PreconditionTypes.MINERALS){
						instructionText += "Required Minerals: " + precondition.getValue();
					}
					else if(precondition.getType() == PreconditionTypes.GAS){
						instructionText += "Required Gas: " + precondition.getValue();
					}
					else if(precondition.getType() == PreconditionTypes.PERCENTAGE){
						instructionText += "@" + precondition.getValue() + "% " + precondition.getUnitOrBuildingType();
					}
					else{
						continue;
					}
					
					if(i != instruction.getPreconditions().size() - 1){
						instructionText += ", ";
					}
				}
				
				instructionText += " - ";
				
				if(instruction.getType() == InstructionTypes.BUILDING){
					instructionText += "Create Building: ";
				}
				else if(instruction.getType() == InstructionTypes.UNIT){
					instructionText += "Create Unit: ";
				}
				else if(instruction.getType() == InstructionTypes.SCOUT){
					instructionText += "Send Scout: ";
				}
				else{
					continue;
				}
				
				instructionText += instruction.getInstructionText();
				html += instructionText;
				
				html += "</li>";
			}
			html += "</ul>";
			
			html += "<h2>Countered By</h2>\n";
			html += "<h3>Soft Counters</h3>\n";
			html += "<ul>\n";

			// build-order specific
			for(String softCounter : buildOrder.getCounteredBySoft()){
				html += "<li>" + softCounter + "</li>\n";
			}
			
			// strategy-wide
			for(String softCounter : getCounteredBySoft()){
				html += "<li>" + softCounter + "</li>\n";
			}
			
			html += "</ul>\n";
			
			html += "<h3>Hard Counters</h3>\n";
			html += "<ul>\n";
			
			// build-order specific
			for(String hardCounter : buildOrder.getCounteredByHard()){
				html += "<li>" + hardCounter + "</li>\n";
			}
			
			// strategy-wide
			for(String hardCounter : getCounteredByHard()){
				html += "<li>" + hardCounter + "</li>\n";
			}
			
			html += "</ul>\n";
			
			html += "<h2>Counter To</h2>\n";
			html += "<h3>Soft Counters</h3>\n";
			html += "<ul>\n";

			// build-order specific
			for(String softCounter : buildOrder.getCounterToSoft()){
				html += "<li>" + softCounter + "</li>\n";
			}
			
			// strategy-wide
			for(String softCounter : getCounterToSoft()){
				html += "<li>" + softCounter + "</li>\n";
			}
			
			html += "</ul>\n";
			
			html += "<h3>Hard Counters</h3>\n";
			html += "<ul>\n";
			
			// build-order specific
			for(String hardCounter : buildOrder.getCounterToHard()){
				html += "<li>" + hardCounter + "</li>\n";
			}
			
			// strategy-wide
			for(String hardCounter : getCounterToHard()){
				html += "<li>" + hardCounter + "</li>\n";
			}
			
			html += "</ul>\n";
			
			html += "<h2>Maps</h2>\n";
			html += "<h3>Strong Maps</h3>\n";
			html += "<ul>\n";
			
			// build-order specific
			for(String strongMap : buildOrder.getStrongMaps()){
				html += "<li>" + strongMap + "</li>\n";
			}
			
			// strategy-wide
			for(String strongMap : getStrongMaps()){
				html += "<li>" + strongMap + "</li>\n";
			}
			
			html += "</ul>\n";
			
			html += "<h3>Weak Maps</h3>\n";
			html += "<ul>\n";
			
			// build-order specific
			for(String weakMap : buildOrder.getWeakMaps()){
				html += "<li>" + weakMap + "</li>\n";
			}
			
			// strategy-wide
			for(String weakMap : getWeakMaps()){
				html += "<li>" + weakMap + "</li>\n";
			}
			
			html += "</ul>\n";
		}
		
		return html;
	}

	/**
	 * Returns a copy of the given string that is legal to use as name for XML elements
	 * 
	 * @param s
	 * @return
	 */
	private String legalizeString(String s){
		s = s.replaceAll("\\p{P}", " ").replaceAll("\\+", " ").replaceAll(" ", "_");
		
		try{
			Integer.parseInt(s.substring(0, 1));
			
			// if we didn't catch any exceptions, means the string starts with a number, so add an underscore
			s = "_" + s;
		}
		catch(NumberFormatException exception){}
		
		return s;
	}

}
