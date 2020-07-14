package com.te.top;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TopCommand implements CommandExecutor {
	
	private final TopManager manager;
	
	public TopCommand(TopManager manager) {
		this.manager = manager;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length == 0 || args.length > 0 && args[0].startsWith("?"))
		{
			printTops(sender);
		}
		else if (args.length == 1)
		{
			for (RatingTop rt : manager.getTops())
				if (args[0].equalsIgnoreCase(rt.main_activator)) {
					rt.output_to_player(sender, manager.topSize);
					return true;
				}
			sender.sendMessage("“опа с таким определителем не существует.");
			printTops(sender);
			return true;
		}
		else if (args.length == 2)
		{
			for (RatingTop rt : manager.getTops()) {
				if (rt.add_activator != null) {
					if (args[0].equalsIgnoreCase(rt.main_activator) && args[1].equalsIgnoreCase(rt.add_activator)) {
						rt.output_to_player(sender, manager.topSize);
						return true;
					}
				}
			}
			sender.sendMessage("“опа с таким определителем не существует.");
			printTops(sender);
			return true;
		}
		return true;
	}
	
	public void printTops(CommandSender sender) {
		sender.sendMessage(ChatColor.GRAY+"ƒоступные топы:");
		String list = "";
		boolean first = true;
		for (RatingTop rt : manager.getTops()) {
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
}
