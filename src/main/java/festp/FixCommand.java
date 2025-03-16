package festp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import festp.utils.OfflinePlayerLoader;
import net.md_5.bungee.api.ChatColor;

public class FixCommand implements  CommandExecutor {
	private Server server;
	
	public FixCommand(Server server) {
		this.server = server;
	}
	
	private boolean isWall(Material m) {
		// is switch faster?
		return m.toString().endsWith("WALL");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if (!sender.isOp()) {
			
			return false;
		}
		
		if(args.length > 1)
		{
			if(args[0].equalsIgnoreCase("ms") || args[0].equalsIgnoreCase("movespeed") || args[0].equalsIgnoreCase("movementspeed")) {
				Player p = null;
				for(Player pl : server.getOnlinePlayers())
				{
					if(args[1].equalsIgnoreCase(pl.getName()))
					{
						p = pl.getPlayer();
						break;
					}
				}
				if(p == null)
					for(OfflinePlayer offp : server.getOfflinePlayers())
					{
						if(args[1].equalsIgnoreCase(offp.getName()))
						{
							p = OfflinePlayerLoader.loadPlayer(offp);
							break;
						}
					}
				if(p != null) {
					double new_speed = 0.1;
					if(args.length > 2) {
						try {
							new_speed = Double.parseDouble(args[2]);
						} catch(Exception e) {
							sender.sendMessage(ChatColor.RED+"Invalid movement speed value (arg 2: \""+args[2]+"\"");
							return false;
						}
					}
					//p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(new_speed);
					//sender.sendMessage("Movement speed of "+args[1]+" have been set to "+new_speed);
					sender.sendMessage(org.bukkit.ChatColor.RED + "Feature is not supported");
					return true;
				}
				sender.sendMessage(ChatColor.RED + "����� �� ������");
				sender.sendMessage(ChatColor.RED + "�������������: /fix ms <nickname> [value] - �������� ����");
				return true;
			}
					
			Material m;
			if (args[0].equalsIgnoreCase("lava")) {
				m = Material.LAVA; 
			} else if (args[0].equalsIgnoreCase("water")) {
				m = Material.WATER;
			} else if (args[0].equalsIgnoreCase("walls")) {
				m = Material.ANDESITE_WALL;
			} else {
				sender.sendMessage(ChatColor.RED + "�������������: /fix <lava/water/walls> <r>");
				return false;
			}
			
			if (m == Material.WATER || m == Material.LAVA) {
				double r = Double.parseDouble(args[1]);
				Player player = (Player) sender;
				int x0 = player.getLocation().getBlockX();
				int y0 = player.getLocation().getBlockY();
				int z0 = player.getLocation().getBlockZ();
				World world = player.getWorld();
				for (int i = (int)-r; i <= r; i++) {
					for (int j = (int)-r; j <= r; j++) {
						for (int k = (int)-r; k <= r; k++) {
							Block block = world.getBlockAt(x0 + i, y0 + j, z0 + k);
							if (block.getType() == m)
								block.setType(Material.AIR);
						}
					}
				}
			} else if (m == Material.ANDESITE_WALL) {
				double r = Double.parseDouble(args[1]);
				Player player = (Player) sender;
				int x0 = player.getLocation().getBlockX();
				int y0 = player.getLocation().getBlockY();
				int z0 = player.getLocation().getBlockZ();
				World world = player.getWorld();
				for (int i = (int)-r; i <= r; i++) {
					for (int j = (int)-r; j <= r; j++) {
						for (int k = (int)-r; k <= r; k++) {
							Block block = world.getBlockAt(x0 + i, y0 + j, z0 + k);
							if (isWall(block.getType())) {
								/*m = block.getType();
								block.setType(Material.AIR);
								block.setType(m); // breaks walls
								block.getState().update(); // no effect*/
								// Wall wall = (Wall) block.getBlockData();
								String wallName = block.getType().toString().toLowerCase();
								String setblockCommand = String.format("setblock %d %d %d %s", x0 + i, y0 + j, z0 + k, wallName);
								Bukkit.dispatchCommand(sender, setblockCommand);
							}
						}
					}
				}
			}
			return true;
		}
		sender.sendMessage(ChatColor.RED + "�������������: /fix <lava/water> <r> - ������� ����/����");
		sender.sendMessage(ChatColor.RED + "�������������: /fix <walls> <r> - ����� ������ �� 1.16");
		sender.sendMessage(ChatColor.RED + "�������������: /fix ms <nickname> [value] - �������� ����");
		return false;
	}
}
