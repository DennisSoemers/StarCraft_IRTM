package irtm.starcraft.utils;

/**
 * Class with some utility methods for dealing with HTML
 * 
 * @author Soemers
 *
 */
public class HtmlUtils {
	
	/**
	 * Returns true iff the given tag name is a header tag (h1, h2, ..., h6)
	 * 
	 * @param tagName
	 * @return
	 */
	public static boolean isHeaderTag(String tagName){
		return (
				tagName.equals("h1") ||
				tagName.equals("h2") ||
				tagName.equals("h3") ||
				tagName.equals("h4") ||
				tagName.equals("h5") ||
				tagName.equals("h6")
				);
	}
	
	/**
	 * Returns true iff the given tag name is a list tag (ul, ol)
	 * 
	 * @param tagName
	 * @return
	 */
	public static boolean isListTag(String tagName){
		return (tagName.equals("ul") ||
				tagName.equals("ol")
				);
	}

}
