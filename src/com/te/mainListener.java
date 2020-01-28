package com.te;

import java.io.File;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
//import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_15_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.te.OfflinePlayerLoader;
import com.te.top.TopManager;



public class mainListener extends JavaPlugin implements Listener
{
	String HAT_CANT_WEAR_BINDING = ChatColor.RED + "You can't wear cursed item.";
	String HAT_CANT_UNWEAR_BINDING = ChatColor.RED + "You can't unwear binded helmet.";
	String HAT_OK = ChatColor.GREEN + "Successfully have put a hat on the head.";
	
	int ping_update_ticks = 20, ping_ticks = ping_update_ticks;
	int sleep_update_ticks = 10, sleep_ticks = sleep_update_ticks;
	private Scoreboard sb;
	List<Camera> looking_players = new ArrayList<>();
	
	TorchPlacer torches = new TorchPlacer(this);
	TopManager tops;
	
	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(torches, this);


		tops = new TopManager(this);
		getCommand("top").setExecutor(tops);
		getCommand("top").setTabCompleter(tops.getCompleter());
		
		sb = Bukkit.getScoreboardManager().getMainScoreboard();
		getServer().getPluginManager().registerEvents(this, this);
		
		loadConfig();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
	            new Runnable() {
			public void run() {
				tops.tick();
				
				ping_ticks--;
				if(ping_ticks <= 0) {
					ping_ticks = ping_update_ticks;
					changePing();
				}
				
				sleep_ticks--;
				if(sleep_ticks <= 0) {
					sleep_ticks = sleep_update_ticks;
					showSleeping();
				}
				
				// For /look
				try {
					for (World w : getServer().getWorlds())
						for (Entity en : w.getEntitiesByClass(ArmorStand.class))
							if (en.getCustomName() != null && en.getCustomName().equals("fakestand"))
								en.remove();
					for (int i = looking_players.size() - 1; i >= 0; i--) {
						Camera c = looking_players.get(i);
						if (c.p.isOnline()) {
							/*Entity fakestand = c.p.getWorld().spawn(c.l, ArmorStand.class);
							fakestand.setGravity(false);
							fakestand.setInvulnerable(true);
							fakestand.setCustomName("fakestand");
							((ArmorStand)fakestand).setVisible(false);
							
							PacketPlayOutEntityTeleport camera = new PacketPlayOutEntityTeleport( ((CraftEntity)fakestand).getHandle() );
							((EntityPlayer)((CraftPlayer)c.p).getHandle()).playerConnection.sendPacket(camera);*/
							 
							/*EntityArmorStand fakestand = new EntityArmorStand( ( (CraftWorld) c.p.getWorld() ).getHandle() );
							fakestand.setInvisible(true);
							fakestand.setNoGravity(true);
							fakestand.setInvulnerable(true);
							PlayerConnection pc = ((EntityPlayer)((CraftPlayer)c.p).getHandle()).playerConnection;
							
							PacketPlayOutSpawnEntityLiving ent = new PacketPlayOutSpawnEntityLiving(fakestand);
				            pc.sendPacket(ent);
				           
				            PacketPlayOutEntityTeleport camera = new PacketPlayOutEntityTeleport(fakestand);
							pc.sendPacket(camera);*/
							
							/*Entity fakeentity = c.p.getWorld().spawn(c.l, ArmorStand.class);//(c.l, EntityType.ZOMBIE);
							PacketPlayOutEntityTeleport camera = new PacketPlayOutEntityTeleport( ((CraftEntity)fakeentity).getHandle() );
							((EntityPlayer)((CraftPlayer)c.p).getHandle()).playerConnection.sendPacket(camera);
							fakeentity.remove();*/
							
						} else {
							looking_players.remove(i);
						}
					}
				} catch(Exception ex) {
					//ex.printStackTrace();
					if (getServer().getWorlds().get(0).getTime() == 0)
						System.out.println("ThirdEye: look error");
				}
			}
		},
	            0L,1L);
	}
	
	public void onDisable()
	{
		
	}
	
	public void loadConfig()
	{
		getConfig().addDefault("topSize", 5);
		getConfig().options().copyDefaults(true);
		saveConfig();
		int top_size = getConfig().getInt("topSize", 5);
		if (top_size <= 0) {
			top_size = 5;
		}
		tops.setSize(top_size);
		
		System.out.println("[ThirdEye] Config Reloaded.");
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
					reloadConfig();
					loadConfig();
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
					sender.sendMessage(ChatColor.RED + "Игрок не найден.");
					return false;
				}
				if (args[0].equalsIgnoreCase("pi")) // offline only
				{
					Player pl = null;
					for(Player p : getServer().getOnlinePlayers())
					{
						if(args[1].equalsIgnoreCase(p.getName()))
						{
							pl = p.getPlayer();
						}
					}
					if(pl == null)
						for(OfflinePlayer p : getServer().getOfflinePlayers())
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
			}
		}
		if(cmd.getName().equalsIgnoreCase("ping"))
		{
			if(args.length == 0)
				if(sender instanceof Player) 
					sender.sendMessage(ChatColor.GREEN + "Ping: " +getPing((Player)sender));
				else
					sender.sendMessage(ChatColor.RED + "Only players can know ping.");
			else {
				if (args[0].equalsIgnoreCase("list"))
				{
					for (Player p : getServer().getOnlinePlayers())
					{
						sender.sendMessage("    " + ChatColor.GREEN + p.getName()+"'s ping: " + getPing(p));
						return true;
					}
				}
				else
				{
					for (Player p : getServer().getOnlinePlayers())
					{
						if (args[0].equalsIgnoreCase(p.getName()))
						{
							sender.sendMessage(ChatColor.GREEN + args[0]+"'s ping: " + String.valueOf(getPing(p)));
							return true;
						}
					}
					sender.sendMessage(ChatColor.RED + "Такого игрока нет онлайн.");
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
				for(OfflinePlayer offp : getServer().getOfflinePlayers())
				{
					if(args[0].equalsIgnoreCase(offp.getName()))
					{
						if(offp.isOnline())
							offp.getPlayer().kickPlayer("Sorryank :(   but we need to delete your dat file.");
						File dat = new File(getServer().getWorlds().get(0).getName()+System.getProperty("file.separator")
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
		else if(cmd.getName().equalsIgnoreCase("fix") && sender.isOp())
		{
			if(args.length > 1)
			{
				if(args[0].equalsIgnoreCase("ms") || args[0].equalsIgnoreCase("movespeed") || args[0].equalsIgnoreCase("movementspeed")) {
					Player p = null;
					for(Player pl : getServer().getOnlinePlayers())
					{
						if(args[1].equalsIgnoreCase(pl.getName()))
						{
							p = pl.getPlayer();
							break;
						}
					}
					if(p == null)
						for(OfflinePlayer offp : getServer().getOfflinePlayers())
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
					sender.sendMessage(ChatColor.RED + "Игрок не найден");
					sender.sendMessage(ChatColor.RED + "Использование: /fix ms <nickname> [value] - скорость бега");
					return true;
				}
						
				Material m;
				if(args[0].equalsIgnoreCase("lava")) m = Material.LAVA; 
				else if(args[0].equalsIgnoreCase("water")) m = Material.WATER;
				else
				{
					sender.sendMessage(ChatColor.RED + "Использование: /fix <lava/water> <r>");
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
			sender.sendMessage(ChatColor.RED + "Использование: /fix <lava/water> <r>");
			sender.sendMessage(ChatColor.RED + "Использование: /fix ms <nickname> [value] - скорость бега");
			return false;
		}
		else if(cmd.getName().equalsIgnoreCase("look"))
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
		return false;
	}
	
	public void changePing()
	{
		if(	sb.getObjective("current_ping") == null ) 
			sb.registerNewObjective("current_ping", "dummy", "Ping");
		Objective tab = sb.getObjective("current_ping");
		//TO DO: do not clear slot if it's not free
		tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		for(Player p : getServer().getOnlinePlayers())
			tab.getScore(p.getDisplayName()).setScore(getPing(p));
	}
	
	public void showSleeping()
	{
		for (Player p : getServer().getOnlinePlayers())
		{
			Biome biome = p.getLocation().getBlock().getBiome();
			if (p.isSleeping()) {
				p.setPlayerListName(ChatColor.AQUA+"[S]"+ChatColor.WHITE+p.getName());
			} else if (biome == Biome.NETHER
					|| biome == Biome.THE_END || biome == Biome.SMALL_END_ISLANDS || biome == Biome.END_BARRENS || biome == Biome.END_HIGHLANDS || biome == Biome.END_MIDLANDS) {
				p.setPlayerListName(ChatColor.RED+"[S]"+ChatColor.WHITE+p.getName());
			} else {
				p.setPlayerListName(p.getName());
			}
		}
	}
	
	public int getPing(Player p) {
		return ((EntityPlayer)((CraftPlayer)p).getHandle()).ping;
	}
	
	public double normalize(double x) {
		return ((double)(int)(x*100))/100;
	}
}