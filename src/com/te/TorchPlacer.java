package com.te;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import net.md_5.bungee.api.ChatColor;

public class TorchPlacer implements Listener {
	private List<Player> lighter_players = new ArrayList<>();
	static final byte MIN_LIGHT = 10;
	public static final String MSG_EMPTY = ChatColor.RED + "You have no torches.";
	public static final String MSG_START = ChatColor.GREEN + "Torch placing is started.";
	public static final String MSG_END   = ChatColor.GREEN + "Torch placing is ended.";
	public static final String MSG_OUT_OF= ChatColor.RED + "Out of torches.";
	public static final String MSG_HAND  = ChatColor.RED + "Take torches in hand.";

	private static final Integer COOLDOWN_TICKS = 2;
	public static final String KEY_COOLDOWN = "cd_torchplacer";
	Main plugin;
	
	public TorchPlacer(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		removePlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player p = event.getPlayer();
		if (lighter_players.contains(p))
		{
			Block b = event.getTo().getBlock();
			if (b.isEmpty() && b.getLightFromBlocks() < MIN_LIGHT
					&& canPlaceOn(b.getRelative(BlockFace.DOWN)))
			{
				placeTorch(p, b);
			}
		}
	}
	
	public boolean addPlayer(Player p)
	{
		boolean already_torched = lighter_players.contains(p);
		if (!already_torched)
			lighter_players.add(p);
		return !already_torched;
	}
	
	public boolean removePlayer(Player p)
	{
		boolean already_torched = lighter_players.contains(p);
		if (already_torched)
			lighter_players.remove(p);
		return already_torched;
	}
	
	private void setCooldown(Player p)
	{
		p.setMetadata(KEY_COOLDOWN, new FixedMetadataValue(plugin, new Integer(p.getTicksLived())));
	}
	private boolean isOnCooldown(Player p)
	{
		if (p.hasMetadata(KEY_COOLDOWN))
		{
			for (MetadataValue meta : p.getMetadata(KEY_COOLDOWN))
				if (meta.asInt() + COOLDOWN_TICKS > p.getTicksLived())
					return true;
		}
		return false;
	}
	
	/** Player is in list */
	private void placeTorch(Player p, Block b)
	{
		if (isOnCooldown(p))
			return;
		
		int amount = 0;
		PlayerInventory pinv = p.getInventory();
		ItemStack main_hand = pinv.getItemInMainHand();
		ItemStack off_hand = pinv.getItemInOffHand();
		if (main_hand != null && main_hand.getType() == Material.TORCH)
		{
			amount = main_hand.getAmount() - 1;
			main_hand.setAmount(amount);
			pinv.setItemInMainHand(main_hand);
		}
		else if (off_hand != null && off_hand.getType() == Material.TORCH)
		{
			amount = off_hand.getAmount() - 1;
			off_hand.setAmount(amount);
			pinv.setItemInOffHand(off_hand);
		}
		else
		{
			int slot = -1;
			ItemStack[] inv = pinv.getStorageContents();
			for (int i = 0; i < inv.length; i++)
				if (inv[i] != null && inv[i].getType() == Material.TORCH)
				{
					slot = i;
					break;
				}
			
			if (slot < 0)
			{
				p.sendMessage(MSG_OUT_OF);
				removePlayer(p);
				return;
			}
			
			//amount = inv[slot].getAmount() - 1;
			//inv[slot].setAmount(amount);
			//pinv.setStorageContents(inv);

			p.sendTitle("", MSG_HAND, 0, 30, 0);
			return;
		}

		if (amount == 0)
		{
			int slot = -1;
			ItemStack[] inv = pinv.getStorageContents();
			for (int i = 0; i < inv.length; i++)
				if (inv[i] != null && inv[i].getType() == Material.TORCH)
				{
					slot = i;
					break;
				}
			
			if (slot < 0)
			{
				p.sendMessage(MSG_OUT_OF);
				removePlayer(p);
			}
			else
				p.sendTitle("", MSG_HAND, 0, 30, 0);
		}
		
		setCooldown(p);
		b.setType(Material.TORCH);
	}
	
	public boolean canPlaceOn(Block b) 
	{
		if (b == null)
			return false;
		
		Material m = b.getType();
		// NOT: cactus, leaves, farmland, enchanting table, end portal frame, grass path, beds, stonecutter, doors, lecterns, daylight sensors, composter, brewing stations, cauldrons
		if (isNegativeException(m)) {
			return false;
		}
		// BUT: glowstone, anvil, end rod, scaffolding
		if (isPositiveException(m)) {
			return true;
		}
		
		if (b.isPassable() // sign, trapdoors, doors...
				|| !m.isSolid() // ladder?
				|| m == Material.CHEST || m == Material.TRAPPED_CHEST)
			return false;
		
		// bottom slabs
		BlockData data = b.getBlockData();
		if (data instanceof Slab) {
			if (((Slab) data).getType() == Type.BOTTOM) {
				return false;
			}
		}
		//bottom stairs and etc
		if (data instanceof Bisected) {
			if (((Bisected) data).getHalf() == Half.BOTTOM) {
				return false;
			}
		}
		
		return true;
	}
	
	boolean isNegativeException(Material m) {
		if (isBed(m) || isDoor(m) || isLeaves(m)) {
			return true;
		}
		
		switch (m) {
		case CACTUS:
		case FARMLAND:
		case ENCHANTING_TABLE:
		case END_PORTAL_FRAME:
		case DIRT_PATH:
		case STONECUTTER:
		case LECTERN:
		case DAYLIGHT_DETECTOR:
		case COMPOSTER:
		case BREWING_STAND:
		case CAULDRON:
			return true;
		default:
			return false;
		}
	}
	
	boolean isPositiveException(Material m) {
		switch (m) {
		case GLOWSTONE:
		case ANVIL:
		case CHIPPED_ANVIL:
		case DAMAGED_ANVIL:
		case END_ROD:
		case SCAFFOLDING:
			return true;
		default:
			return false;
		}
	}
	
	
	public boolean isBed(Material m) {
		switch (m) {
		case BLACK_BED:
		case BLUE_BED:
		case BROWN_BED:
		case CYAN_BED:
		case GRAY_BED:
		case GREEN_BED:
		case LIGHT_BLUE_BED:
		case LIGHT_GRAY_BED:
		case LIME_BED:
		case MAGENTA_BED:
		case ORANGE_BED:
		case PINK_BED:
		case PURPLE_BED:
		case RED_BED:
		case WHITE_BED:
		case YELLOW_BED:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isLeaves(Material m) {
		switch (m) {
		case ACACIA_LEAVES:
		case BIRCH_LEAVES:
		case DARK_OAK_LEAVES:
		case JUNGLE_LEAVES:
		case OAK_LEAVES:
		case SPRUCE_LEAVES:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isWoodenDoor(Material m) {
		switch (m) {
		case ACACIA_DOOR:
		case BIRCH_DOOR:
		case DARK_OAK_DOOR:
		case JUNGLE_DOOR:
		case OAK_DOOR:
		case SPRUCE_DOOR:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isDoor(Material m) {
		return m == Material.IRON_DOOR || isWoodenDoor(m);
	}
}
