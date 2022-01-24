package com.te;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;
import com.te.top.JSONStatPlayer;
import com.te.top.TopManager;
import com.te.utils.OfflinePlayerLoader;
import com.te.utils.StatReader;

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
			return onTorchBranch(sender, args);
		}
		if (cmd.getName().equalsIgnoreCase("te") && sender.isOp())
		{
			return onTEBranch(sender, args);
		}
		if(cmd.getName().equalsIgnoreCase("ping"))
		{
			return onPingBranch(sender, args);
		}
		if(cmd.getName().equalsIgnoreCase("hat"))
		{
			return onHatBranch(sender, args);
		}
		if(cmd.getName().equalsIgnoreCase("top"))
		{
		}

		else if(cmd.getName().equalsIgnoreCase("deldat") && sender.isOp()) {
			return onDeldatBranch(sender, args);
		}
		else if(cmd.getName().equalsIgnoreCase("look"))
		{
			return onLookBranch(sender, args);
		}
		return false;
	}
	
	public boolean onTorchBranch(CommandSender sender, String[] args)
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
		return true;
	}
	
	
	// TODO REFACTORING
	public boolean onTEBranch(CommandSender sender, String[] args)
	{
		if (args.length == 1)
			if (args[0].equalsIgnoreCase("reload"))
			{
				plugin.reloadConfig();
				plugin.loadConfig();
				tops.updateTops();
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
				sender.sendMessage(ChatColor.RED + "Такой игрок не найден.");
				return false;
			}
			if (args[0].equalsIgnoreCase("temp")) {
				Player p = (Player) sender;
				JsonObject stat = StatReader.read(p.getUniqueId());
				sender.sendMessage("pig cm: " + StatReader.readLong(stat, "minecraft:custom", "minecraft:pig_one_cm"));
				sender.sendMessage("leaves: " + StatReader.readLong(stat, "minecraft:custom", "minecraft:leave_game"));
				JSONStatPlayer pl = new JSONStatPlayer(p.getName(), p.getUniqueId());
				sender.sendMessage("pig cm: " + pl.getStat(Statistic.PIG_ONE_CM, null));
			}
		}
		return false;
	}
	
	public boolean onPingBranch(CommandSender sender, String[] args)
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
				}
				return true;
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
				sender.sendMessage(ChatColor.RED + "Такого игрока нет онлайн.");
			}
			return false;
		}
		
		return false;
	}
	
	public boolean onHatBranch(CommandSender sender, String[] args)
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
	
	public boolean onDeldatBranch(CommandSender sender, String[] args)
	{
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
		sender.sendMessage(ChatColor.RED + "Использование: /deldat <nickname> - удалить файл игрока(с киком)");
		return false;
	}
	
	public boolean onLookBranch(CommandSender sender, String[] args)
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Буть играком!1!");
			return false;
		}
		Player p = (Player)sender;
		if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
			for(Camera c : looking_players) 
				if(c.p == p) {
					sender.sendMessage(ChatColor.GRAY + "У вас установлена камера на "+c.l.getX()+", "+c.l.getY()+", "+c.l.getZ()+" ("+c.l.getYaw()+"; "+c.l.getPitch()+").");
					return true;
				}
			sender.sendMessage(ChatColor.GRAY + "У вас нет камер.");
			return true;
		}
		if(args.length == 0)
			for(Camera c : looking_players) 
				if(c.p == p) {
					looking_players.remove(c);
					sender.sendMessage(ChatColor.GREEN + "Камера успешно отключена.");
					return true;
				}
		
		if(args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Вы не можете выключить камеру, если у вас её нет.");
			return true;
		}
		else if(args.length != 3 && args.length != 4 && args.length != 5)
		{
			sender.sendMessage(ChatColor.GRAY + "Использование: /look - выключение");
			sender.sendMessage(ChatColor.GRAY + "               /look info - информация о текущей камере");
			sender.sendMessage(ChatColor.GRAY + "               /look <x> <y> <z> - вид сверху");
			sender.sendMessage(ChatColor.GRAY + "               /look <x> <y> <z> <yaw> - вид сверху с поворотом");
			sender.sendMessage(ChatColor.GRAY + "               /look <x> <y> <z> <yaw> <pitch> - вид под определённым углом");
			sender.sendMessage(ChatColor.GRAY + "        (Все команды требуют находиться рядом с точкой по x и z)");
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
				sender.sendMessage(ChatColor.RED + "Ошибка с аргументом x.");
				return false;
			}
		//Y
		if(args[1].equals("~"))
			y = p.getLocation().getY();
		else
			try {
				y = Double.parseDouble(args[1]);
			} catch(Exception ex) {
				sender.sendMessage(ChatColor.RED + "Ошибка с аргументом y.");
				return false;
			}
		//Z
		if(args[2].equals("~"))
			z = p.getLocation().getZ();
		else
			try {
				z = Double.parseDouble(args[2]);
			} catch(Exception ex) {
				sender.sendMessage(ChatColor.RED + "Ошибка с аргументом z.");
				return false;
			}
		float yaw = 180, pitch = 90;
		//Yaw
		if(args.length > 3)
			try {
				yaw = Float.parseFloat(args[3]);
			} catch(Exception ex) {
				sender.sendMessage(ChatColor.RED + "Ошибка с аргументом yaw.");
				return false;
			}
		//pitch
		if(args.length > 4)
			try {
				pitch = Float.parseFloat(args[4]);
			} catch(Exception ex) {
				sender.sendMessage(ChatColor.RED + "Ошибка с аргументом pitch.");
				return false;
			}
		Location l = new Location(p.getWorld(), x, y, z, yaw, pitch);
		for(Camera c : looking_players) 
			if(c.p == p) {
				looking_players.remove(c);
				break;
			}
		looking_players.add(new Camera(p, l));
		sender.sendMessage(ChatColor.GREEN + "Установлена камера на "+normalize(x)+", "+normalize(y)+", "+normalize(z)
				+" ("+normalize(yaw)+"; "+normalize(pitch)+").");
		return false;
	}
	
	public double normalize(double x) {
		return ((double)(int)(x*100))/100;
	}
}
