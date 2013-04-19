package me.xcelsior.bblottery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Localization {

	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	
	private BBLottery plugin;

	public  String TICKET_BOUGHT;
	public  String TICKET_BOUGHTOTHER;
	public  String TICKET_BOUGHTBYOTHER;
	public  String[] HELP;
	public  String ERROR_NO_PLAYER;
	public  String ERROR_RANGE;
	public  String ERROR_MONEY;
	public  String ERROR_MAXTICKETS;
	public 	String ERROR_OTHER_MAXTICKETS;
	public  String ERROR_INVALIDPLAYER;
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
	     }else{
	    	 checkDefaults();
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
		TICKET_BOUGHTOTHER=customConfig.getString("ticketBoughtOther");
		TICKET_BOUGHTBYOTHER=customConfig.getString("ticketBoughByProxy");
		HELP=customConfig.getStringList("help").toArray(new String[10]);
		ERROR_NO_PLAYER=customConfig.getString("error.noPlayer");
		ERROR_RANGE=customConfig.getString("error.ticketRange");
		ERROR_MONEY=customConfig.getString("error.money");
		ERROR_MAXTICKETS=customConfig.getString("error.maxTickets");
		ERROR_OTHER_MAXTICKETS=customConfig.getString("otherMaxTickets");
		ERROR_INVALIDPLAYER=customConfig.getString("error.invalidPlayer");
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

	
	private void checkDefaults(){
		
		String s=convertStreamToString(this.plugin.getResource("Lang.yml"));
		String[] l=s.split("\\n");
		
		for(int i=0;i<l.length;i++){
			this.plugin.log("miep "+i+": "+l[i]);
		}
		
		if(TICKET_BOUGHT==null||TICKET_BOUGHT.equals("")){
			TICKET_BOUGHT=l[10].replaceAll("\"", "").split(":")[1];
		}
		if(TICKET_BOUGHTOTHER==null||TICKET_BOUGHTOTHER.equals("")){
			TICKET_BOUGHTOTHER=l[11].replaceAll("\"", "").split(":")[1];
		}
		if(TICKET_BOUGHTBYOTHER==null||TICKET_BOUGHTBYOTHER.equals("")){
			TICKET_BOUGHTBYOTHER=l[12].replaceAll("\"", "").split(":")[1];
		}
		if(HELP==null){
			for(int i=0;i<10;i++){
				HELP[i]=l[i+15].replaceAll("\"", "").split(":")[1];
			}
		}else{
			for(int i=0;i<10;i++){
				if(HELP[i]==null||HELP[i].equals("")){
					for(int k=0;k<10;k++){
						HELP[k]=l[k+15].replaceAll("\"", "").split(":")[1];
					}
				}
			}
		}
		
		if(ERROR_NO_PLAYER==null||ERROR_NO_PLAYER.equals("")){
			ERROR_NO_PLAYER=l[49].replaceAll("\"", "").split(":")[1];
		}
		if(ERROR_RANGE==null||ERROR_RANGE.equals("")){
			ERROR_RANGE=l[50].replaceAll("\"", "").split(":")[1];
		}
		if(ERROR_MONEY==null||ERROR_MONEY.equals("")){
			ERROR_MONEY=l[51].replaceAll("\"", "").split(":")[1];
		}
		if(ERROR_MAXTICKETS==null||ERROR_MAXTICKETS.equals("")){
			ERROR_MAXTICKETS=l[52].replaceAll("\"", "").split(":")[1];
		}
		if(ERROR_OTHER_MAXTICKETS==null||ERROR_OTHER_MAXTICKETS.equals("")){
			ERROR_OTHER_MAXTICKETS=l[53].replaceAll("\"", "").split(":")[1];
		}
		if(ERROR_INVALIDPLAYER==null||ERROR_INVALIDPLAYER.equals("")){
			ERROR_INVALIDPLAYER=l[54].replaceAll("\"", "").split(":")[1];
		}
		if(INFO_JACKPOT==null||INFO_JACKPOT.equals("")){
			INFO_JACKPOT=l[26].replaceAll("\"", "").split(":")[1];
		}
		if(INFO_TICKETS_ON_DRAW==null||INFO_TICKETS_ON_DRAW.equals("")){
			INFO_TICKETS_ON_DRAW=l[28].replaceAll("\"", "").split(":")[1];
		}
		if(INFO_PRICE==null||INFO_PRICE.equals("")){
			INFO_PRICE=l[27].replaceAll("\"", "").split(":")[1];
		}
		if(INFO_TAX==null||INFO_TAX.equals("")){
			INFO_TAX=l[30].replaceAll("\"", "").split(":")[1];
		}
		if(INFO_INTRO==null||INFO_INTRO.equals("")){
			INFO_INTRO=l[31].replaceAll("\"", "").split(":")[1];
		}
		if(INFO_TICKETS_BOUGHT==null||INFO_TICKETS_BOUGHT.equals("")){
			INFO_TICKETS_BOUGHT=l[32].replaceAll("\"", "").split(":")[1];
		}
		if(INFO_DRAWS_SINCE_WIN==null||INFO_DRAWS_SINCE_WIN.equals("")){
			INFO_DRAWS_SINCE_WIN=l[12].replaceAll("\"", "").split(":")[1];
		}
		if(STATS_INTRO==null||STATS_INTRO.equals("")){
			STATS_INTRO=l[34].replaceAll("\"", "").split(":")[1];
		}
		if(STATS_TICKETS==null||STATS_TICKETS.equals("")){
			STATS_TICKETS=l[35].replaceAll("\"", "").split(":")[1];
		}
		if(STATS_DRAWS==null||STATS_DRAWS.equals("")){
			STATS_DRAWS=l[36].replaceAll("\"", "").split(":")[1];
		}
		if(STATS_WON_DRAWS==null||STATS_WON_DRAWS.equals("")){
			STATS_WON_DRAWS=l[37].replaceAll("\"", "").split(":")[1];
		}
		if(STATS_WINNERS==null||STATS_WINNERS.equals("")){
			STATS_WINNERS=l[38].replaceAll("\"", "").split(":")[1];
		}
		if(STATS_AMOUNT==null||STATS_AMOUNT.equals("")){
			STATS_AMOUNT=l[39].replaceAll("\"", "").split(":")[1];
		}
		if(STATS_AMOUNT_PP==null||STATS_AMOUNT_PP.equals("")){
			STATS_AMOUNT_PP=l[40].replaceAll("\"", "").split(":")[1];
		}
		if(DRAW_INTRO==null||DRAW_INTRO.equals("")){
			DRAW_INTRO=l[42].replaceAll("\"", "").split(":")[1];
		}
		if(DRAW_SINGLE_WINNER==null||DRAW_SINGLE_WINNER.equals("")){
			DRAW_SINGLE_WINNER=l[43].replaceAll("\"", "").split(":")[1];
		}
		if(DRAW_MULTI_WINNER==null||DRAW_MULTI_WINNER.equals("")){
			DRAW_MULTI_WINNER=l[45].replaceAll("\"", "").split(":")[1];
		}
		if(DRAW_SINGLE_NOTIFICATION==null||DRAW_SINGLE_NOTIFICATION.equals("")){
			DRAW_SINGLE_NOTIFICATION=l[44].replaceAll("\"", "").split(":")[1];
		}
		if(DRAW_MULTI_NOTIFICATION==null||DRAW_MULTI_NOTIFICATION.equals("")){
			DRAW_MULTI_NOTIFICATION=l[46].replaceAll("\"", "").split(":")[1];
		}
		if(DRAW_NO_WINNER==null||DRAW_NO_WINNER.equals("")){
			DRAW_NO_WINNER=l[47].replaceAll("\"", "").split(":")[1];
		}
	}
	
	private String convertStreamToString(InputStream is) {
	    Scanner s = new Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

}
