package io.jenkins.plugins.build.environment;

import java.util.LinkedHashMap;

public class BuildGoalUtil {
	
	public static LinkedHashMap<String, String> getData() {
		LinkedHashMap<String, String> items = new LinkedHashMap<String, String>();
		items.put("Build Goal", "buildGoal"); // (usedForDisplayName, value)
		items.put("FindBugs goal", "findBugsGoal");
		return items;
	}
	
	public static LinkedHashMap<String, String> getServerData() {
		LinkedHashMap<String, String> items = new LinkedHashMap<String, String>();
		/* http://ip-api.com/json
		 * http://ipinfo.io/json
		 */
		//String response = "[['Build Goal', 'buildGoal'], ['FindBugs goal', 'findBugsGoal']]";
		//JSONArray arr = new org.json.JSONArray( response );
		items.put("Build Goal", "buildGoal");
		items.put("FindBugs goal", "findBugsGoal");
		return items;
	}
}
