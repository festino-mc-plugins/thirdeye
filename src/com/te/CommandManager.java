package com.te;

import java.io.File;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.te.top.TopManager;

import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {

	private static final String HAT_CANT_WEAR_BINDING = ChatColor.RED + "You can't wear cursed item.";
	private static final String HAT_CANT_UNWEAR_BINDING = ChatColor.RED + "You can't unwear binded helmet.";
	private static final String HAT_OK = ChatColor.GREEN + "Successfully have put a hat on the head.";
	
	public List<Camera> looking_players = new ArrayList<>();
	private TorchPlacer torches;
	private TopManager tops;
	private Main plugin;
	private Server server;
	
	public CommandManager(Main plugin, TorchPlacer torches, TopManager tops) {
		this.plugin = plugin;
		server = plugin.getServer();
		this.torches = torches;
		this.tops = tops;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("torch"))
		{
			if (sender instanceof Player)
			{
				Player p = (Player)sender;
				if (!p.getInventory().contains(Material.TORCH))
					sender.sendMessage(TorchPlacer.MSG_EMPTY);
				else if (torches.addPlayer(p))
					sender.sendMessage(TorchPlacer.MSG_START);
				else
				{
					torches.removePlayer(p);
					sender.sendMessage(TorchPlacer.MSG_END);
				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("te") && sender.isOp())
		{
			if (args.length == 1)
				if (args[0].equalsIgnoreCase("reload"))
				{
					plugin.reloadConfig();
					plugin.loadConfig();
					tops.update_tops();
					sender.sendMessage("Config and tops were reloaded.");
					return true;
				}
				else if(args[0].equalsIgnoreCase("pi"))
				{
					sender.sendMessage(ChatColor.RED + "Usage: /te pi <nickname>");
					return true;
				}
			if (args.length == 2)
			{
				if(args[0].equalsIgnoreCase("date"))
				{
					//deprecated - laggy and useless (and might be operator only)
					for(OfflinePlayer p : Bukkit.getOfflinePlayers() )
					{
						if( args[1].equalsIgnoreCase( p.getName() ) )
						{
							sender.sendMessage(ChatColor.GREEN + "Log-ins " + p.getName() + "'s information:");
							sender.sendMessage(ChatColor.GREEN + "First log-in: " + new Date(p.getFirstPlayed()) + " " + ((p.getFirstPlayed()/3600000+3)%24) + ":" + p.getFirstPlayed()/60000%60 + ":" + p.getFirstPlayed()/1000%60 ); //ms
							sender.sendMessage(ChatColor.GREEN + "Last log-in: " + new Date(p.getLastPlayed()) + " " + ((p.getLastPlayed()/3600000+3)%24) + ":" + p.getLastPlayed()/60000%60 + ":" + p.getLastPlayed()/1000%60 );
							return true;
						}
					}
					sender.sendMessage(ChatColor.RED + "����� �� ������.");
					return false;
				}
				if (args[0].equalsIgnoreCase("pi")) // offline only
				{
					Player pl = null;
					for(Player p : server.getOnlinePlayers())
					{
						if(args[1].equalsIgnoreCase(p.getName()))
						{
							pl = p.getPlayer();
						}
					}
					if(pl == null)
						for(OfflinePlayer p : server.getOfflinePlayers())
						{
							if(args[1].equalsIgnoreCase(p.getName()))
							{
								pl = OfflinePlayerLoader.loadPlayer(p);
							}
						}
					if(pl != null) {
						NumberFormat fo = new DecimalFormat("#0.00");     
						sender.sendMessage(ChatColor.GREEN + "Player Info about " + args[1] + ":");
						Location l = pl.getPlayer().getLocation();
						sender.sendMessage(ChatColor.GREEN + "  World: "+pl.getWorld().getName()
								+", X:" + fo.format(l.getX())
								+", Y:" + fo.format(l.getY())
								+", Z:" + fo.format(l.getZ()) );
						return true;
					}
					sender.sendMessage(ChatColor.RED + "����� ����� �� ������.");
					return false;
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("ping"))
		{
			if(args.length == 0)
				if(sender instanceof Player) 
					sender.sendMessage(ChatColor.GREEN + "Ping: "  + Main.getPing((Player)sender));
				else
					sender.sendMessage(ChatColor.RED + "Only players can know ping.");
			else {
				if (args[0].equalsIgnoreCase("list"))
				{
					for (Player p : server.getOnlinePlayers())
					{
						sender.sendMessage("    " + ChatColor.GREEN + p.getName()+"'s ping: " + Main.getPing(p));
						return true;
					}
				}
				else
				{
					for (Player p : server.getOnlinePlayers())
					{
						if (args[0].equalsIgnoreCase(p.getName()))
						{
							sender.sendMessage(ChatColor.GREEN + args[0]+"'s ping: " + String.valueOf(Main.getPing(p)));
							return true;
						}
					}
					sender.sendMessage(ChatColor.RED + "������ ������ ��� ������.");
				}
				return false;
			}
		}
		if(cmd.getName().equalsIgnoreCase("hat"))
		{
			if(sender instanceof Player) {
				Player p = (Player)sender;
				ItemStack is = p.getInventory().getHelmet();
				if (is != null && is.getEnchantmentLevel(Enchantment.BINDING_CURSE) > 0)
				{
					sender.sendMessage(HAT_CANT_UNWEAR_BINDING);
					return false;
				}
				
				ItemStack hand = p.getInventory().getItemInMainHand();
				if(hand != null && hand.getType() != Material.AIR)
				{
					if (hand.getEnchantmentLevel(Enchantment.BINDING_CURSE) > 0)
					{
						sender.sendMessage(HAT_CANT_WEAR_BINDING);
						return false;
					}
					p.getInventory().setHelmet(hand);
					p.getInventory().setItemInMainHand(is);
					sender.sendMessage(HAT_OK);
					return true;
				}
				
				hand = p.getInventory().getItemInOffHand();
				if(hand != null && hand.getType() != Material.AIR)
				{
					if (hand.getEnchantmentLevel(Enchantment.BINDING_CURSE) > 0)
					{
						sender.sendMessage(HAT_CANT_WEAR_BINDING);
						return false;
					}
					p.getInventory().setHelmet(hand);
					p.getInventory().setItemInOffHand(is);
					sender.sendMessage(HAT_OK);
					return true;
				}
				
				sender.sendMessage(ChatColor.RED + "You have not items in hands.");
				return false;
			}
			else {
				sender.sendMessage(ChatColor.RED + "Only players can use /hat.");
				return false;
			}
		}
		if(cmd.getName().equalsIgnoreCase("top"))
		{
		}
		//ellipse <x> <y> <z> <a> <b> <c> <r>

		else if(cmd.getName().equalsIgnoreCase("deldat") && sender.isOp()) {
			if(args.length == 1)
			{
				for(OfflinePlayer offp : server.getOfflinePlayers())
				{
					if(args[0].equalsIgnoreCase(offp.getName()))
					{
						if(offp.isOnline())
							offp.getPlayer().kickPlayer("Sorryank :(   but we need to delete your dat file.");
						File dat = new File(server.getWorlds().get(0).getName()+System.getProperty("file.separator")
											+"playerdata"+System.getProperty("file.separator")
											+offp.getUniqueId().toString()+".dat");
						if(dat.exists()) {
							dat.delete();
						}
					}
				}
			}
			sender.sendMessage(ChatColor.RED + "�������������: /deldat <nickname> - ������� ���� ������(� �����)");
			return false;
		}
		else if(cmd.getName().equalsIgnoreCase("fix") && sender.isOp())
		{
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
						p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(new_speed);
						sender.sendMessage("Movement speed of "+args[1]+" have been set to "+new_speed);
						return true;
					}
					sender.sendMessage(ChatColor.RED + "����� �� ������");
					sender.sendMessage(ChatColor.RED + "�������������: /fix ms <nickname> [value] - �������� ����");
					return true;
				}
						
				Material m;
				if(args[0].equalsIgnoreCase("lava")) m = Material.LAVA; 
				else if(args[0].equalsIgnoreCase("water")) m = Material.WATER;
				else
				{
					sender.sendMessage(ChatColor.RED + "�������������: /fix <lava/water> <r>");
					return false;
				}
				double r = Double.parseDouble(args[1]);
				for(int i=(int)-r;i<=r;i++){
					for(int j=(int)-r;j<=r;j++){
						for(int o=(int)-r;o<=r;o++){
							if(((Player)sender).getWorld().getBlockAt((int)Math.ceil(((Player)sender).getLocation().getX()+i), (int)Math.ceil(((Player)sender).getLocation().getY()+j), (int)Math.ceil(((Player)sender).getLocation().getZ()+o)).getType() == m)
								((Player)sender).getWorld().getBlockAt((int)Math.ceil(((Player)sender).getLocation().getX()+i), (int)Math.ceil(((Player)sender).getLocation().getY()+j), (int)Math.ceil(((Player)sender).getLocation().getZ()+o)).setType(Material.AIR);
						}
					}
				}
				return true;
			}
			sender.sendMessage(ChatColor.RED + "�������������: /fix <lava/water> <r>");
			sender.sendMessage(ChatColor.RED + "�������������: /fix ms <nickname> [value] - �������� ����");
			return false;
		}
		else if(cmd.getName().equalsIgnoreCase("look"))
		{
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "���� �������!1!");
				return false;
			}
			Player p = (Player)sender;
			if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
				for(Camera c : looking_players) 
					if(c.p == p) {
						sender.sendMessage(ChatColor.GRAY + "� ��� ����������� ������ �� "+c.l.getX()+", "+c.l.getY()+", "+c.l.getZ()+" ("+c.l.getYaw()+"; "+c.l.getPitch()+").");
						return true;
					}
				sender.sendMessage(ChatColor.GRAY + "� ��� ��� �����.");
				return true;
			}
			if(args.length == 0)
				for(Camera c : looking_players) 
					if(c.p == p) {
						looking_players.remove(c);
						sender.sendMessage(ChatColor.GREEN + "������ ������� ���������.");
						return true;
					}
			
			if(args.length == 0) {
				sender.sendMessage(ChatColor.RED + "�� �� ������ ��������� ������, ���� � ��� � ���.");
				return true;
			}
			else if(args.length != 3 && args.length != 4 && args.length != 5)
			{
				sender.sendMessage(ChatColor.GRAY + "�������������: /look - ����������");
				sender.sendMessage(ChatColor.GRAY + "               /look info - ���������� � ������� ������");
				sender.sendMessage(ChatColor.GRAY + "               /look <x> <y> <z> - ��� ������");
				sender.sendMessage(ChatColor.GRAY + "               /look <x> <y> <z> <yaw> - ��� ������ � ���������");
				sender.sendMessage(ChatColor.GRAY + "               /look <x> <y> <z> <yaw> <pitch> - ��� ��� ����������� �����");
				sender.sendMessage(ChatColor.GRAY + "        (��� ������� ������� ���������� ����� � ������ �� x � z)");
				return false;
			}
			
			double x, y, z;
			//X
			if(args[0].equals("~"))
				x = p.getLocation().getX();
			else
				try {
					x = Double.parseDouble(args[0]);
				} catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "������ � ���������� x.");
					return false;
				}
			//Y
			if(args[1].equals("~"))
				y = p.getLocation().getY();
			else
				try {
					y = Double.parseDouble(args[1]);
				} catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "������ � ���������� y.");
					return false;
				}
			//Z
			if(args[2].equals("~"))
				z = p.getLocation().getZ();
			else
				try {
					z = Double.parseDouble(args[2]);
				} catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "������ � ���������� z.");
					return false;
				}
			float yaw = 180, pitch = 90;
			//Yaw
			if(args.length > 3)
				try {
					yaw = Float.parseFloat(args[3]);
				} catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "������ � ���������� yaw.");
					return false;
				}
			//pitch
			if(args.length > 4)
				try {
					pitch = Float.parseFloat(args[4]);
				} catch(Exception ex) {
					sender.sendMessage(ChatColor.RED + "������ � ���������� pitch.");
					return false;
				}
			Location l = new Location(p.getWorld(), x, y, z, yaw, pitch);
			for(Camera c : looking_players) 
				if(c.p == p) {
					looking_players.remove(c);
					break;
				}
			looking_players.add(new Camera(p, l));
			sender.sendMessage(ChatColor.GREEN + "����������� ������ �� "+normalize(x)+", "+normalize(y)+", "+normalize(z)
					+" ("+normalize(yaw)+"; "+normalize(pitch)+").");
			return false;
		}
		return false;
	}
	
	public double normalize(double x) {
		return ((double)(int)(x*100))/100;
	}
}
