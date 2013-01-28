/*
 * BBLottery - by Xcelsior
 * http://
 *
 * powered by Kickstarter
 */

package me.xcelsior.bblottery;


import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.xcelsior.bblottery.commands.CommandExecutor_Lottery;


public class BBLottery extends JavaPlugin{
	private Logger log;
	private PluginDescriptionFile description;
	public static Permission permission = null;
    public static Economy economy = null;
    
    public LotterySave loSa;
    public LotteryManager manager;

	private String prefix;

	
	@Override
	public void onEnable(){
		log = Logger.getLogger("Minecraft");
		description = getDescription();
		prefix = "["+description.getName()+"] ";

		log("loading "+description.getFullName());
		
		setupEconomy();
		setupPermissions();

		saveDefaultConfig();
		loSa=new LotterySave(this);
		manager=new LotteryManager(this);
		
		getCommand("lottery").setExecutor(new CommandExecutor_Lottery(this));


	}
	
	@Override
	public void onDisable(){
		log("disabled "+description.getFullName());
		manager.save();

	}
	public void log(String message){
		log.info(prefix+message);
	}
	
	private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public LotterySave getSave(){
		return loSa;
	}
	
	public LotteryManager getManager(){
		return manager;
	}
	





	

}
