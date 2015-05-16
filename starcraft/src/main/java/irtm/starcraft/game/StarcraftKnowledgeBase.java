package irtm.starcraft.game;

import java.util.HashMap;

import edu.stanford.nlp.util.ArrayUtils;

/**
 * Knowledge base with a bunch of constant info that we know about StarCraft
 * (for example, unit names)
 *
 * @author Dennis Soemers
 */
public class StarcraftKnowledgeBase {
	
	final static String[] RACE_NAMES = {
		"protoss",
		"terran",
		"zerg"
	};
	
	final static String[] PROTOSS_BUILDING_NAMES = {
		"nexus",
		"pylon",
		"assimilator",
		"gateway",
		"forge",
		"shield battery",
		"cybernetics core",
		"photon cannon",
		"robotics facility",
		"stargate",
		"citadel of adun",
		"robotics support bay",
		"fleet beacon",
		"templar archives",
		"observatory",
		"arbiter tribunal"
	};
	
	final static String[] PROTOSS_UNIT_NAMES = {
		"probe",
		"zealot",
		"dragoon",
		"high templar",
		"dark templar",
		"reaver",
		"archon",
		"dark archon",
		"observer",
		"shuttle",
		"scout",
		"carrier",
		"interceptor",
		"arbiter",
		"corsair"
	};
	
	final static String[] TERRAN_BUILDING_NAMES = {
		"command center",
		"comsat station",
		"nuclear silo",
		"supply depot",
		"refinery",
		"barracks",
		"engineering bay",
		"bunker",
		"academy",
		"missile turret",
		"factory",
		"machine shop",
		"starport",
		"control tower",
		"armory",
		"science facility",
		"physics lab",
		"covert ops"
	};
	
	final static String[] TERRAN_UNIT_NAMES = {
		"scv",
		"marine",
		"firebat",
		"medic",
		"ghost",
		"vulture",
		"spider mine",
		"siege tank",
		"goliath",
		"wraith",
		"dropship",
		"science vessel",
		"battlecruiser",
		"valkyrie"
	};
	
	final static String[] ZERG_BUILDING_NAMES = {
		"hatchery",
		"creep colony",
		"sunken colony",
		"spore colony",
		"extractor",
		"spawning pool",
		"evolution chamber",
		"hydralisk den",
		"lair",
		"spire",
		"queen's nest",
		"hive",
		"greater spire",
		"nydus canal",
		"ultralisk cavern",
		"defiler mound"
	};
	
	final static String[] ZERG_UNIT_NAMES = {
		"larva",
		"egg",
		"drone",
		"zergling",
		"hydralisk",
		"lurker",
		"lurker egg",
		"ultralisk",
		"defiler",
		"infested terran",
		"broodling",
		"overlord",
		"mutalisk",
		"cocoon",
		"scourge",
		"queen",
		"guardian",
		"devourer"
	};
	
	/**
	 * Returns the base version of the given string, if it is a Starcraft term.
	 * Can be used to convert for example abbreviations into the full name of a unit
	 * 
	 * @param string
	 * @return
	 */
	public static String getBaseTerm(String string){
		return starcraftDictionary.get(string.trim().toLowerCase());
	}
	
	public static boolean isRace(String string){
		string = starcraftDictionary.get(string.trim().toLowerCase());
		
		if(string == null){
			return false;
		}
		
		return ArrayUtils.contains(RACE_NAMES, string);
	}
	
	public static boolean isBuilding(String string){
		string = starcraftDictionary.get(string.trim().toLowerCase());
		
		if(string == null){
			return false;
		}
		
		return (ArrayUtils.contains(PROTOSS_BUILDING_NAMES, string) ||
				ArrayUtils.contains(TERRAN_BUILDING_NAMES, string) 	||
				ArrayUtils.contains(ZERG_BUILDING_NAMES, string) 		);
	}
	
	public static boolean isStarcraftTerm(String string){
		return starcraftDictionary.containsKey(string);
	}
	
	public static boolean isUnit(String string){
		string = starcraftDictionary.get(string.trim().toLowerCase());
		
		if(string == null){
			return false;
		}
		
		return (ArrayUtils.contains(PROTOSS_UNIT_NAMES, string) ||
				ArrayUtils.contains(TERRAN_UNIT_NAMES, string) 	||
				ArrayUtils.contains(ZERG_UNIT_NAMES, string) 		);
	}
	
	public static boolean isUnitOrBuilding(String string){
		return (isBuilding(string) || isUnit(string));
	}
	
	/* 
	 * Hardcoded dictionary mapping from all kinds of words (and abbreviations) commonly used in wiki
	 * to one of the names used in one of the hardcoded arrays below.
	 */
	static HashMap<String, String> starcraftDictionary;
	
	// initialize the hardcoded dictionary
	static{
		starcraftDictionary = new HashMap<String, String>();

		// start with the exact copies of the terms used in hardcoded arrays
		for(String s : RACE_NAMES){
			starcraftDictionary.put(s, s);
		}
		
		for(String s : PROTOSS_BUILDING_NAMES){
			starcraftDictionary.put(s, s);
		}
		
		for(String s : PROTOSS_UNIT_NAMES){
			starcraftDictionary.put(s, s);
		}
		
		for(String s : TERRAN_BUILDING_NAMES){
			starcraftDictionary.put(s, s);
		}
		
		for(String s : TERRAN_UNIT_NAMES){
			starcraftDictionary.put(s, s);
		}
		
		for(String s : ZERG_BUILDING_NAMES){
			starcraftDictionary.put(s, s);
		}
		
		for(String s : ZERG_UNIT_NAMES){
			starcraftDictionary.put(s, s);
		}
		
		// now some hardcoded abbreviations / other terms known to be commonly used in the wiki
		// partially taken from http://starcraft.wikia.com/wiki/List_of_StarCraft_terminology
		// partially created manually by reading lots of wiki pages
		starcraftDictionary.put("arb", "arbiter");
		starcraftDictionary.put("arc", "archon");
		starcraftDictionary.put("bay", "engineering bay");
		starcraftDictionary.put("bc", "battlecruiser");
		starcraftDictionary.put("cc", "command center");
		starcraftDictionary.put("ceptor", "interceptor");
		starcraftDictionary.put("da", "dark archon");
		starcraftDictionary.put("dt", "dark templar");
		starcraftDictionary.put("ebay", "engineering bay");
		starcraftDictionary.put("e-bay", "engineering bay");
		starcraftDictionary.put("fact", "factory");
		starcraftDictionary.put("fbat", "firebat");
		starcraftDictionary.put("gate", "gateway");
		starcraftDictionary.put("goon", "dragoon");
		starcraftDictionary.put("hatch", "hatchery");
		starcraftDictionary.put("high temp", "high templar");
		starcraftDictionary.put("ht", "high templar");
		starcraftDictionary.put("temp", "high templar");
		starcraftDictionary.put("hydra", "hydralisk");
		starcraftDictionary.put("ling", "zergling");
		starcraftDictionary.put("muta", "mutalisk");
		starcraftDictionary.put("obs", "observer");
		starcraftDictionary.put("rax", "barracks");
		starcraftDictionary.put("rine", "marine");
		starcraftDictionary.put("sair", "corsair");
		starcraftDictionary.put("depot", "supply depot");
		starcraftDictionary.put("ultra", "ultralisk");
		starcraftDictionary.put("robo", "robotics facility");
		starcraftDictionary.put("cannon", "photon cannon");
		starcraftDictionary.put("tank", "siege tank");
	}
}
