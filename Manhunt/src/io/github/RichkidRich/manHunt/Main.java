package io.github.RichkidRich.manHunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
	
	static public Player hunterPlayer;
	public int gameLength;
	public int initTime;
	public int initTaskID;
	
	static public int numRunners = 0;
	static public boolean hunter_won = false;
	
	public int mainTime;
	public int mainTaskID;
	
	public int gameStartTime;
	public int gameNumTicks;
	
	public boolean gameStarted = false;
	public boolean startTimeSaved = false;
	
	public String BEGIN_HUNT = "The Hunt has Begun!";
	public String HUNTER_WIN = "Yare yare daze~ Everyone was found, The Hunter wins!";
	public String RUNNER_WIN = "Ara Ara~ The Hunter failed and let their country down, The Runners win!";
	
	static public Player getHunter() {
		return hunterPlayer;
	}
	
	static public int getNumRunners() {
		return numRunners;
	}
	
	static public void setNumRunners() {
		numRunners = numRunners - 1;
		return;
	}
	
	static public void setHunterWon() {
		hunter_won = true;
		return;
	}
	
	@Override
    public void onEnable() {
        // TODO Insert logic to be performed when the plugin is enabled
		Listener listener = new Listeners(this);
        getServer().getPluginManager().registerEvents(listener, this);
    }
    
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }
    
    public void setTimer(int amount) {
    	initTime = amount;
    }
    
    public void stopTimer() {
    	Bukkit.getScheduler().cancelTask(initTaskID);
    	Bukkit.broadcastMessage(ChatColor.WHITE + "Game and Timer Stopped...");
    	for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.setGameMode(GameMode.CREATIVE);
			p.getInventory().clear();
		}
    	gameStarted = false;
    	startTimeSaved = false;
    	Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Game State Reset, ready for re-run");
    	
    }
    
    public void startTimer() {
    	
    	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    	initTaskID = scheduler.scheduleSyncRepeatingTask(this,  new Runnable() {
    		@Override
    		public void run() {
    			if(!startTimeSaved) {
    				gameStartTime = initTime - 60;
    				startTimeSaved = true;
    			}
    			if(initTime == 0) {
    	    		Bukkit.broadcastMessage(ChatColor.GOLD + RUNNER_WIN);
    	    		stopTimer();
    	    		return;
    	    	}else if(initTime == gameStartTime && !gameStarted) {
    	    		Bukkit.broadcastMessage(ChatColor.GREEN + BEGIN_HUNT);
    	    		gameStarted = true;
    	    	}
    			
    			if(gameStarted) {
    				if(hunter_won) {
    					Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + HUNTER_WIN);
        	    		stopTimer();
        	    		return;
    				}
    				if(initTime % 60 == 0) {
    					Bukkit.broadcastMessage(ChatColor.RED + "Time remaining: " + initTime + " seconds");
    				}
    				if(initTime % 45 == 0) {
    					for(Player p : Bukkit.getOnlinePlayers()) {
    						p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 1));
            	    	}
    				}
    				for(Player p : Bukkit.getOnlinePlayers()) {
        	    		p.setLevel(initTime);
        	    	}
    			} else {
    				if(initTime % 10 == 0) {
    					Bukkit.broadcastMessage(ChatColor.RED + "Game Time remaining: " + (initTime-gameStartTime) + " seconds");
    				}
    				for(Player p : Bukkit.getOnlinePlayers()) {
        	    		p.setLevel((initTime-gameStartTime));
        	    	}
    			}
    	    	
    			initTime = initTime - 1;
    		}
    	}, 0L, 20L);
    	
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player){
           //player is attacking someone
           if(event.getDamager() == hunterPlayer) {
        	  if(event.getEntity() instanceof Player) {
        		  Player caught = (Player) event.getEntity();
        		  String caughtName = caught.getDisplayName();
        		  Bukkit.broadcastMessage(ChatColor.GOLD + caughtName + " was caught!");
        		  caught.setGameMode(GameMode.SPECTATOR);
        		  numRunners = numRunners -1;
        		  if(numRunners == 0) {
        			  hunter_won = true;
        		  }
        	  }
           }
        }
    }
    
    // /startManHunt <hunter> <time in secs> <blindness>
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	if(cmd.getName().equalsIgnoreCase("startManHunt")) {
    		if (sender instanceof Player) {
    			if(args.length != 3) {
    	    		return false;
    	    	}
    			if(Integer.parseInt(args[1]) < 60) {
    				Bukkit.broadcastMessage(ChatColor.DARK_RED + "Min time for game must be greater than or equal to 60 seconds");
    				return false;
    			}
    			gameLength = Integer.parseInt(args[1]);
    			gameNumTicks = (gameLength+60)*20;
    			int lengthMinutes = gameLength/60;
    			hunterPlayer = Bukkit.getPlayer(args[0]);
 
    			Location zig = new Location(Bukkit.getWorld("The Great Pudding Land"), -54, 90, -109);
    			
    			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
    				p.setGameMode(GameMode.ADVENTURE);
    				p.getInventory().clear();
    				if(p == hunterPlayer) {
    					Bukkit.broadcastMessage(ChatColor.AQUA + "Hunter is " + hunterPlayer.getDisplayName());
    					p.teleport(zig);
    					p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 1));
    					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, gameNumTicks, 2));
    					if(Boolean.parseBoolean(args[2])) {
    						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 1));
    					}
    					p.getInventory().addItem(new ItemStack(Material.CROSSBOW, 1));
    					p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 30));
    					p.getInventory().addItem(new ItemStack(Material.SPECTRAL_ARROW, 21));
    				} else {
    					numRunners = numRunners + 1;
    					p.teleport(zig);
    					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 1));
    					Bukkit.broadcastMessage(ChatColor.RED + "Runners have 1 minute to hide");
    					if(Boolean.parseBoolean(args[2])) {
    						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, gameNumTicks, 1));
    					}
    					p.getInventory().addItem(new ItemStack(Material.BREAD, 64));
    					p.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
    				}
    			    
    			}
    			
    			setTimer(gameLength + 60);  //set time for runners to hide
    			startTimer();
    			return true;
    		} else {
    			//console
    			Bukkit.broadcastMessage(ChatColor.YELLOW + "Moshi Moshi, you are not a player");
    			return true;
    		}
    	}
    	
    	
    	return false;
    }
}
