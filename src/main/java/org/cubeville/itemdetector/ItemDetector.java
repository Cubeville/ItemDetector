package org.cubeville.itemdetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.itemdetector.command.DetectorCommand;
import org.cubeville.itemdetector.listener.DetectorListener;

/**
 * Bukkit plugin to create item detecting pressure plates
 * 
 * @author GoalieGuy6 <goalieguy6@cubeville.org>
 */
public class ItemDetector extends JavaPlugin {
	
	public String name;
	public String version;
	
	public static final Logger log = Logger.getLogger("Minecraft");
	
	private DetectorListener listener;
	private DetectorCommand executor;
	
	private Map<Block, Detector> detectors = new HashMap<Block, Detector>();
	private Map<Player, String> actions = new HashMap<Player, String>();
	
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
		executor = new DetectorCommand(this);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(listener, this);
		
		setupCommands();
		
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
	
	private void setupCommands() {
		getCommand("itemdetector").setExecutor(executor);
	}
	
	private void setupDatabase() {
		try {
			getDatabase().find(Detector.class).findRowCount();
		} catch (PersistenceException ex) {
			log.info("[" + name + "] Setting up database.");
			installDDL();
		}
	}
	
	public void setAction(final Player player, final String action) {
		if (action.isEmpty() && actions.containsKey(player)) {
			actions.remove(player);
			return;
		}
		
		actions.put(player, action);
		
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				if (actions.containsKey(player) && actions.get(player).equals(action)) {
					actions.remove(player);
				}
			}
		}, 300L);
	}
	
	public String getAction(Player player) {
		return actions.containsKey(player) ? actions.get(player) : "";
	}
	
	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Detector.class);
		return list;
	}
	
	public void addDetector(Player owner, Block block) {
		Detector detector = new Detector();
		detector.setOwner(owner.getName());
		detector.setWorld(block.getWorld().getName());
		detector.setX(block.getX());
		detector.setY(block.getY());
		detector.setZ(block.getZ());
		detectors.put(block, detector);
		getDatabase().save(detector);
	}
	
	public void removeDetector(Block block) {
		if (detectors.containsKey(block)) {
			getDatabase().delete(detectors.get(block));
			detectors.remove(block);
		}
	}
	
	public boolean isDetector(Block block) {
		return detectors.containsKey(block);
	}
	
	public Detector getDetector(Block block) {
		return detectors.containsKey(block) ? detectors.get(block) : null;
	}

}
