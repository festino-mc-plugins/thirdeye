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
		top.setDecorators("��� �� �������:", " ����� �������� ����� ", ", � �������� ", " ����� �� �������.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.PLAYER_KILLS, "kill");
		top.setDecorators("��� �� ��������� �������:", " ����� �������� ����� ", ", ������� ", " ���.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.DEATHS, "death");
		top.setDecorators("��� �� �������:", " ����� �������� ����� ", ", ������� ", " ���.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.JUMP, "jump");
		top.setDecorators("��� ����������:", " ����� �������� ����� ", ", ���������� ", " ���.");
		tops.add(top);
		
		top = new VanillaRatingTop(Statistic.WALK_ONE_CM, "distance", "walk");
		top.setDecorators("��� �� ������:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.PIG_ONE_CM, "distance", "pig");
		top.setDecorators("��� �� �������� ����:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.SPRINT_ONE_CM, "distance", "sprint");
		top.setDecorators("��� �� ����:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.CROUCH_ONE_CM, "distance", "crouch");
		top.setDecorators("��� �� ������ ���������:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.FALL_ONE_CM, "distance", "fall");
		top.setDecorators("��� �� �������:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.HORSE_ONE_CM, "distance", "horse");
		top.setDecorators("��� �� �������� ����:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.WALK_ON_WATER_ONE_CM, "distance", "onwater");
		top.setDecorators("��� �� ������ � ����:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.WALK_UNDER_WATER_ONE_CM, "distance", "underwater");
		top.setDecorators("��� �� ������ ��� �����:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.SWIM_ONE_CM, "distance", "swim");
		top.setDecorators("��� �������:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.FLY_ONE_CM, "distance", "fly");
		top.setDecorators("��� �� ������������(��� ��� ���):", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.AVIATE_ONE_CM, "distance", "elytra");
		top.setDecorators("��� �� ��������:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.BOAT_ONE_CM, "distance", "boat");
		top.setDecorators("��� �� ����������:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.CLIMB_ONE_CM, "distance", "climb");
		top.setDecorators("��� �� �������������:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);
		top = new VanillaRatingTop(Statistic.MINECART_ONE_CM, "distance", "minecart");
		top.setDecorators("��� �� �������:", " ����� �������� ����� ", ", � �������� ", " �.");
		tops.add(top);

		top = new VanillaRatingTop(Statistic.MOB_KILLS, "killmob");
		top.setDecorators("��� �� ��������� �����:", " ����� �������� ����� ", ", � �������� ", " �������.");
		tops.add(top);
		for (EntityType et : TopUtils.getMobStatisticList()) {
			top = new VanillaRatingTop(Statistic.KILL_ENTITY, "killmob", et.toString().toLowerCase());
			top.setDecorators("��� �� ��������� " + et.toString().toLowerCase().replace('_', ' ') + ":",
					" ����� �������� ����� ", ", � �������� ", " �������.");
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
					top_name = "���������";
					end[0] = "�������"; end[1] = "��������"; end[2] = "���������"; // TODO rework russian feature with formatted output
				} else if (stat == Statistic.MINE_BLOCK) {
					top_activator = "imine";
					top_name = "����������";
					end[0] = "����"; end[1] = "�����"; end[2] = "������";
				} else if (stat == Statistic.CRAFT_ITEM) {
					top_activator = "icraft";
					top_name = "���������";
					end[0] = "�������"; end[1] = "��������"; end[2] = "���������";
				} else if (stat == Statistic.USE_ITEM) {
					top_activator = "iuse";
					top_name = "��������������";
					end[0] = "�������������"; end[1] = "�������������"; end[2] = "�������������";
				}
				
				String material_name = m.toString().toLowerCase().replace('_', ' ');
				top = new VanillaRatingTop(stat, top_activator, m.toString().toLowerCase());
				top.setDecorators("��� �� " + top_name + " " + material_name + ":",
						" ����� �������� ����� ", ", � �������� ", " " + end[0] + ".");
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
