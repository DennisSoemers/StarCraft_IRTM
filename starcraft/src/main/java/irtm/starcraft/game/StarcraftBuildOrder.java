package irtm.starcraft.game;

import irtm.starcraft.game.StarcraftBuildOrderInstruction.InstructionTypes;
import irtm.starcraft.game.StarcraftPrecondition.PreconditionTypes;
import irtm.starcraft.utils.WikiPageNode;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * Class representing a single StarCraft build order
 *
 * @author Dennis Soemers
 */
public class StarcraftBuildOrder {
	
	private ArrayList<StarcraftBuildOrderInstruction> instructions = new ArrayList<StarcraftBuildOrderInstruction>();
	private ArrayList<String> counteredBySoft = new ArrayList<String>();
	private ArrayList<String> counteredByHard = new ArrayList<String>();
	private ArrayList<String> counterToSoft = new ArrayList<String>();
	private ArrayList<String> counterToHard = new ArrayList<String>();
	private ArrayList<String> weakMaps = new ArrayList<String>();
	private ArrayList<String> strongMaps = new ArrayList<String>();

	public StarcraftBuildOrder(WikiPageNode node, Annotation annotation) {
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence : sentences) {
	    	ArrayList<StarcraftPrecondition> preconditions = new ArrayList<StarcraftPrecondition>();
	    	List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
	    	// once we found something to build in a sentence, we no longer allow preconditions later in that sentence
	    	boolean allowPreconditions = true;
	    	
	    	for(int i = 0; i < tokens.size(); ++i){
	    		CoreLabel token = tokens.get(i);
	    		String tokenText = token.get(TextAnnotation.class);
	    		String nextTokenText = "";
	    		
	    		if(i + 1 < tokens.size()){
	    			nextTokenText = tokens.get(i + 1).get(TextAnnotation.class);
	    		}
	    		
	    		if(allowPreconditions){
	    			if(nextTokenText.equals("-") || nextTokenText.equals("and") || tokenText.contains("/")){	// found a precondition
			    		if(tokenText.contains("/")){	// probably supply condition in format x/y
			    			String[] supplyLevels = tokenText.split("/");
			    			
			    			if(supplyLevels.length != 2){
			    				printPreconditionError(tokenText);
			    			}
			    			
			    			try{
			    				int requiredSupply = Integer.parseInt(supplyLevels[0]);
			    				preconditions.add(new StarcraftPrecondition(PreconditionTypes.SUPPLY, requiredSupply));
			    				continue;
			    			}
			    			catch(NumberFormatException exception){
			    				printPreconditionError(tokenText);
			    			}
			    		}	// TODO precondition in format @xxx Gas (see: http://wiki.teamliquid.net/starcraft/1_Rax_Gas_%28vs._Zerg%29)
			    		else{	// try directly parsing as int, maybe condition in format of <supply - thing to build>
			    			try{
			    				int requiredSupply = Integer.parseInt(tokenText);
			    				preconditions.add(new StarcraftPrecondition(PreconditionTypes.SUPPLY, requiredSupply));
			    				continue;
			    			}
			    			catch(NumberFormatException exception){
			    			}
			    		}
			    	}
	    		}
	    		
	    		// didn't continue yet, meaning we didn't add a precondition, so see if we have something we can build
	    		String unitOrBuildingText = null;
	    		if(StarcraftKnowledgeBase.isUnitOrBuilding(tokenText)){		// try only this token...
	    			unitOrBuildingText = StarcraftKnowledgeBase.getBaseTerm(tokenText);
	    		}	// ... and if not a match, try it together with next token
	    		else if(StarcraftKnowledgeBase.isUnitOrBuilding(tokenText + " " + nextTokenText)){
	    			unitOrBuildingText = StarcraftKnowledgeBase.getBaseTerm(tokenText + " " + nextTokenText);
	    			
	    			++i;	// increase loop counter because we don't want to visit the next token again if we've already used it
	    		}
	    		
	    		if(unitOrBuildingText != null){
	    			// found an instruction to execute, so no longer allow preconditions in this sentence
	    			allowPreconditions = false;
	    			
	    			if(StarcraftKnowledgeBase.isBuilding(unitOrBuildingText)){
	    				instructions.add(new StarcraftBuildOrderInstruction(InstructionTypes.BUILDING, 
	    																	unitOrBuildingText, preconditions));
	    			}
	    			else if(StarcraftKnowledgeBase.isUnit(unitOrBuildingText)){
	    				instructions.add(new StarcraftBuildOrderInstruction(InstructionTypes.UNIT, 
																			unitOrBuildingText, preconditions));
	    			}
	    		}
	    	}
	    }
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
	
	public ArrayList<String> getWeakMaps(){
		return weakMaps;
	}
	
	public ArrayList<String> getStrongMaps(){
		return strongMaps;
	}
	
	public ArrayList<StarcraftBuildOrderInstruction> getInstructions(){
		return instructions;
	}
	
	private void printPreconditionError(String tokenText){
		System.err.println("StarcraftBuildOrder::Ctor: Don't know how to deal with token: ''" + tokenText + "''");
	}

}
