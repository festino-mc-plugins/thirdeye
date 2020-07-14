package com.te;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R1.EntityPlayer;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.te.top.TopManager;

public class Main extends JavaPlugin {
	
	private static String PLUGIN_NAME = "ThirdEye";
	private static Logger STATICLOGGER;
	int ping_update_ticks = 20, ping_ticks = ping_update_ticks;
	int sleep_update_ticks = 10, sleep_ticks = sleep_update_ticks;
	private Scoreboard sb;
	
	TorchPlacer torches = new TorchPlacer(this);
	TopManager tops;

	public Main() {
		PLUGIN_NAME = getName();
		STATICLOGGER = getLogger();
	}
	
	public static String getStaticName() {
		return PLUGIN_NAME;
	}
	
	public static Logger getStaticLogger() {
		return STATICLOGGER;
	}
	
	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(torches, this);

		tops = new TopManager();
		getCommand("top").setExecutor(tops.getExecutor());
		getCommand("top").setTabCompleter(tops.getTabCompleter());
		pm.registerEvents(tops.getUpdater(), this);
		
		sb = Bukkit.getScoreboardManager().getMainScoreboard();
		
		CommandManager cmdManager = new CommandManager(this, torches, tops);
		getCommand("te").setExecutor(cmdManager);
		getCommand("torch").setExecutor(cmdManager);
		getCommand("ping").setExecutor(cmdManager);
		getCommand("hat").setExecutor(cmdManager);
		getCommand("deldat").setExecutor(cmdManager);
		getCommand("look").setExecutor(cmdManager);
		FixCommand fixCommand = new FixCommand(getServer());
		getCommand("fix").setExecutor(fixCommand);
		DateCommand dateCommand = new DateCommand();
		getCommand("date").setExecutor(dateCommand);
		
		loadConfig();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
	            new Runnable() {
			public void run()
			{
				tops.tick();
				tops.getUpdater().tick();
				
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
					for (int i = cmdManager.looking_players.size() - 1; i >= 0; i--) {
						Camera c = cmdManager.looking_players.get(i);
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
							cmdManager.looking_players.remove(i);
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
			} else if (biome == Biome.NETHER_WASTES || biome == Biome.SOUL_SAND_VALLEY || biome == Biome.CRIMSON_FOREST || biome == Biome.WARPED_FOREST || biome == Biome.BASALT_DELTAS
					|| biome == Biome.THE_END || biome == Biome.SMALL_END_ISLANDS || biome == Biome.END_BARRENS || biome == Biome.END_HIGHLANDS || biome == Biome.END_MIDLANDS) {
				p.setPlayerListName(ChatColor.RED+"[S]"+ChatColor.WHITE+p.getName());
			} else {
				p.setPlayerListName(p.getName());
			}
		}
	}
	
	public static int getPing(Player p) {
		return ((EntityPlayer)((CraftPlayer)p).getHandle()).ping;
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
}