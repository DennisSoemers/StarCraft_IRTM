package irtm.starcraft.game;

import java.util.Arrays;
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
	
	final static String[] MAP_NAMES_2_PLAYER = {
	    "Acheron",
	    "Alternative",
	    "Asgard",
	    "Ash Rose",
	    "Baby Steps",
	    "Baekmagoji",
	    "Battle Royal",
	    "Benzene",
	    "Bifrost",
	    "Binary Burghs",
	    "Blitz",
	    "Bloody Ridge",
	    "Blue Storm",
	    "Bottleneck",
	    "Boxer",
	    "Breaking Point",
	    "Byway",
	    "Carthage",
	    "Chain Reaction",
	    "Challenger",
	    "Checkmate",
	    "Chupung-Ryeong",
	    "Close Quarters",
	    "Coulee",
	    "Cross Game",
	    "Crystalized",
	    "Crystallis",
	    "Desolate Platform",
	    "Destination",
	    "Detonation",
	    "Discovery",
	    "Double Jeopardy",
	    "Efreet",
	    "Eldritch Lake",
	    "Emperor of Emperor",
	    "Endurance",
	    "Enmity",
	    "Enter The Dragon",
	    "Execution",
	    "Eye of Typhoon",
	    "Face Off",
	    "Fading Realm",
	    "Fire Walker",
	    "Flight-Dreamliner",
	    "Forbidden City",
	    "Full Circle",
	    "Gauntlet",
	    "Gemini Station",
	    "Ghost Path",
	    "Giant Steps",
	    "Hawkeye",
	    "Heartbreak Ridge",
	    "Hellhole",
	    "Highway Star",
	    "Hitchhiker",
	    "Hwarangdo",
	    "Ice Floes",
	    "Into the Darkness",
	    "Isolation",
	    "Jacob's Ladder",
	    "King of the Abyss",
	    "Korhal of Ceres",
	    "Loki",
	    "Match Point",
	    "Midnight Lagoon",
	    "Mist",
	    "Monte Cristo",
	    "Monty Hall",
	    "Nightlight",
	    "Odd-Eye",
	    "Operation Claws",
	    "Overwatch",
	    "Oxide",
	    "Paradoxxx",
	    "Paranoid Android",
	    "Peaks of Baekdu",
	    "Polaris Rhapsody",
	    "Pridelands",
	    "Queensbridge",
	    "Raid Assault",
	    "Resonance II",
	    "Ride of Valkyries",
	    "River Crossing",
	    "River Styx",
	    "Road War",
	    "Roads to Antiga Prime",
	    "Showdown",
	    "Sidewinder",
	    "Signal",
	    "Solar Station",
	    "Sound Barrier",
	    "Space Madness",
	    "Spinel Valley III",
	    "Steel Wall",
	    "Switchback",
	    "The Small Divide",
	    "Tiamat",
	    "Turnaround",
	    "Twisted Fate",
	    "Twisted Trail",
	    "Up the Creek",
	    "Valley Of Wind",
	    "Volcanis",
	    "Zerg Soccer"
	};
	
	final static String[] MAP_NAMES_3_PLAYER = {
	    "Alchemist",
	    "Another Day",
	    "Athena",
	    "Aztec",
	    "Calm Breeze",
	    "Central Plains",
	    "Crimson Isles",
	    "Demian",
	    "Demon's Forest",
	    "Demon's Shiver",
	    "Desert Fox",
	    "El Niño",
	    "Hazard Black",
	    "Holy Ground",
	    "Ice Mountain",
	    "Industrial Revolution",
	    "Inferno",
	    "Longinus",
	    "Medusa",
	    "Meltdown",
	    "Mercenaries",
	    "Mercenaries II",
	    "Moon Glaive",
	    "Mouse Trap",
	    "Outburst",
	    "Outlier",
	    "Outsider",
	    "Overlook",
	    "Paranoid Android",
	    "Pathfinder",
	    "Pinnacle",
	    "Plasma",
	    "Rush Hour",
	    "Slow Burn",
	    "Stepping Stones",
	    "Tau Cross",
	    "Tears of the Moon",
	    "Three Kingdoms",
	    "Trench Wars",
	    "Triad",
	    "Triathlon",
	    "Triskelion",
	    "Triumvirate",
	    "Universal Tripod",
	    "Whirlwind"
	};
	
	final static String[] MAP_NAMES_4_PLAYER = {
	    "A Bridge Too Near",
	    "Acro",
	    "Adrenaline Rush",
	    "Alpha Draconis",
	    "Andromeda",
	    "Arcadia",
	    "Archipelago",
	    "Arctic Station",
	    "Arena",
	    "Arizona",
	    "Arkanoid",
	    "Ashrigo",
	    "Astral Eclipse",
	    "Avalon",
	    "Avant Garde",
	    "Avatar",
	    "Azalea",
	    "Back and Belly",
	    "Battle of Salsu",
	    "Beltway",
	    "Black Vane",
	    "Blade Storm",
	    "Blaze",
	    "Blood Bath",
	    "Bone Canyon",
	    "Boxed In",
	    "Brushfire",
	    "Bull's-eye",
	    "Byzantium",
	    "Caldera",
	    "Catwalk Alley",
	    "Chaotic Surface",
	    "Chariots of Fire",
	    "Charity",
	    "Christmas Rush",
	    "Circuit Breaker",
	    "Colosseum",
	    "Crescent Moon",
	    "Cross the Line",
	    "Crossfire",
	    "Crossroads",
	    "Crusader",
	    "Crusader",
	    "Cyclone",
	    "Dahlia of Jungle",
	    "Dante's Peak",
	    "Dark Crystal",
	    "Dark Star",
	    "Dark Stone",
	    "Dead End",
	    "Desert Lost Temple",
	    "DeserTec",
	    "Desolation",
	    "Desperado",
	    "Dire Straits",
	    "Dream of Balhae",
	    "Dust Bowl",
	    "Eddy",
	    "Electric Circuit",
	    "Empire of the Sun",
	    "Enarey",
	    "Entanglement",
	    "Erebos",
	    "Estrella",
	    "Eye in the Sky",
	    "Eye of the Storm",
	    "Fantasy",
	    "Faoi",
	    "Fighting Spirit",
	    "Forbidden Zone",
	    "Forest of Abyss",
	    "Forsaken Valley",
	    "Forte",
	    "Fortress",
	    "Frenzy",
	    "Frostbite",
	    "Gaema Gowon",
	    "Gaia",
	    "Gemlong",
	    "Geometry",
	    "Glacial Epoch",
	    "Glacier Bay",
	    "Gladiator",
	    "Gladiator Pits",
	    "God's Garden",
	    "Golden Cross",
	    "Gorky Island",
	    "Grand Canyon",
	    "Grand Line",
	    "Greener Pastures",
	    "Ground Zero",
	    "Guillotine",
	    "Hae Bing Gi",
	    "Hailstorm",
	    "Hall of Valhalla",
	    "Hannibal",
	    "Harmony",
	    "Heartwood",
	    "High Noon",
	    "Highland Denizens",
	    "Holy World",
	    "Hostile Waters",
	    "Hot Zone",
	    "Icarus",
	    "Incubus",
	    "Indian Lament",
	    "Inferno",
	    "Iron Curtain",
	    "Isle of Sirens",
	    "Jade",
	    "Jim Raynor's Memory",
	    "Judgement Day",
	    "Jungle Siege",
	    "Jungle Story",
	    "Kakaru Keys",
	    "Katrina",
	    "La Mancha",
	    "Lake Shore",
	    "Landslide",
	    "Legacy of Char",
	    "Lost Civilization",
	    "Lost Temple",
	    "Luna",
	    "Lycosidae",
	    "Martian Cross",
	    "Mausoleum",
	    "Melting Pot",
	    "Mengsk Pride",
	    "Mercury",
	    "Mesopotamia",
	    "Molten Playground",
	    "Mountain Stronghold",
	    "Mystique",
	    "Namja Iyagi",
	    "Nemesis",
	    "Nightfall",
	    "Nightmare Station",
	    "No Way Out",
	    "Nostalgia",
	    "Nova Station",
	    "Obsidian",
	    "Odin",
	    "Odyssey",
	    "Old Tornado",
	    "Opposing City States '98",
	    "Orbital Gully",
	    "Orbital Relay",
	    "Othello",
	    "Pamir Plateau",
	    "Parallel Lines",
	    "Path of Sorrow",
	    "Pelennor",
	    "Perdition's Flame",
	    "Permafrost",
	    "Persona",
	    "Pinwheel",
	    "Pioneer Period",
	    "Plains to Hill",
	    "Power Lines",
	    "Proving Grounds",
	    "Pyroclasm",
	    "Python",
	    "R-Point",
	    "Ragnarok",
	    "Ramparts",
	    "Red Canyons",
	    "Remote Outpost",
	    "Requiem",
	    "Return of the King",
	    "Reverse Temple",
	    "Ridge to Ridge",
	    "Rivalry",
	    "River Lethe",
	    "River of Flames",
	    "Road to Nowhere",
	    "Roadkill",
	    "Roadrunner",
	    "Ruins of the Ancients",
	    "Sanctuary",
	    "Sapphire",
	    "Sarengo Canyon",
	    "Satterchasm",
	    "Sauron",
	    "Scorched Earth",
	    "Seongangil",
	    "Shades of Twilight",
	    "Shadowlands",
	    "Shakras",
	    "Shoreline",
	    "Shroud Platform",
	    "Silent Vortex",
	    "Sniper Ridge",
	    "Snowbound",
	    "Space Atoll",
	    "Space Debris",
	    "Space Odyssey",
	    "Spring Thaw",
	    "Steal the Beacon"
	};
	
	final static String[] MAP_NAMES_5_PLAYER = {
	    "Broken Mesa",
	    "Desert Bloom",
	    "Diablo",
	    "Ebon Lakes",
	    "Hidden Shrine",
	    "Hwangsanbul",
	    "Island Hop",
	    "Jeweled River",
	    "Lake of Fire",
	    "Predators",
	    "Ricochet",
	    "Rock Garden",
	    "Rosewood",
	    "Sandstorm",
	    "Sherwood Forest",
	    "Twilight Star",
	    "Typhoon"
	};
	
	final static String[] MAP_NAMES_6_PLAYER = {
	    "Acropolis",
	    "Across the Cape",
	    "Aftershock",
	    "All Your Base",
	    "Avalanche",
	    "Azure Plains",
	    "Bazaar",
	    "Blast Furnace",
	    "Blizzard",
	    "Bunker Command",
	    "Cauldron",
	    "Chain Lightning",
	    "Close Encounters",
	    "Crash Sites",
	    "Crazy Critters",
	    "Cutthroat",
	    "Dark Masters",
	    "Deep Freeze",
	    "Deep Purple",
	    "Divided Factions",
	    "DMZ",
	    "Easy Money",
	    "Egg Madness",
	    "End of the Line",
	    "Exile Station",
	    "Firestorm",
	    "Flooded Plains",
	    "Full Moon",
	    "Funeral Pyre",
	    "Ghost Town",
	    "Ground Zero",
	    "Hypothermia",
	    "Indigo Waters",
	    "Klondike",
	    "Last One Standing",
	    "Legacy",
	    "Medusa",
	    "Mobius",
	    "New Gettysburg",
	    "New Venice",
	    "Outbreak",
	    "Pit Traps",
	    "Polaris Prime",
	    "Razor Thicket",
	    "Revolution",
	    "River of Light",
	    "Sapphire Isles",
	    "Schism",
	    "Sirocco",
	    "Tale of Two Cities",
	    "The Huntress",
	    "The Void",
	    "Thin Ice",
	    "Trade Masters",
	    "Triple Team",
	    "Valley of Re",
	    "Wide Horizons",
	    "Winter Conquest"
	};
	
	final static String[] MAP_NAMES_7_PLAYER = {
	    "Black Lotus",
	    "Broken Steppes",
	    "Continental Divide",
	    "Heart Attack",
	    "Hot Spot",
	    "Mirage",
	    "Nightshade",
	    "River War",
	    "Seven Sins",
	    "Ursadon Flats",
	    "Zephyr Cove"
	};
	
	final static String[] MAP_NAMES_8_PLAYER = {
	    "Abbatoir",
	    "Allied Fortress",
	    "Aurora",
	    "Backlash",
	    "Backwoods",
	    "Battle Lines",
	    "Black Hole",
	    "Border Dispute",
	    "Bridge to Bridge Combat '98",
	    "Bunker Command II",
	    "Char Magma",
	    "Crystal Castles",
	    "Dark Continent",
	    "Double Diamond",
	    "Elderlands",
	    "Enigma",
	    "Eruption",
	    "Expedition",
	    "Friends '98",
	    "Friends in Space",
	    "Frozen Sea",
	    "Garden of Aiur",
	    "Green Valleys",
	    "Hellfire",
	    "Homeworld",
	    "Ice Age",
	    "Jungle Rumble",
	    "Kaleidoscope",
	    "Killing Fields",
	    "Labyrinth",
	    "Meatgrinder",
	    "Megalopolis",
	    "Multiplication",
	    "New Aegean Sea",
	    "Nukes Away",
	    "Octopus",
	    "Orbital Death",
	    "Plains of Snow '98",
	    "Primeval Isles",
	    "River Runs Through It",
	    "Scorpion Ravine",
	    "Station Unrest",
	    "The Hunters",
	    "Theatre of War",
	    "Tribes",
	    "Tropical Seas",
	    "Turbo",
	    "Untamed Wilds",
	    "Wheel of War"
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
	
	public static boolean isMapName(String string){
		string = string.trim().toLowerCase();
		
		return (
				(Arrays.binarySearch(MAP_NAMES_2_PLAYER, string) >= 0) ||
				(Arrays.binarySearch(MAP_NAMES_3_PLAYER, string) >= 0) ||
				(Arrays.binarySearch(MAP_NAMES_4_PLAYER, string) >= 0) ||
				(Arrays.binarySearch(MAP_NAMES_5_PLAYER, string) >= 0) ||
				(Arrays.binarySearch(MAP_NAMES_6_PLAYER, string) >= 0) ||
				(Arrays.binarySearch(MAP_NAMES_7_PLAYER, string) >= 0) ||
				(Arrays.binarySearch(MAP_NAMES_8_PLAYER, string) >= 0)
				);
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
		
		// set all map names to lower case
		for(int i = 0; i < MAP_NAMES_2_PLAYER.length; ++i){
			MAP_NAMES_2_PLAYER[i] = MAP_NAMES_2_PLAYER[i].toLowerCase();
		}
		for(int i = 0; i < MAP_NAMES_3_PLAYER.length; ++i){
			MAP_NAMES_3_PLAYER[i] = MAP_NAMES_3_PLAYER[i].toLowerCase();
		}
		for(int i = 0; i < MAP_NAMES_4_PLAYER.length; ++i){
			MAP_NAMES_4_PLAYER[i] = MAP_NAMES_4_PLAYER[i].toLowerCase();
		}
		for(int i = 0; i < MAP_NAMES_5_PLAYER.length; ++i){
			MAP_NAMES_5_PLAYER[i] = MAP_NAMES_5_PLAYER[i].toLowerCase();
		}
		for(int i = 0; i < MAP_NAMES_6_PLAYER.length; ++i){
			MAP_NAMES_6_PLAYER[i] = MAP_NAMES_6_PLAYER[i].toLowerCase();
		}
		for(int i = 0; i < MAP_NAMES_7_PLAYER.length; ++i){
			MAP_NAMES_7_PLAYER[i] = MAP_NAMES_7_PLAYER[i].toLowerCase();
		}
		for(int i = 0; i < MAP_NAMES_8_PLAYER.length; ++i){
			MAP_NAMES_8_PLAYER[i] = MAP_NAMES_8_PLAYER[i].toLowerCase();
		}
	}
}
