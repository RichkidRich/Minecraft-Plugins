package io.github.RichkidRich.manHunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Listeners implements Listener{
	
	public Listeners(Main main) {
		// TODO Auto-generated constructor stub
	}

	@EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player){
           //player is attacking someone
           if(event.getDamager() == Main.getHunter()) {
        	  if(event.getEntity() instanceof Player) {
        		  Player caught = (Player) event.getEntity();
        		  String caughtName = caught.getDisplayName();
        		  Bukkit.broadcastMessage(ChatColor.GOLD + caughtName + " was caught!");
        		  caught.setGameMode(GameMode.SPECTATOR);
        		  Main.setNumRunners();
        		  if(Main.getNumRunners() == 0) {
        			  Main.setHunterWon();
        		  }
        	  }
           }
        }
    }
}
