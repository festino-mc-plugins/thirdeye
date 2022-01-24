package com.te.utils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StatReader {
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
	
	public static JsonObject read(UUID playerUUID)
	{
		File statsDir = getStatsDir();
		String formattedUUID = playerUUID.toString(); // 8-4-4-4-12
		File playerFile = new File(statsDir, formattedUUID + ".json");
		try {
			JsonObject parsed = (JsonObject) JsonParser.parseReader(new FileReader(playerFile));
			return (JsonObject) parsed.get("stats");
		} catch (Exception e) {
			return null;
		}
	}
	
	public static long readLong(JsonObject stats, String section, String item)
	{
		try {
			JsonObject jsonSection = (JsonObject) stats.get(section);
			return jsonSection.get(item).getAsLong();
		} catch (Exception e) {
			//throw new Exception("Couldn't read \"stats/" + section + "/" + "item\", error: " + e.getMessage());
			return 0;
		}
	}
}
