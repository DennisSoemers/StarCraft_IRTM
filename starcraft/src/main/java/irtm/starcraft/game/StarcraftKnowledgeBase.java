package irtm.starcraft.game;

import edu.stanford.nlp.util.ArrayUtils;

/**
 * Knowledge base with a bunch of constant info that we know about StarCraft
 * (for example, unit names)
 *
 * @author Dennis Soemers
 */
public class StarcraftKnowledgeBase {
	
	public static boolean isRace(String word){
		return ArrayUtils.contains(RACE_NAMES, word.trim().toLowerCase());
	}
	
	public static boolean isBuilding(String word){
		word = word.trim().toLowerCase();
		return (ArrayUtils.contains(PROTOSS_BUILDING_NAMES, word) 	||
				ArrayUtils.contains(TERRAN_BUILDING_NAMES, word) 	||
				ArrayUtils.contains(ZERG_BUILDING_NAMES, word) 			);
	}
	
	public static boolean isUnit(String word){
		word = word.trim().toLowerCase();
		return (ArrayUtils.contains(PROTOSS_UNIT_NAMES, word) 	||
				ArrayUtils.contains(TERRAN_UNIT_NAMES, word) 	||
				ArrayUtils.contains(ZERG_UNIT_NAMES, word) 			);
	}
	
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

}
