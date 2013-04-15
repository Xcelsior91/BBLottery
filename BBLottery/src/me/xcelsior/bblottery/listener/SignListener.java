package me.xcelsior.bblottery.listener;

import me.xcelsior.bblottery.BBLottery;
import me.xcelsior.bblottery.Perms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener{
	
	BBLottery plugin;

	public SignListener(BBLottery plugin) {
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e){
		if(e.getLine(0).equalsIgnoreCase("[Lottery]")){
			if(Perms.hasPerm(e.getPlayer(), Perms.SIGN)){
				if(e.getLine(1).equalsIgnoreCase("Jackpot")){
					
				}else if(e.getLine(1).matches("[W,w]inner(\\s\\d)?")){
					
				}
			}
		}
	}

}
