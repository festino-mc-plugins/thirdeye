package com.te.top;

import java.util.UUID;

import org.bukkit.Statistic;
import org.json.simple.JSONObject;

import com.te.utils.StatReader;

public class JSONStatPlayer implements StatPlayer {
	private final String name;
	private final JSONObject player;
	
	public JSONStatPlayer(String name, UUID uuid) {
		this.name = name;
		player = StatReader.read(uuid);
	}
	
	@Override
	public String getName() {
		return name;
	}
	@Override
	public long getStat(Statistic criteria, Object subcriteria) {
		String section, item;
		section = sectionFromStatistic(criteria);
		if (subcriteria != null) {
			item = "minecraft:" + subcriteria.toString().toLowerCase();
		} else {
			item = "minecraft:" + criteria.toString().toLowerCase();
		}
		return StatReader.readLong(player, section, item);
	}
	
	private String sectionFromStatistic(Statistic criteria) {
		if (criteria == Statistic.CRAFT_ITEM)
			return "minecraft:crafted";
		if (criteria == Statistic.USE_ITEM)
			return "minecraft:used";
		if (criteria == Statistic.BREAK_ITEM)
			return "minecraft:broken";
		if (criteria == Statistic.MINE_BLOCK)
			return "minecraft:mined";
		if (criteria == Statistic.DROP)
			return "minecraft:dropped";
		if (criteria == Statistic.PICKUP)
			return "minecraft:picked_up";
		if (criteria == Statistic.KILL_ENTITY)
			return "minecraft:killed";
		if (criteria == Statistic.ENTITY_KILLED_BY)
			return "minecraft:killed_by";
		return "minecraft:custom";
	}

	@Override
	public boolean isValid() {
		return player != null;
	}
}
