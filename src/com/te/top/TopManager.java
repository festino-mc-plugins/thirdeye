package com.te.top;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public class TopManager {
	int topSize = 5;
	
	//int top_update_maxticks = 216000, top_update_ticks = 0; //20*60*60*3 (3 hours)
	
	List<RatingTop> tops = new ArrayList<>();
	TopCommand command = new TopCommand(this);
	TopTabCompleter completer = new TopTabCompleter(this);
	TopUpdater updater = new TopUpdater(this);
	
	public TopManager() {

		tops.add(new VanillaRatingTop(Statistic.PLAY_ONE_MINUTE, "time", "Топ по времени:",
				" место занимает игрок ", " место занимают игроки ",
				", у которого ", ", у которых ",
				" час на сервере.", " часа на сервере.", " часов на сервере."));
		tops.add(new VanillaRatingTop(Statistic.PLAYER_KILLS, "kill", "Топ по убийствам игроков:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" убийство.", " убийства.", " убийств."));
		tops.add(new VanillaRatingTop(Statistic.DEATHS, "death", "Топ по смертям:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" смерть.", " смерти.", " смертей."));
		
		tops.add(new VanillaRatingTop(Statistic.WALK_ONE_CM, "distance", "walk", "Топ по ходьбе:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.PIG_ONE_CM, "distance", "pig", "Топ по свиновой езде:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.SPRINT_ONE_CM, "distance", "sprint", "Топ по бегу:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.CROUCH_ONE_CM, "distance", "crouch", "Топ по ходьбе крадучись:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.FALL_ONE_CM, "distance", "fall", "Топ по падению:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.HORSE_ONE_CM, "distance", "horse", "Топ по верховой езде:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.WALK_ON_WATER_ONE_CM, "distance", "onwater", "Топ по ходьбе в воде:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.WALK_UNDER_WATER_ONE_CM, "distance", "underwater", "Топ по ходьбе под водой:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.SWIM_ONE_CM, "distance", "swim", "Топ пловцов:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.FLY_ONE_CM, "distance", "fly", "Топ по креативщикам(вас ждёт бан):", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.AVIATE_ONE_CM, "distance", "elytra", "Топ по летучкам:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.BOAT_ONE_CM, "distance", "boat", "Топ по лодочникам:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.CLIMB_ONE_CM, "distance", "climb", "Топ по лестницелазам:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		tops.add(new VanillaRatingTop(Statistic.MINECART_ONE_CM, "distance", "minecart", "Топ по шахтёрам:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" м.", " м.", " м."));
		
		tops.add(new VanillaRatingTop(Statistic.MOB_KILLS, "killmob", "Топ по убийствам мобов:", 
				" место занимает игрок ", " место занимают игроки ", 
				", у которого ", ", у которых ", 
				" убийство.", " убийства.", " убийств."));
		for (EntityType et : TopUtils.getMobStatisticList()) {
			tops.add(new VanillaRatingTop(Statistic.KILL_ENTITY, "killmob", et.toString().toLowerCase(),
					"Топ по убийствам " + et.toString().toLowerCase().replace('_', ' ') + ":", 
					" место занимает игрок ", " место занимают игроки ", 
					", у которого ", ", у которых ", 
					" убийство.", " убийства.", " убийств."));
		}
		for (Statistic stat : TopUtils.MATERIAL_STATISTIC_LIST) {
			if (stat == Statistic.PICKUP || stat == Statistic.DROP)
				continue; // skip meanless statistics
			for (Material m : TopUtils.getMaterialStatisticList(stat)) {
				String top_activator = stat.toString();
				String top_name = "ERROR_NAME(" + stat + ")";
				String[] end = {"END1", "END2_4", "END5_10"};
				if (stat == Statistic.BREAK_ITEM) {
					top_activator = "ibreak";
					top_name = "сломанным";
					end[0] = "предмет"; end[1] = "предмета"; end[2] = "предметов"; // TODO remove russian feature
				} else if (stat == Statistic.MINE_BLOCK) {
					top_activator = "imine";
					top_name = "выкопанным";
					end[0] = "блок"; end[1] = "блока"; end[2] = "блоков";
				} else if (stat == Statistic.CRAFT_ITEM) {
					top_activator = "icraft";
					top_name = "созданным";
					end[0] = "предмет"; end[1] = "предмета"; end[2] = "предметов";
				} else if (stat == Statistic.USE_ITEM) {
					top_activator = "iuse";
					top_name = "использованным";
					end[0] = "использование"; end[1] = "использования"; end[2] = "использований";
				}
				
				String material_name = m.toString().toLowerCase().replace('_', ' ');
				tops.add(new VanillaRatingTop(stat, top_activator, m.toString().toLowerCase(),
						"Топ по " + top_name + " " + material_name + ":", 
						" место занимает игрок ", " место занимают игроки ", 
						", у которого ", ", у которых ", 
						" " + end[0] + ".", " " + end[1] + ".", " " + end[2] + "."));
			}
		}

		updateTops();
		//top_update_ticks = 0; // delayed update due to CraftServer.getOfflinePlayers()
	}
	
	public void tick() {
		/*top_update_ticks--;
		if(top_update_ticks <= 0) {
			top_update_ticks = top_update_maxticks;
			update_tops();
		}*/
	}

	public void setSize(int size) {
		topSize = size;
	}

	public int getSize() {
		return topSize;
	}
	
	public RatingTop[] getTops() {
		return tops.toArray(new RatingTop[0]);
	}

	public TopCommand getExecutor() {
		return command;
	}

	public TopUpdater getUpdater() {
		return updater;
	}

	public TopTabCompleter getTabCompleter() {
		return completer;
	}

	/** <b>Warning:</b> Cause memory leak*/
	public void updateTops() {
		updater.update_tops();
	}
}
