package me.xcelsior.bblottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.xcelsior.bblottery.tasks.Task_Draw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class LotteryManager {
	ArrayList<ArrayList<String>> tickets;

	double jackpotInit;

	double jackpotCurrent;
	long intervall;
	int maxTickets;
	double price;
	double tax;
	int range;
	String prefix = ChatColor.GOLD + "[Lottery]";
	BBLottery plugin;
	int drawTaskID=-1;
	int totalDraws;
	int totalTickets;
	int totalWins;
	int totalWinners;
	int drawsSinceLastWin;
	double totalAmountWon;
	HashMap<String, String> playerStats;
	Localization loc;

	public LotteryManager(BBLottery plugin) {
		this.plugin = plugin;
		
		loadConfigData(true);
		loc=plugin.getLoc();
		if(plugin.getConfig().getDouble("intervall")!=0){
			drawTaskID=plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Task_Draw(plugin), intervall);
		}

	}
	
	/**
	 * Loads data from config to memory, if lotteryFlag is set it also loads the lottery
	 * @param lotteryFlag whether to load the lottery-data or only the config
	 */
	public void loadConfigData(boolean lotteryFlag){
		jackpotInit = plugin.getConfig().getDouble("pot");
		intervall = (long) (plugin.getConfig().getDouble("intervall") * 60*20);// minutes to Serverticks
		maxTickets = plugin.getConfig().getInt("maxTickets");
		range = plugin.getConfig().getInt("range");
		price=plugin.getConfig().getDouble("ticketPrice");
		tax=plugin.getConfig().getDouble("tax");
		
		if(lotteryFlag){
			tickets=new ArrayList<ArrayList<String>>();
	
			if (plugin.getSave().getCustomConfig().isSet("lottery.jackpot")) {
				jackpotCurrent = plugin.getSave().getCustomConfig()
						.getDouble("lottery.jackpot");
			} else {
				jackpotCurrent = jackpotInit;
			}
			
			if(plugin.getSave().getCustomConfig().isSet("lottery.drawsSinceLastWin")){
				drawsSinceLastWin=plugin.getSave().getCustomConfig().getInt("lottery.jackpot");
			}else{
				drawsSinceLastWin=0;
			}
	
			if(range!=-1){
			for (int i = 0; i < range; i++) {
				ArrayList<String> ptmp = new ArrayList<String>();
				if (plugin.getSave().getCustomConfig().isSet("lottery.tickets." + i)) {
					@SuppressWarnings("unchecked")
					List<String> tmp = (List<String>) plugin.getSave()
							.getCustomConfig().getList("lottery.tickets." + i);
					for (String s : tmp) {
						ptmp.add(plugin.getServer().getOfflinePlayer(s).getName());
					}
				}
				tickets.add(ptmp);
			}
			}else{
				int i=0;
				while(plugin.getSave().getCustomConfig().isSet("lottery.tickets." + i)){
					ArrayList<String> ptmp = new ArrayList<String>();
					if (plugin.getSave().getCustomConfig().isSet("lottery.tickets." + i)) {
						@SuppressWarnings("unchecked")
						List<String> tmp = (List<String>) plugin.getSave()
								.getCustomConfig().getList("lottery.tickets." + i);
						for (String s : tmp) {
							ptmp.add(plugin.getServer().getOfflinePlayer(s).getName());
						}
					}
					tickets.add(ptmp);
					i++;
				}
			}
			totalDraws=plugin.getSave().getCustomConfig().getInt("stats.total-draws");
			totalTickets=plugin.getSave().getCustomConfig().getInt("stats.total-tickets");
			totalWins=plugin.getSave().getCustomConfig().getInt("stats.total-wins");
			totalWinners=plugin.getSave().getCustomConfig().getInt("stats.total-winners");
			totalAmountWon=plugin.getSave().getCustomConfig().getDouble("stats.total-amount-won");
			
			playerStats=new HashMap<>();
			for(String s:plugin.getSave().getCustomConfig().getStringList("playerstats")){
				String[] ps=s.split(":");
				String p=ps[0];
				String st=ps[1]+":"+ps[2];
				playerStats.put(p, st);
			}
		}
	}

	/**
	 * Saves the lottery to the yml-file
	 */
	public void save() {
		plugin.getSave().getCustomConfig().set("lottery.jackpot", jackpotCurrent);
		plugin.getSave().getCustomConfig().set("lottery.drawsSinceLastWin", drawsSinceLastWin);
		
		if(range!=-1){
			for (int i = 0; i < range; i++) {
				ArrayList<String> tmp = new ArrayList<String>();
				for (String p : tickets.get(i)) {
					tmp.add(p);
				}
				plugin.getSave().getCustomConfig().set("lottery.tickets." + i, tmp);
			}
		}else{
			int i=0;
			while(!tickets.get(i).isEmpty()){
				ArrayList<String> tmp = new ArrayList<String>();
				for (String p : tickets.get(i)) {
					tmp.add(p);
				}
				plugin.getSave().getCustomConfig().set("lottery.tickets." + i, tmp);
				i++;
			}
			
		}
		plugin.getSave().getCustomConfig().set("stats.total-draws", totalDraws);
		plugin.getSave().getCustomConfig().set("stats.total-tickets", totalTickets);
		plugin.getSave().getCustomConfig().set("stats.total-wins", totalWins);
		plugin.getSave().getCustomConfig().set("stats.total-winners", totalWinners);
		plugin.getSave().getCustomConfig().set("stats.total-amount-won", totalAmountWon);
		

		ArrayList<String> list=new ArrayList<>();
		for(String p:playerStats.keySet()){
			list.add(p+":"+playerStats.get(p));
		}
		plugin.getSave().getCustomConfig().set("playerStats", list);
		
		plugin.getSave().saveCustomConfig();

	}

	/**
	 * Checks whether a Player may buy more lottery-tickets
	 * @param pl The Player to check for
	 * @return true if Player can buy more tickets, else false
	 */
	private boolean checkTickets(String pl) {
		int amnt = 0;
		for (@SuppressWarnings("rawtypes") List lst : tickets) {
			if (lst.contains(pl)) {
				amnt++;
			}
		}
		if (amnt < maxTickets) {
			return true;
		}
		return false;
	}

	
	
	/**
	 * Buys a random ticket for a player if he can buy more tickets 
	 * @param pl Player who buys the ticket
	 * @return true if ticket was bought, else false which means pl has max Tickets
	 */
	public boolean buyTicket(Player pl) {
		if (checkTickets(pl.getName())) {
			int ticketNum;
			if(range!=-1){
				ticketNum = (int) (Math.random() * range);
			}else{
				ticketNum=0;
				while(!tickets.get(ticketNum).isEmpty()){
					ticketNum++;
				}
				if(tickets.size()<=ticketNum){
					tickets.add(new ArrayList<String>());
				}
			}
			tickets.get(ticketNum).add(pl.getName());
			pl.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.TICKET_BOUGHT).replaceAll("%[n,m]",""+ChatColor.DARK_PURPLE+(ticketNum+1)+ChatColor.GREEN));
			jackpotCurrent+=(price-(price*tax));
			totalTickets++;
			save();
			return true;
		} else{
			pl.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.ERROR_MAXTICKETS));
			return false;
		}
	}
	
	/**
	 * Buys a specific ticket for a player if he can buy any more
	 * @param pl The Player to buy the ticket for
	 * @param num The Ticket-Number to buy
	 * @return true if ticket was bought, else false which means pl has max Tickets
	 */
	public boolean buyTicket(Player pl, int num){
		if(checkTickets(pl.getName())){
			tickets.get(num-1).add(pl.getName());
			pl.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.TICKET_BOUGHT).replaceAll("%[n,m]",""+ChatColor.DARK_PURPLE+num+ChatColor.GREEN));
			jackpotCurrent+=(price-(price*tax));
			totalTickets++;
			save();
			return true;
		}else {
			pl.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.ERROR_MAXTICKETS));
			save();
			return false;
		}
	}
	
	/**
	 * Lets a player buy a Ticket for another player
	 * @param proxy The Player buying the ticket
	 * @param pl The player receiving the ticket
	 * @return Whether the Ticket was bought successfully or not
	 */
	public boolean proxyBuyTicket(String proxy, Player pl){
		if(checkTickets(pl.getName())){
			int ticketNum;
			if(range!=-1){
				ticketNum = (int) (Math.random() * range);
			}else{
				ticketNum=0;
				while(tickets.size()>ticketNum&&!tickets.get(ticketNum).isEmpty()){
					ticketNum++;
				}
				if(tickets.size()<=ticketNum){
					tickets.add(new ArrayList<String>());
				}
			}
			tickets.get(ticketNum).add(pl.getName());
			pl.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.TICKET_BOUGHTBYOTHER).replaceAll("%[n,m]",""+ChatColor.DARK_PURPLE+(ticketNum+1)+ChatColor.GREEN).replaceAll("%pn", proxy));
			if(!proxy.equalsIgnoreCase("Console"))
				plugin.getServer().getPlayerExact(proxy).sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.TICKET_BOUGHTOTHER).replaceAll("%[n,m]",""+ChatColor.DARK_PURPLE+(ticketNum+1)+ChatColor.GREEN).replaceAll("%pn", pl.getName()));
			jackpotCurrent+=(price-(price*tax));
			totalTickets++;
			save();
			return true;
		}else{
			if(!proxy.equalsIgnoreCase("Console"))
				plugin.getServer().getPlayerExact(proxy).sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.ERROR_OTHER_MAXTICKETS));
			return false;
		}
	}
	
	/**
	 * Lets a player buy a specific Ticket for another player
	 * @param proxy The Player buying the ticket
	 * @param pl The player receiving the ticket
	 * @param num The ticket proxy wants to buy
	 * @return Whether the Ticket was bought successfully or not
	 */
	public boolean proxyBuyTicket(String proxy, Player pl, int num){
		if(checkTickets(pl.getName())){
			tickets.get(num-1).add(pl.getName());
			pl.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.TICKET_BOUGHTBYOTHER).replaceAll("%[n,m]",""+ChatColor.DARK_PURPLE+num+ChatColor.GREEN).replaceAll("%pn", proxy));
			if(!proxy.equalsIgnoreCase("Console"))
				plugin.getServer().getPlayerExact(proxy).sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.TICKET_BOUGHTOTHER).replaceAll("%[n,m]",""+ChatColor.DARK_PURPLE+(num+1)+ChatColor.GREEN));
			jackpotCurrent+=(price-(price*tax));
			totalTickets++;
			save();
			return true;
		}else {
			if(!proxy.equalsIgnoreCase("Console"))
				plugin.getServer().getPlayerExact(proxy).sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.ERROR_OTHER_MAXTICKETS));
			save();
			return false;
		}
	}

	/**
	 * Starts the drawing of the lottery and broadcasts the results, then starts a delayed task to draw again&resets the tickets
	 */
	public void draw() {
		totalDraws++;
		int drawn;
		if(range!=-1){
		 drawn = ((int) (Math.random() * range)) + 1;
		}else{
			int i=0;
			while(tickets.size()>i&&!tickets.get(i).isEmpty()){
				i++;
			}
			drawn = ((int) (Math.random() * i)) + 1;
		}
		Bukkit.getServer().broadcastMessage(
				prefix + ChatColor.GREEN + loc.replace(loc.DRAW_INTRO).replaceAll("%n", ""+ ChatColor.GOLD + drawn + ChatColor.GREEN));
		ArrayList<String> winners = tickets.get(drawn - 1);
		if (winners.size() > 0) {
			String winner = winners.get(0);
			for (String pl : winners) {
				if(pl!=winners.get(0)){
					winner = winner + ", " + pl;
				}
			}
			totalWins++;
			totalWinners+=winners.size();
			totalAmountWon+=jackpotCurrent;
			
			Bukkit.getServer().broadcastMessage(
					prefix
							+ ChatColor.GREEN
							+ (winners.size() > 1 ? loc.replace(loc.DRAW_MULTI_WINNER).replaceAll("%pn", winner)
									: loc.replace(loc.DRAW_SINGLE_WINNER).replaceAll("%pn", winner)));
			if(tickets.get(drawn - 1).size()==1){//single winner
				String pl=tickets.get(drawn-1).get(0);
				if(pl!=null&&!pl.equals("")){
					Bukkit.getPlayerExact(pl).sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.DRAW_SINGLE_NOTIFICATION).replaceAll("%[n, m]",""+jackpotCurrent));
				}
				BBLottery.economy.depositPlayer(pl, jackpotCurrent);
				
				
				if(playerStats.get(pl)==null){
					playerStats.put(pl, "0:0");
				}
				String[] st=playerStats.get(pl).split(":");
				double totalAmntPlayerWon=Double.parseDouble(st[0])+jackpotCurrent;
				int totalTimesWon=Integer.parseInt(st[1])+1;
				String newStats=totalAmntPlayerWon+":"+totalTimesWon;
				playerStats.put(pl, newStats);
				
			}else{//multiple winners
				double amnt=jackpotCurrent/tickets.get(drawn - 1).size();
				for(String p:tickets.get(drawn - 1)){
					if(p!=null&&!p.equals("")){
						Bukkit.getPlayerExact(p).sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.DRAW_SINGLE_NOTIFICATION).replaceAll("%[n, m]",""+amnt));
					}
					BBLottery.economy.depositPlayer(p, amnt);
					
					String[] st=playerStats.get(p).split(":");
					double totalAmntPlayerWon=Double.parseDouble(st[0])+amnt;
					int totalTimesWon=Integer.parseInt(st[1])+1;
					String newStats=totalAmntPlayerWon+":"+totalTimesWon;
					playerStats.put(p, newStats);
				}
			}
			jackpotCurrent=jackpotInit;
			
			
		}else{
			Bukkit.getServer().broadcastMessage(prefix+ChatColor.GREEN+loc.replace(loc.DRAW_NO_WINNER));
			Bukkit.getServer().broadcastMessage(prefix+ChatColor.GREEN+loc.replace(loc.INFO_JACKPOT));
			drawsSinceLastWin++;
		}
		resetTickets();
		if(plugin.getConfig().getDouble("intervall")!=0){
			drawTaskID=plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Task_Draw(plugin), intervall);
		}
		save();

	}
	
	
	/**
	 *Forces a drawing of the lottery, stops Task of the regular drawing 
	 */
	public void forceDraw(){
		if(drawTaskID!=-1){
			Bukkit.getScheduler().cancelTask(drawTaskID);
			plugin.log("Old drawing cancelled, starting new one!");
			draw();
		}else{
			draw();
		}
	}
	
	/**
	 * Collects the numbers of the ticket a player possesses
	 * @param pl the Players to collect theTicketnumbers for
	 * @return an full int[] which contains all numbers of tickets a player possesses
	 */
	public int[] getTickets(Player pl){
		ArrayList<Integer> lst=new ArrayList<Integer>();
		
		for(int i=0; i<tickets.size();i++){
			if(tickets.get(i).contains(pl)){
				lst.add(i+1);
			}
		}
		int[] result=new int[lst.size()];
		for(int i=0;i<lst.size();i++){
			result[i]=lst.get(i);
		}
		return result;
	}
	
	
	/**
	 * @return the intervall between drawings in Serverticks
	 */
	public long getIntervall(){
		return intervall;
	}
	
	/**
	 * @return the price of one ticket
	 */
	public double getPrice(){
		return price;
	}
	
	/**
	 * Calculates the number of sold tickets for a given ticketnumber
	 * @param i the ticketnumber
	 * @return the amount of tickets sold for this number
	 */
	public double getNumOfTicketsForTicket(int i){
		return tickets.get(i).size();
	}
	
	/**
	 * @return The highest number that can be drawn in the Lottery
	 */
	public int getRange(){
		return range;
	}
	
	/**
	 * @return the current jackpot
	 */
	public double getJackpot(){
		return jackpotCurrent;
	}
	/**
	 * @return the total number of sold tickets for the current drawing
	 */
	public int getBoughtTickets(){
		int amnt=0;
		for(ArrayList<String> lst:tickets){
			amnt+=lst.size();
		}
		return amnt;
	}

	/**
	 * @return the Tax on tickets
	 */
	public double getTax() {
		return tax;
	}
	/**
	 * resets the lists of bought tickets
	 */
	private void resetTickets(){
		tickets=new ArrayList<ArrayList<String>>();
		if(range!=-1){
		for(int i=0;i<range;i++){
			tickets.add(new ArrayList<String>());
		}}
		else{
			int players=plugin.getServer().getOfflinePlayers().length;
			for(int i=0;i<players;i++){
				tickets.add(new ArrayList<String>());
			}
		}
		
	}
	
	/**
	 * Build a Sting array describing the Stats of the lottery till now
	 * @return a string[] of lengths 5
	 */
	public String[] getStats(){
		String[] stats=new String[5];
		
		stats[0]=prefix+ChatColor.DARK_GREEN+loc.replace(loc.STATS_TICKETS).replaceAll("%n", ""+totalTickets);
		stats[1]=prefix+ChatColor.DARK_GREEN+loc.replace(loc.STATS_DRAWS).replaceAll("%n", ""+totalDraws);
		stats[2]=prefix+ChatColor.DARK_GREEN+loc.replace(loc.STATS_WON_DRAWS).replaceAll("%n", ""+totalWins);
		stats[3]=prefix+ChatColor.DARK_GREEN+loc.replace(loc.STATS_WINNERS).replaceAll("%n", ""+totalWinners);
		stats[4]=prefix+ChatColor.DARK_GREEN+loc.replace(loc.STATS_AMOUNT).replaceAll("%n", ""+totalAmountWon);
		save();
		return stats;
	}
	
	public ArrayList<String> getPlayerStats(){
		ArrayList<String> stats=new ArrayList<>();
		
		for(String p:playerStats.keySet()){
			String[] s=playerStats.get(p).split(":");
			stats.add(prefix+ChatColor.DARK_GREEN+loc.replace(loc.STATS_AMOUNT_PP).replaceAll("%pn", p).replaceAll("%n",s[0]).replaceAll("%m", s[1]));
		}
		
		
		return stats;
	}
	
	public int getDrawsSinceLastWin(){
		return drawsSinceLastWin;
	}
}