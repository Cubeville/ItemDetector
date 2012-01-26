package org.cubeville.itemdetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.itemdetector.listener.DetectorListener;

/**
 * Bukkit plugin to create item detecting pressure plates
 * 
 * @author GoalieGuy6 <goalieguy6@cubeville.org>
 */
public class ItemDetector extends JavaPlugin {
	
	public String name;
	public String version;
	
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private DetectorListener listener;
	
	private Map<Block, Detector> detectors = new HashMap<Block, Detector>();
	
	public void onDisable() {
		saveDetectors();
		
		log.info("[" + name + "] Version " + version + " disabled.");
	}

	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		name = pdfFile.getName();
		version = pdfFile.getVersion();
		
		setupDatabase();
		loadDetectors();
		
		listener = new DetectorListener(this);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(listener, this);
		
		log.info("[" + name + "] Version " + version + " enabled.");
	}
	
	private void loadDetectors() {
		List<Detector> list = getDatabase().find(Detector.class).findList();
		
		for (Detector d : list) {
			Block block = getServer().getWorld(d.getWorld()).getBlockAt(d.getX(), d.getY(), d.getZ());
			
			if (block.getType() != Material.STONE_PLATE && block.getType() != Material.WOOD_PLATE) {
				getDatabase().delete(d);
			} else {
				detectors.put(block, d);
			}
		}
	}
	
	private void saveDetectors() {
		for (Detector d : detectors.values()) {
			getDatabase().save(d);
		}
	}
	
	private void setupDatabase() {
		try {
			getDatabase().find(Detector.class).findRowCount();
		} catch (PersistenceException ex) {
			log.info("[" + name + "] Setting up database.");
			installDDL();
		}
	}
	
	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Detector.class);
		return list;
	}
	
	public boolean isDetector(Block block) {
		return detectors.containsKey(block);
	}

}
