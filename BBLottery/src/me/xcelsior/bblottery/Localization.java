package me.xcelsior.bblottery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Localization {

	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	
	private BBLottery plugin;

	public  String TICKET_BOUGHT;
	public  String[] HELP;
	public  String ERROR_NO_PLAYER;
	public  String ERROR_RANGE;
	public  String ERROR_MONEY;
	public  String ERROR_MAXTICKETS;
	public  String INFO_JACKPOT;
	public  String INFO_TICKETS_ON_DRAW;
	public  String INFO_DRAWS_SINCE_WIN;
	public  String INFO_TAX;
	public  String INFO_PRICE;
	public  String INFO_INTRO;
	public  String INFO_TICKETS_BOUGHT;
	public  String STATS_INTRO;
	public  String STATS_TICKETS;
	public  String STATS_DRAWS;
	public  String STATS_WON_DRAWS;
	public  String STATS_WINNERS;
	public  String STATS_AMOUNT;
	public  String STATS_AMOUNT_PP;
	public  String DRAW_INTRO;
	public  String DRAW_SINGLE_WINNER;
	public  String DRAW_MULTI_WINNER;
	public  String DRAW_SINGLE_NOTIFICATION;
	public  String DRAW_MULTI_NOTIFICATION;
	public  String DRAW_NO_WINNER;
	
	
	public Localization(BBLottery plugin){
		this.plugin=plugin;
		reloadCustomConfig();
		saveDefaultConfig();
	}
	
	public void reloadCustomConfig() {
	    if (customConfigFile == null) {
	    customConfigFile = new File(plugin.getDataFolder(), "Lang.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("Lang.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (customConfig == null) {
	        this.reloadCustomConfig();
	    }
	    return customConfig;
	}
	public void saveDefaultConfig() {
	    if (!customConfigFile.exists()) {            
	         this.plugin.saveResource("Lang.yml", false);
	     }
	}
	
	public void saveCustomConfig() {
	    if (customConfig == null || customConfigFile == null) {
	    return;
	    }
	    try {
	        getCustomConfig().save(customConfigFile);
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
	    }
	}
	
	public void loadStrings(){
		TICKET_BOUGHT=customConfig.getString("ticketBought");
		HELP=customConfig.getStringList("help").toArray(new String[9]);
		ERROR_NO_PLAYER=customConfig.getString("error.noPlayer");
		ERROR_RANGE=customConfig.getString("error.ticketRange");
		ERROR_MONEY=customConfig.getString("error.money");
		ERROR_MAXTICKETS=customConfig.getString("error.maxTickets");
		INFO_JACKPOT=customConfig.getString("info.jackpot");
		INFO_TICKETS_ON_DRAW=customConfig.getString("info.ticketsOnDraw");
		INFO_PRICE=customConfig.getString("info.price");
		INFO_TAX=customConfig.getString("info.tax");
		INFO_INTRO=customConfig.getString("info.introBoughtTickets");
		INFO_TICKETS_BOUGHT=customConfig.getString("info.boughtTicketInfo");
		INFO_DRAWS_SINCE_WIN=customConfig.getString("info.drawsSinceWin");
		STATS_INTRO=customConfig.getString("stats.intro");
		STATS_TICKETS=customConfig.getString("stats.tickets");
		STATS_DRAWS=customConfig.getString("stats.draws");
		STATS_WON_DRAWS=customConfig.getString("stats.wonDraws");
		STATS_WINNERS=customConfig.getString("stats.winners");
		STATS_AMOUNT=customConfig.getString("stats.amount");
		STATS_AMOUNT_PP=customConfig.getString("stats.amountPP");
		DRAW_INTRO=customConfig.getString("draw.intro");
		DRAW_SINGLE_WINNER=customConfig.getString("draw.singleWinner");
		DRAW_MULTI_WINNER=customConfig.getString("draw.multiWinner");
		DRAW_SINGLE_NOTIFICATION=customConfig.getString("draw.singleNotification");
		DRAW_MULTI_NOTIFICATION=customConfig.getString("draw.multiNotification");
		DRAW_NO_WINNER=customConfig.getString("draw.noWinner");
	}

	
	public String replace(String orig){
		String s=""+orig;
		s=s.replaceAll("%r", ""+plugin.getManager().getRange());
		s=s.replaceAll("%pr", ""+plugin.getManager().getPrice());
		s=s.replaceAll("%jp", ""+plugin.getManager().getJackpot());
		s=s.replaceAll("%bt", ""+plugin.getManager().getBoughtTickets());
		s=s.replaceAll("%t", ""+(plugin.getManager().getTax()*100));
		return s;
				
				
				
				
	}
	
	public String[] replace(String[] orig){
		String[] newStringArray=new String[orig.length];
		for(int i=0;i<orig.length;i++){
			newStringArray[i]=replace(orig[i]);
		}
		return newStringArray;
	}


}
