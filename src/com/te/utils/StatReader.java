package com.te.utils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class StatReader {
	private static JSONParser parser = new JSONParser();
	
	private static File getStatsDir()
	{
		File mainWorldDir = Bukkit.getWorlds().get(0).getWorldFolder();
		return new File(mainWorldDir, "stats");
	}
	
	public static UUID[] readAll()
	{
		File statsDir = getStatsDir();
		String[] names = statsDir.list();
		List<UUID> uuids = new ArrayList<>();
		for (int i = 0; i < names.length; i++) {
			try {
				uuids.add(UUID.fromString(names[i]));
			} catch (Exception e) {
				
			}
		}
		return uuids.toArray(new UUID[] {});
	}
	
	public static JSONObject read(UUID playerUUID)
	{
		File statsDir = getStatsDir();
		String formattedUUID = playerUUID.toString(); // 8-4-4-4-12
		File playerFile = new File(statsDir, formattedUUID + ".json");
		try {
			JSONObject parsed = (JSONObject) parser.parse(new FileReader(playerFile));
			return (JSONObject) parsed.get("stats");
		} catch (Exception e) {
			return null;
		}
	}
	
	public static long readLong(JSONObject stats, String section, String item)
	{
		try {
			JSONObject jsonSection = (JSONObject) stats.get(section);
			return (Long) jsonSection.get(item);
		} catch (Exception e) {
			//throw new Exception("Couldn't read \"stats/" + section + "/" + "item\", error: " + e.getMessage());
			return 0;
		}
	}
}
