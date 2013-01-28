package me.xcelsior.bblottery;

import java.util.ArrayList;
import java.util.List;

import me.xcelsior.bblottery.tasks.Task_Draw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class LotteryManager {
	 ArrayList<ArrayList<OfflinePlayer>> tickets;

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
	double totalAmountWon;

	public LotteryManager(BBLottery plugin) {
		this.plugin = plugin;
		
		loadConfigData(true);
		
		drawTaskID=plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Task_Draw(plugin), intervall);

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
			tickets=new ArrayList<ArrayList<OfflinePlayer>>(range);
	
			if (plugin.getSave().getCustomConfig().isSet("lottery.jackpot")) {
				jackpotCurrent = plugin.getSave().getCustomConfig()
						.getDouble("lottery.jackpot");
			} else {
				jackpotCurrent = jackpotInit;
			}
	
			for (int i = 0; i < range; i++) {
				ArrayList<OfflinePlayer> ptmp = new ArrayList<OfflinePlayer>();
				if (plugin.getSave().getCustomConfig().isSet("lottery.tickets." + i)) {
					@SuppressWarnings("unchecked")
					List<String> tmp = (List<String>) plugin.getSave()
							.getCustomConfig().getList("lottery.tickets." + i);
					for (String s : tmp) {
						ptmp.add(plugin.getServer().getOfflinePlayer(s));
					}
				}
				tickets.add(ptmp);
			}
			totalDraws=plugin.getSave().getCustomConfig().getInt("stats.total-draws");
			totalTickets=plugin.getSave().getCustomConfig().getInt("stats.total-tickets");
			totalWins=plugin.getSave().getCustomConfig().getInt("stats.total-wins");
			totalWinners=plugin.getSave().getCustomConfig().getInt("stats.total-winners");
			totalAmountWon=plugin.getSave().getCustomConfig().getDouble("stats.total-amount-won");
		}
	}

	/**
	 * Saves the lottery to the yml-file
	 */
	public void save() {
		plugin.getSave().getCustomConfig().set("lottery.jackpot", jackpotCurrent);
		for (int i = 0; i < range; i++) {
			ArrayList<String> tmp = new ArrayList<String>();
			for (OfflinePlayer p : tickets.get(i)) {
				tmp.add(p.getName());
			}
			plugin.getSave().getCustomConfig().set("lottery.tickets." + i, tmp);
		}
		plugin.getSave().getCustomConfig().set("stats.total-draws", totalDraws);
		plugin.getSave().getCustomConfig().set("stats.total-tickets", totalTickets);
		plugin.getSave().getCustomConfig().set("stats.total-wins", totalWins);
		plugin.getSave().getCustomConfig().set("stats.total-winners", totalWinners);
		plugin.getSave().getCustomConfig().set("stats.total-amount-won", totalAmountWon);
		plugin.getSave().saveCustomConfig();

	}

	/**
	 * Checks whether a Player may buy more lottery-tickets
	 * @param pl The Player to check for
	 * @return true if Player can buy more tickets, else false
	 */
	private boolean checkTickets(Player pl) {
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
		if (checkTickets(pl)) {
			int ticketNum = (int) (Math.random() * range);
			tickets.get(ticketNum).add(pl);
			pl.sendMessage(prefix+ChatColor.GREEN+"You bought a ticket! Your ticket has the number "+ChatColor.DARK_PURPLE+(ticketNum+1)+ChatColor.GREEN+"!");
			jackpotCurrent+=(price-(price*tax));
			totalTickets++;
			save();
			return true;
		} else
			pl.sendMessage(prefix+ChatColor.GREEN+"You allready have the maximum amount of tickets!");
			return false;
	}
	
	/**
	 * Buys a specific ticket for a player if he can buy any more
	 * @param pl The Player to buy the ticket for
	 * @param num The Ticket-Number to buy
	 * @return true if ticket was bought, else false which means pl has max Tickets
	 */
	public boolean buyTicket(Player pl, int num){
		if(checkTickets(pl)){
			tickets.get(num-1).add(pl);
			pl.sendMessage(prefix+ChatColor.GREEN+"You bought a ticket! Your ticket has the number "+ChatColor.DARK_PURPLE+num+ChatColor.GREEN+"!");
			jackpotCurrent+=(price-(price*tax));
			totalTickets++;
			save();
			return true;
		}else {
			pl.sendMessage(prefix+ChatColor.GREEN+"You allready have the maximum amount of tickets!");
			save();
			return false;
		}
	}

	/**
	 * Starts the drawing of the lottery and broadcasts the results, then starts a delayed task to draw again&resets the tickets
	 */
	public void draw() {
		totalDraws++;
		int drawn = ((int) (Math.random() * range)) + 1;
		Bukkit.getServer().broadcastMessage(
				prefix + ChatColor.GREEN + "Drawing............"
						+ ChatColor.GOLD + drawn + ChatColor.GREEN + "!");
		ArrayList<OfflinePlayer> winners = tickets.get(drawn - 1);
		if (winners.size() > 0) {
			String winner = winners.get(0).getName();
			for (OfflinePlayer pl : winners) {
				if(pl!=winners.get(0)){
					winner = winner + ", " + pl.getName();
				}
			}
			totalWins++;
			totalWinners+=winners.size();
			totalAmountWon+=jackpotCurrent;
			
			Bukkit.getServer().broadcastMessage(
					prefix
							+ ChatColor.GREEN
							+ (winners.size() > 1 ? "Winners are: "
									: "Winner is: ") + winner + "!");
			if(tickets.get(drawn - 1).size()==1){
				plugin.log("blub");
				if(tickets.get(drawn - 1).get(0).getPlayer()!=null){
					plugin.log("blubblub");
					tickets.get(drawn - 1).get(0).getPlayer().sendMessage(prefix+ChatColor.GREEN+"You won the whole pot! You got "+jackpotCurrent+"!");
				}
				BBLottery.economy.depositPlayer(tickets.get(drawn - 1).get(0).getName(), jackpotCurrent);
			}else{
				double amnt=jackpotCurrent/tickets.get(drawn - 1).size();
				for(OfflinePlayer p:tickets.get(drawn - 1)){
					if(p.getPlayer()!=null){
						p.getPlayer().sendMessage(prefix+ChatColor.GREEN+"You got "+amnt+BBLottery.economy.currencyNameSingular()+"of the pot!");
					}
					BBLottery.economy.depositPlayer(p.getName(), amnt);
				}
			}
			jackpotCurrent=jackpotInit;
			
			
		}else{
			Bukkit.getServer().broadcastMessage(prefix+ChatColor.GREEN+"Nobody Won the Lottery!");
			Bukkit.getServer().broadcastMessage(prefix+ChatColor.GREEN+"There are currently "+jackpotCurrent+BBLottery.economy.currencyNameSingular()+"in the pot!");
		}
		resetTickets();
		drawTaskID=plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Task_Draw(plugin), intervall);
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
		for(ArrayList<OfflinePlayer> lst:tickets){
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
		tickets=new ArrayList<ArrayList<OfflinePlayer>>(range);
		for(int i=0;i<range;i++){
			tickets.add(new ArrayList<OfflinePlayer>());
		}
	}
	
	/**
	 * Build a Sting array describing the Stats of the lottery till now
	 * @return a string[] of lengths 5
	 */
	public String[] getStats(){
		String[] stats=new String[5];
		
		stats[0]=prefix+ChatColor.DARK_GREEN+"Total tickets bought: "+totalTickets;
		stats[1]=prefix+ChatColor.DARK_GREEN+"Total draws commenced: "+totalDraws;
		stats[2]=prefix+ChatColor.DARK_GREEN+"Total number of won draws: "+totalWins;
		stats[3]=prefix+ChatColor.DARK_GREEN+"Total number of winners of the lottery: "+totalWinners;
		stats[4]=prefix+ChatColor.DARK_GREEN+"Total amount of money won through the lottery: "+totalAmountWon;
		save();
		return stats;
	}
}