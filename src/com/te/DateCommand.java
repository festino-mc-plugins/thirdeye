package com.te;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class DateCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Введите ник игрока.");
			return false;
		}
		
		String name = args[0];
		OfflinePlayer p = Bukkit.getOfflinePlayer(name);
		if (p == null) {
			sender.sendMessage(ChatColor.RED + "NPE! Игрок \"" + name + "\" не найден.");
			return false;
		}
		if (!p.hasPlayedBefore()) {
			sender.sendMessage(ChatColor.RED + "Игрок \"" + name + "\" не играл.");
			return false;
		}
		sender.sendMessage(ChatColor.GREEN + "Информация о " + p.getName() + ":");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String firstDateStr = dateFormat.format(new Date(p.getFirstPlayed()));
		String lastDateStr = dateFormat.format(new Date(p.getLastPlayed()));
		sender.sendMessage(ChatColor.GREEN + "    Первый заход:     " + firstDateStr);
		if (p.equals(sender)) {
			sender.sendMessage(ChatColor.GREEN + "    Последний заход: это ты, дурашка" );
		} else if (p.isOnline()) {
			sender.sendMessage(ChatColor.GREEN + "    Последний заход: этот игрок сейчас на сервере" );
		} else {
			sender.sendMessage(ChatColor.GREEN + "    Последний заход: " + lastDateStr);
		}
		return true;
	}
}
