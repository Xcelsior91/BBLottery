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
	
	public static String PREFIX;
	public static String TICKET_BOUGHT;
	public static String[] HELP;
	public static String ERROR_NO_PLAYER;
	public static String ERROR_RANGE;
	public static String ERROR_MONEY;
	public static String ERROR_MAXTICKETS;
	public static String INFO_JACKPOT;
	public static String INFO_TICKETS_ON_DRAW;
	public static String INFO_TAX;
	public static String INFO_PRICE;
	public static String INFO_INTRO;
	public static String INFO_TICKETS_BOUGHT;
	public static String STATS_INTRO;
	public static String STATS_TICKETS;
	public static String STATS_DRAWS;
	public static String STATS_WON_DRAWS;
	public static String STATS_WINNERS;
	public static String STATS_AMOUNT;
	public static String STATS_AMOUNT_PP;
	public static String DRAW_INTRO;
	public static String DRAW_SINGLE_WINNER;
	public static String DRAW_MULTI_WINNER;
	public static String DRAW_SINGLE_NOTIFICATION;
	public static String DRAW_MULTI_NOTIFICATION;
	public static String DRAW_NO_WINNER;
	
	
	public Localization(BBLottery plugin){
		this.plugin=plugin;
		reloadCustomConfig();
		PREFIX=replace(customConfig.getString("prefix"));
		TICKET_BOUGHT=replace(customConfig.getString("ticketBought"));
		HELP=replace((String[]) customConfig.getStringList("help").toArray());
		ERROR_NO_PLAYER=replace(customConfig.getString("error.noPlayer"));
		ERROR_RANGE=replace(customConfig.getString("error.ticketRange"));
		ERROR_MONEY=replace(customConfig.getString("error.money"));
		ERROR_MONEY=replace(customConfig.getString("error.maxTickets"));
		INFO_JACKPOT=replace(customConfig.getString("info.jackpot"));
		INFO_TICKETS_ON_DRAW=replace(customConfig.getString("info.ticketsOnDraw"));
		INFO_PRICE=replace(customConfig.getString("info.price"));
		INFO_TAX=replace(customConfig.getString("info.tax"));
		INFO_INTRO=replace(customConfig.getString("info.introBoughtTickets"));
		INFO_TICKETS_BOUGHT=replace(customConfig.getString("info.boughtTicketInfo"));
		STATS_INTRO=replace(customConfig.getString("stats.intro"));
		STATS_TICKETS=replace(customConfig.getString("stats.tickets"));
		STATS_DRAWS=replace(customConfig.getString("stats.draws"));
		STATS_WON_DRAWS=replace(customConfig.getString("stats.wonDraws"));
		STATS_WINNERS=replace(customConfig.getString("stats.winners"));
		STATS_AMOUNT=replace(customConfig.getString("stats.amount"));
		STATS_AMOUNT_PP=replace(customConfig.getString("stats.amountP"));
		DRAW_INTRO=replace(customConfig.getString("draw.intro"));
		DRAW_SINGLE_WINNER=replace(customConfig.getString("draw.singleWinner"));
		DRAW_MULTI_WINNER=replace(customConfig.getString("draw.multiWinner"));
		DRAW_SINGLE_NOTIFICATION=replace(customConfig.getString("draw.singleNotification"));
		DRAW_MULTI_NOTIFICATION=replace(customConfig.getString("draw.multiNotification"));
		DRAW_NO_WINNER=replace(customConfig.getString("draw.noWinner"));
	}
	
	public void reloadCustomConfig() {
	    if (customConfigFile == null) {
	    customConfigFile = new File(plugin.getDataFolder(), "Lang"+plugin.getConfig().getString("Language")+".yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource("LangEN.yml");
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

	
	public String replace(String orig){
		return orig.replaceAll("%r", ""+plugin.getManager().getRange())
				.replaceAll("%p", ""+plugin.getManager().getPrice())
				.replaceAll("%jp", ""+plugin.getManager().getJackpot())
				.replaceAll("%bt", ""+plugin.getManager().getBoughtTickets())
				.replaceAll("%t", ""+(plugin.getManager().getTax()*100));
	}
	
	private String[] replace(String[] orig){
		String[] newStringArray=new String[orig.length];
		for(int i=0;i<orig.length;i++){
			newStringArray[i]=replace(orig[i]);
		}
		return newStringArray;
	}


}
