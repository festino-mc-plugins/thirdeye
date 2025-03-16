package festp.top;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import festp.Logger;

public class TopUtils {
	
	public static final Statistic MATERIAL_STATISTIC_LIST[] = {
			Statistic.MINE_BLOCK, Statistic.BREAK_ITEM, Statistic.CRAFT_ITEM,
			Statistic.USE_ITEM, Statistic.PICKUP, Statistic.DROP };
	
	public static <T> boolean contains(T[] arr, T elem) {
		for (T el : arr)
			if (el.equals(elem))
				return true;
		return false;
	}
	
	public static List<EntityType> getMobStatisticList() {
		List<EntityType> list = new ArrayList<>();
		OfflinePlayer p = null;
		//Player p = null;
		try {
			OfflinePlayer[] off_players = Bukkit.getServer().getOfflinePlayers();
			if (off_players.length > 0) { // on first server launch can't load specific mob kills
				//p = OfflinePlayerLoader.loadPlayer(off_players[0]);
				p = off_players[0];
			}
		} catch (Exception e) {
			Logger.warning("[ThirdEye] Couldn't load player statistic for \"TopUtils.getMobStatisticList()\"");
		}
			
		if (p != null) {
			for (EntityType et : EntityType.values()) {
				try {
					p.getStatistic(Statistic.KILL_ENTITY, et);
				} catch (Exception e) {
					Logger.warning("[ThirdEye] Can't get Statistic.KILL_ENTITY - " + et);
					continue;
				}
				list.add(et);
			}
		}
		return list;
	}
	
	public static List<Material> getMaterialStatisticList(Statistic stat)
	{
		List<Material> list = new ArrayList<>();
		if (!TopUtils.contains(MATERIAL_STATISTIC_LIST, stat))
			return list;
					
		//Player p = null;
		OfflinePlayer p = null;
		try {
			OfflinePlayer[] off_players = Bukkit.getServer().getOfflinePlayers();
			if (off_players.length > 0) { // on first server launch can't load specific mob kills
				//p = OfflinePlayerLoader.loadPlayer(off_players[0]);
				p = off_players[0];
			}
		} catch (Exception e) {
			Logger.warning("[ThirdEye] Couldn't load player statistic for \"TopUtils.getMaterialStatisticList(" + stat + ")\"");
		}
			
		if (p != null) {
			for (Material m : Material.values()) {
				try {
					p.getStatistic(stat, m);
				} catch (Exception e) {
					Logger.warning("[ThirdEye] Can't get Statistic." + stat + " - " + m);
					continue;
				}
				list.add(m);
			}
		}
		return list;
	}
}
