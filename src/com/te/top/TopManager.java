package com.te.top;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.te.OfflinePlayerLoader;
import com.te.top.TopTabCompleter.Option;

import net.md_5.bungee.api.ChatColor;

public class TopManager implements CommandExecutor {
	public static final Statistic MATERIAL_STATISTIC_LIST[] = {
			Statistic.MINE_BLOCK, Statistic.BREAK_ITEM, Statistic.CRAFT_ITEM,
			Statistic.USE_ITEM, Statistic.PICKUP, Statistic.DROP };
	
	int topSize = 5;
	int top_update_maxticks = 216000, top_update_ticks = 0; //20*60*60*3 (3 hours)
	JavaPlugin plugin;
	
	List<RatingTop> tops = new ArrayList<>();
	TopTabCompleter completer = new TopTabCompleter();
	
	public TopManager(JavaPlugin plugin) {
		this.plugin = plugin;

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
		for (EntityType et : getMobStatisticList()) {
			tops.add(new VanillaRatingTop(Statistic.KILL_ENTITY, "killmob", et.toString().toLowerCase(),
					"Топ по убийствам " + et.toString().toLowerCase().replace('_', ' ') + ":", 
					" место занимает игрок ", " место занимают игроки ", 
					", у которого ", ", у которых ", 
					" убийство.", " убийства.", " убийств."));
		}
		for (Statistic stat : MATERIAL_STATISTIC_LIST) {
			if (stat == Statistic.PICKUP || stat == Statistic.DROP)
				continue; // skip boring statistics
			for (Material m : getMaterialStatisticList(stat)) {
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
		
		top_update_ticks = 0; // delayed update due to CraftServer.getOfflinePlayers()
	}
	
	public void tick() {
		top_update_ticks--;
		if(top_update_ticks <= 0) {
			top_update_ticks = top_update_maxticks;
			update_tops();
		}
	}
	
	public void setSize(int size) {
		topSize = size;
	}

	public TabCompleter getCompleter() {
		return completer;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length == 0 || args.length > 0 && args[0].startsWith("?"))
		{
			//((EntityPlayer)((CraftPlayer)sender).getHandle()).fauxSleeping = true
			printTops(sender);
		}
		else if (args.length == 1)
		{
			for (RatingTop rt : tops)
				if (args[0].equalsIgnoreCase(rt.main_activator)) {
					rt.output_to_player(sender, topSize);
					return true;
				}
			sender.sendMessage("Топа с таким определителем не существует.");
			printTops(sender);
			return true;
		}
		else if (args.length == 2)
		{
			for (RatingTop rt : tops) {
				if (rt.add_activator != null) {
					if (args[0].equalsIgnoreCase(rt.main_activator) && args[1].equalsIgnoreCase(rt.add_activator)) {
						rt.output_to_player(sender, topSize);
						return true;
					}
				}
			}
			sender.sendMessage("Топа с таким определителем не существует.");
			printTops(sender);
			return true;
		}
		return true;
	}
	
	public void printTops(CommandSender sender) {
		sender.sendMessage(ChatColor.GRAY+"Доступные топы:");
		String list = "";
		boolean first = true;
		for (RatingTop rt : tops) {
			int index = list.indexOf(rt.main_activator);
			if (index < 0) {
				if (first) {
					first = false;
				} else {
					list += ", ";
				}
				list += rt.main_activator;
			} else {
				if (list.indexOf(rt.main_activator + "(с подвариантами)") != index) {
					index = index + rt.main_activator.length();
					if (index != list.length() && list.charAt(index) != ',')
					list = list.substring(0, index) + "(с подвариантами)" + list.substring(index);
				}
			}
		}
		sender.sendMessage(ChatColor.GRAY + list);
	}

	public void update_tops() {
		for (RatingTop rt : tops) {
			for (int i = 0; i < rt.places.length; i++) {
				if (rt.places[i] == null) // end of spend places
					break;
				rt.places[i] = null;
			}
		}
		
		new Thread(new Runnable() {
		    public void run() {
		     	try {
		     		int error_count = 0, player_count = 0;
		     		for (OfflinePlayer p : plugin.getServer().getOfflinePlayers())
					{
						player_count++;
						Player pl;
						if (p.isOnline()) {
							pl = p.getPlayer();
						} else {
							try {
								pl = OfflinePlayerLoader.loadPlayer(p.getUniqueId());
							} catch(Exception e) {
								e.printStackTrace();
								error_count++;
								continue;
							}
						}
						
						for(RatingTop rt : tops) {
							rt.getStatistic(pl);
						}
						
					}
					System.out.println("[ThirdEye] Tops: " + player_count + " players, " + error_count + " errors");
				} catch (Exception e) {
					System.out.println("[ThirdEye] Tops: 1 big error");
					e.printStackTrace();
				}
		     	
		     	completer.setOptions(genOptions());
		     	//System.out.print("FINISHED " + Thread.currentThread() + " (" + Thread.activeCount() + ")");
		    }
		}).start();
	}
	
	private List<TopTabCompleter.Option> genOptions() {
		List<Option> options = new ArrayList<>();
		
		for (RatingTop rt : tops) {
			if (rt.places[0] == null) {
				continue;
			}
			
			boolean suboption = false;
			for (Option op : options) {
				if (op.option.equals(rt.main_activator)) {
					suboption = true;
					if (op.suboptions == null) {
						op.suboptions = new ArrayList<>();
					}
					Option new_op = new Option(rt.add_activator);
					op.suboptions.add(new_op);
				}
			}
			
			if (!suboption) {
				Option new_op = new Option(rt.main_activator);
				options.add(new_op);
				if (rt.add_activator != null) {
					if (new_op.suboptions == null) {
						new_op.suboptions = new ArrayList<>();
					}
					Option sub_op = new Option(rt.add_activator);
					new_op.suboptions.add(sub_op);
				}
			}
		}

		return options;
	}
	
	private List<EntityType> getMobStatisticList() {
		List<EntityType> list = new ArrayList<>();
		Player p = null;
		try {
			OfflinePlayer[] off_players = plugin.getServer().getOfflinePlayers();
			if (off_players.length > 0) { // on first server launch can't load specific mob kills
				p = OfflinePlayerLoader.loadPlayer(off_players[0]);
			}
		} catch (Exception e) {
			System.out.print("[ThirdEye] Can't load player statistic");
		}
			
		if (p != null) {
			for (EntityType et : EntityType.values()) {
				try {
					p.getStatistic(Statistic.KILL_ENTITY, et);
				} catch (Exception e) {
					System.out.print("[ThirdEye] Can't get Statistic.KILL_ENTITY - " + et);
					continue;
				}
				list.add(et);
			}
		}
		return list;
	}
	
	private List<Material> getMaterialStatisticList(Statistic stat)
	{
		List<Material> list = new ArrayList<>();
		if (!UtilsTop.contains(MATERIAL_STATISTIC_LIST, stat))
			return list;
					
		Player p = null;
		try {
			OfflinePlayer[] off_players = plugin.getServer().getOfflinePlayers();
			if (off_players.length > 0) { // on first server launch can't load specific mob kills
				p = OfflinePlayerLoader.loadPlayer(off_players[0]);
			}
		} catch (Exception e) {
			System.out.print("[ThirdEye] Can't load player statistic");
		}
			
		if (p != null) {
			for (Material m : Material.values()) {
				try {
					p.getStatistic(stat, m);
				} catch (Exception e) {
					System.out.print("[ThirdEye] Can't get Statistic." + stat + " - " + m);
					continue;
				}
				list.add(m);
			}
		}
		return list;
	}
}
