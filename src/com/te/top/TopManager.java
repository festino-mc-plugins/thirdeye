package com.te.top;

import java.util.ArrayList;
import java.util.List;

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
							int value;
							if (rt instanceof VanillaRatingTop) {
								VanillaRatingTop vrt = (VanillaRatingTop) rt;
								if (vrt.criteria == Statistic.KILL_ENTITY && vrt.subcriteria instanceof EntityType) {
									value = pl.getStatistic(vrt.criteria, (EntityType) vrt.subcriteria);
								} else {
									value = pl.getStatistic(vrt.criteria);
								}
							} else {
								value = -1;
							}
							rt.try_add( value, pl.getName() );
						}
						
					}
					System.out.println("[ThirdEye] Tops: " + player_count + " players, " + error_count + " errors");
				} catch (Exception e) {
					System.out.println("[ThirdEye] Tops: 1 big error");
					e.printStackTrace();
				}
		     	
		     	completer.setOptions(genOptions());
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
}
