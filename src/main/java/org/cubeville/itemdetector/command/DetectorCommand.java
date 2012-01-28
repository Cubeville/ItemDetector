package org.cubeville.itemdetector.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cubeville.itemdetector.ItemDetector;

public class DetectorCommand implements CommandExecutor {
	
	private final ItemDetector plugin;
	
	public DetectorCommand(ItemDetector plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			ItemDetector.log.info("That command cannot be used from the console.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("ItemDetector.create")) {
			return true;
		}
		
		if (args.length != 1) {
			player.sendMessage(ChatColor.RED + "Invalid syntax. /" + label + " <create/remove>");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("create")) {
			plugin.setAction(player, "create");
			player.sendMessage(ChatColor.GREEN + "Punch a pressure plate to create a detector!");
		} else if (args[0].equalsIgnoreCase("remove")) {
			plugin.setAction(player, "remove");
			player.sendMessage(ChatColor.GREEN + "Punch a pressure plate to remove its detector.");
		} else {
			player.sendMessage(ChatColor.RED + "Invalid syntax. /" + label + " <create/remove>");
		}
				
		return true;
	}

}
