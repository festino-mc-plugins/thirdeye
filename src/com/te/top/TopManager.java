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
		RatingTop top = new VanillaRatingTop(Statistic.PLAY_ONE_MINUTE, "time");
		top.setDecorators("Топ по времени:", " место занимает игрок ", ", у которого ", " часов на сервере.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.PLAYER_KILLS, "kill");
		top.setDecorators("Топ по убийствам игроков:", " место занимает игрок ", ", убивший ", " раз.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.DEATHS, "death");
		top.setDecorators("Топ по смертям:", " место занимает игрок ", ", умерший ", " раз.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.JUMP, "jump");
		top.setDecorators("Топ прыгателей:", " место занимает игрок ", ", прыгнувший ", " раз.");
		tops.add(top);
		
		top = new VanillaRatingTop(Statistic.WALK_ONE_CM, "distance", "walk");
		top.setDecorators("Топ по ходьбе:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.PIG_ONE_CM, "distance", "pig");
		top.setDecorators("Топ по свиновой езде:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.SPRINT_ONE_CM, "distance", "sprint");
		top.setDecorators("Топ по бегу:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.CROUCH_ONE_CM, "distance", "crouch");
		top.setDecorators("Топ по ходьбе крадучись:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.FALL_ONE_CM, "distance", "fall");
		top.setDecorators("Топ по падению:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.HORSE_ONE_CM, "distance", "horse");
		top.setDecorators("Топ по верховой езде:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.WALK_ON_WATER_ONE_CM, "distance", "onwater");
		top.setDecorators("Топ по ходьбе в воде:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.WALK_UNDER_WATER_ONE_CM, "distance", "underwater");
		top.setDecorators("Топ по ходьбе под водой:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.SWIM_ONE_CM, "distance", "swim");
		top.setDecorators("Топ пловцов:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.FLY_ONE_CM, "distance", "fly");
		top.setDecorators("Топ по креативщикам(вас ждёт бан):", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.AVIATE_ONE_CM, "distance", "elytra");
		top.setDecorators("Топ по летучкам:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.BOAT_ONE_CM, "distance", "boat");
		top.setDecorators("Топ по лодочникам:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.CLIMB_ONE_CM, "distance", "climb");
		top.setDecorators("Топ по лестницелазам:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.MINECART_ONE_CM, "distance", "minecart");
		top.setDecorators("Топ по шахтёрам:", " место занимает игрок ", ", у которого ", " м.");
		tops.add(top);

		top = new VanillaRatingTop(Statistic.MOB_KILLS, "killmob");
		top.setDecorators("Топ по убийствам мобов:", " место занимает игрок ", ", у которого ", " убийств.");
		tops.add(top);
		for (EntityType et : TopUtils.getMobStatisticList()) {
			top = new VanillaRatingTop(Statistic.KILL_ENTITY, "killmob", et.toString().toLowerCase());
			top.setDecorators("Топ по убийствам " + et.toString().toLowerCase().replace('_', ' ') + ":",
					" место занимает игрок ", ", у которого ", " убийств.");
			tops.add(top);
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
					end[0] = "предмет"; end[1] = "предмета"; end[2] = "предметов"; // TODO rework russian feature with formatted output
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
				top = new VanillaRatingTop(stat, top_activator, m.toString().toLowerCase());
				top.setDecorators("Топ по " + top_name + " " + material_name + ":",
						" место занимает игрок ", ", у которого ", " " + end[0] + ".");
				tops.add(top);
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

	/** <b>Warning:</b> May be slow*/
	public void updateTops() {
		updater.reloadTops_UsingStatFiles();
	}
}
