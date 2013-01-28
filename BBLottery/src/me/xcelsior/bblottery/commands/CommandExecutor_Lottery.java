/*
 * BBLottery - by Xcelsior
 * http://
 *
 * powered by Kickstarter
 */

package me.xcelsior.bblottery.commands;



import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.xcelsior.bblottery.BBLottery;
import me.xcelsior.bblottery.Perms;


public class CommandExecutor_Lottery implements CommandExecutor {
	private BBLottery plugin;
	String prefix = ChatColor.GOLD + "[Lottery]"+ChatColor.GREEN;
	String[] usage={ChatColor.GREEN+"---------"+prefix+"---------",
			ChatColor.GREEN+"This is the help for the Lottery!",
			ChatColor.GREEN+"There are the following Commands that you may have acces to:",
			ChatColor.YELLOW+"/lottery           "+ChatColor.GREEN+"Shows this help.",
			ChatColor.YELLOW+"/lottery buy [tip] "+ChatColor.GREEN+"Buys yourself a ticket for the next lottery, you may chose a number yourself if you want.",
			ChatColor.YELLOW+"/lottery info      "+ChatColor.GREEN+"Shows you the amount of money in the pot and, if you have bought a ticket, how much money you would get if the pot is split.",
			ChatColor.GREEN+"The following commands are mostly for ops or Admins, so you may not have acces to them:",
			ChatColor.YELLOW+"/lottery reload [-f]   "+ChatColor.GREEN+"Reloads the config, if -f is set it also reloads the lottery-data.!",
			ChatColor.YELLOW+"/lottery draw      "+ChatColor.GREEN+"Forces the next drawing of the lottery."};

	public CommandExecutor_Lottery(BBLottery plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
		if (command.getName().equalsIgnoreCase("lottery")) {
			if(args.length==0){
				sender.sendMessage(usage);
				return true;
			}else {
				if(args[0].equalsIgnoreCase("buy")){
					if (!(sender instanceof Player)) {
						sender.sendMessage("You have to be a player to buy tickets!");
						return true;
					}else{
						buyTicket(sender, args);
					}
					
				}
				else if(args[0].equalsIgnoreCase("info")){
					info(sender);
				}
				else if(args[0].equalsIgnoreCase("stats")){
					stats(sender);
				}
				else if(args[0].equalsIgnoreCase("reload")){
					if(args.length>1&&args[1].equalsIgnoreCase("-f")){
						reload(sender, true);
					}
					reload(sender, false);
				}
				else if(args[0].equalsIgnoreCase("draw")){
					forceDraw(sender);
				}
				else{
					sender.sendMessage(usage);
				}
			}
			plugin.getManager().save();
			return true;
		}
		plugin.getManager().save();
		return false;
	}
	
	private void buyTicket(CommandSender sender, String[] args){
		if(Perms.hasPerm(sender, Perms.BUY)){
			if(BBLottery.economy.getBalance(sender.getName())>=plugin.getManager().getPrice()){
				if(args.length==2){
					if(args[1].matches("\\d+")){
						int ticketNum=Integer.parseInt(args[1]);
						if(ticketNum>0&&ticketNum<=plugin.getManager().getRange()){
							if(plugin.getManager().buyTicket((Player)sender, ticketNum)){
								BBLottery.economy.withdrawPlayer(sender.getName(), plugin.getManager().getPrice());
							}
						}else{
							sender.sendMessage(prefix+"Please enter a valid ticketnumber between 1 and "+plugin.getManager().getRange()+"!");
						}
					}else{
						sender.sendMessage(prefix+"Please enter a valid ticketnumber between 1 and "+plugin.getManager().getRange()+"!");
					}
				}else{
					if(plugin.getManager().buyTicket((Player)sender)){
						BBLottery.economy.withdrawPlayer(sender.getName(), plugin.getManager().getPrice());
					}
				}
			}else{
				sender.sendMessage(prefix+ChatColor.GREEN+"You do not have enough money to buy a lottery-ticket!");
			}
		}else{
			sendNoPerm(sender);
		}
	}
	
	private void info(CommandSender sender){
		double jp=plugin.getManager().getJackpot();
		double price=plugin.getManager().getPrice();
		int ticketNum=plugin.getManager().getBoughtTickets();
		double tax=plugin.getManager().getTax()*100;
		
		sender.sendMessage(prefix+"Current Jackpot: "+jp+".");
		sender.sendMessage(prefix+"Ticketprice: "+price);
		sender.sendMessage(prefix+"Overall tickets bought for current draw: "+ ticketNum+".");
		sender.sendMessage(prefix+"The tax on the tickets is "+tax+"%.");
		
		if(sender instanceof Player){
			sender.sendMessage("");
			int[] ticketNums=plugin.getManager().getTickets((Player)sender);
			if(ticketNums.length>0){
				sender.sendMessage(prefix+"You bought the following tickets:");
				for(int i=0;i<ticketNums.length;i++){
					double fraction=jp/plugin.getManager().getNumOfTicketsForTicket(ticketNums[i]-1);
					sender.sendMessage(prefix+" Ticket no. "+ticketNums[i]+ ", you would get "+fraction+" if this ticket would get drawn now.");
				}
			}
		}
	}
	
	private void stats(CommandSender sender){
		sender.sendMessage(plugin.getManager().getStats());
		sender.sendMessage(prefix+"Player-Stats");
		for(String s:plugin.getManager().getPlayerStats()){
			sender.sendMessage(s);
		}
	}
	
	private void forceDraw(CommandSender sender){
		if(Perms.hasPerm(sender, Perms.DRAW)){
			plugin.getManager().forceDraw();
		}else{
			sendNoPerm(sender);
		}
	}
	
	private void reload(CommandSender sender, boolean flag){
		if(Perms.hasPerm(sender, Perms.RELOAD)){
			if(flag){
				sender.sendMessage(prefix+"Reloading config...");
				plugin.reloadConfig();
				sender.sendMessage(prefix+"Reloading lottery...");
				plugin.getSave().reloadCustomConfig();
				sender.sendMessage(prefix+"Finished!");
			}else{
				plugin.getManager().save();
				sender.sendMessage(prefix+"Reloading config...");
				plugin.reloadConfig();
				sender.sendMessage(prefix+"Finished!");
			}
			plugin.getManager().loadConfigData(flag);
		}else{
			sendNoPerm(sender);	
		}
	}
	
	private void sendNoPerm(CommandSender sender){
		sender.sendMessage(prefix+"Please enter a valid ticketnumber!");
	}
}
