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
import me.xcelsior.bblottery.Localization;
import me.xcelsior.bblottery.Perms;


public class CommandExecutor_Lottery implements CommandExecutor {
	private BBLottery plugin;
	String prefix = ChatColor.GOLD + "["+Localization.PREFIX+"]"+ChatColor.GREEN;
	String[] usage={ChatColor.GREEN+"---------"+prefix+"---------",
			ChatColor.GREEN+Localization.HELP[0],
			ChatColor.GREEN+Localization.HELP[1],
			ChatColor.YELLOW+"/lottery           "+ChatColor.GREEN+Localization.HELP[2],
			ChatColor.YELLOW+"/lottery buy [tip] "+ChatColor.GREEN+Localization.HELP[3],
			ChatColor.YELLOW+"/lottery info      "+ChatColor.GREEN+Localization.HELP[4],
			ChatColor.GREEN+Localization.HELP[5],
			ChatColor.YELLOW+"/lottery reload [-f]   "+ChatColor.GREEN+Localization.HELP[6],
			ChatColor.YELLOW+"/lottery draw      "+ChatColor.GREEN+Localization.HELP[7]};

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
						sender.sendMessage(Localization.ERROR_NO_PLAYER);
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
							sender.sendMessage(prefix+Localization.ERROR_RANGE);
						}
					}else{
						sender.sendMessage(prefix+Localization.ERROR_RANGE);
					}
				}else{
					if(plugin.getManager().buyTicket((Player)sender)){
						BBLottery.economy.withdrawPlayer(sender.getName(), plugin.getManager().getPrice());
					}
				}
			}else{
				sender.sendMessage(prefix+ChatColor.GREEN+Localization.ERROR_MONEY);
			}
		}else{
			sendNoPerm(sender);
		}
	}
	
	private void info(CommandSender sender){
		double jp=plugin.getManager().getJackpot();
		
		sender.sendMessage(prefix+Localization.INFO_JACKPOT);
		sender.sendMessage(prefix+Localization.INFO_PRICE);
		sender.sendMessage(prefix+Localization.INFO_TICKETS_ON_DRAW);
		sender.sendMessage(prefix+Localization.INFO_TAX);
		
		if(sender instanceof Player){
			sender.sendMessage("");
			int[] ticketNums=plugin.getManager().getTickets((Player)sender);
			if(ticketNums.length>0){
				sender.sendMessage(prefix+Localization.INFO_INTRO);
				for(int i=0;i<ticketNums.length;i++){
					double fraction=jp/plugin.getManager().getNumOfTicketsForTicket(ticketNums[i]-1);
					sender.sendMessage(prefix+Localization.INFO_TICKETS_BOUGHT.replaceFirst("%n", ""+ticketNums[i]).replaceFirst("%n", ""+fraction));
				}
			}
		}
	}
	
	private void stats(CommandSender sender){
		sender.sendMessage(plugin.getManager().getStats());
		sender.sendMessage(prefix+Localization.STATS_INTRO);
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
				plugin.getLoc().reloadCustomConfig();
				plugin.getLoc().loadStrings();
				sender.sendMessage(prefix+"Reloading lottery...");
				plugin.getSave().reloadCustomConfig();
				sender.sendMessage(prefix+"Finished!");
			}else{
				plugin.getManager().save();
				sender.sendMessage(prefix+"Reloading config...");
				plugin.reloadConfig();
				plugin.getLoc().reloadCustomConfig();
				plugin.getLoc().loadStrings();
				sender.sendMessage(prefix+"Finished!");
			}
			plugin.getManager().loadConfigData(flag);
		}else{
			sendNoPerm(sender);	
		}
	}
	
	private void sendNoPerm(CommandSender sender){
		sender.sendMessage(prefix+"You do not have enough permissions to do that!");
	}
}
