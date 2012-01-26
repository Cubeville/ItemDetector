package org.cubeville.itemdetector.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cubeville.itemdetector.ItemDetector;

public class DetectorListener implements Listener {
	
	private final ItemDetector plugin;
	
	public DetectorListener(ItemDetector plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPressurePlate(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL) {
			return;
		}
		
		Block block = event.getClickedBlock();
		
		if (block.getType() != Material.STONE_PLATE && block.getType() != Material.WOOD_PLATE) {
			return;
		}
		
		if (!plugin.isDetector(block)) {
			return;
		}
	}

}
