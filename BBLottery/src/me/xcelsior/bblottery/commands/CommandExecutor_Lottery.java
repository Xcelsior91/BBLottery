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
	String prefix = ChatColor.GOLD + "[Lottery]"+ChatColor.GREEN;
	String[] usage;
	Localization loc;

	public CommandExecutor_Lottery(BBLottery plugin){
		this.plugin = plugin;
		loc=plugin.getLoc();
		String[] u ={ChatColor.GREEN+"---------"+prefix+"---------",
				ChatColor.GREEN+loc.replace(loc.HELP[0]),
				ChatColor.GREEN+loc.replace(loc.HELP[1]),
				ChatColor.YELLOW+"/lottery           "+ChatColor.GREEN+loc.replace(loc.HELP[2]),
				ChatColor.YELLOW+"/lottery buy [tip] "+ChatColor.GREEN+loc.replace(loc.HELP[3]),
				ChatColor.YELLOW+"/lottery give <player> [tip]"+ChatColor.GREEN+loc.replace(loc.HELP[4]),
				ChatColor.YELLOW+"/lottery info      "+ChatColor.GREEN+loc.replace(loc.HELP[5]),
				ChatColor.YELLOW+"/lottery stats      "+ChatColor.GREEN+loc.replace(loc.HELP[6]),
				ChatColor.GREEN+loc.replace(loc.HELP[7]),
				ChatColor.YELLOW+"/lottery reload [-f]   "+ChatColor.GREEN+loc.replace(loc.HELP[8]),
				ChatColor.YELLOW+"/lottery draw      "+ChatColor.GREEN+loc.replace(loc.HELP[9])};
		usage=u;
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
						sender.sendMessage(loc.replace(loc.ERROR_NO_PLAYER));
						return true;
					}else{
						buyTicket(sender, args);
					}
					
				}else if(args[0].equalsIgnoreCase("give")){
					giveTicket(sender, args);					
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
				if(plugin.getManager().getRange()!=-1&&args[1].matches("\\d+")){
					int ticketNum=Integer.parseInt(args[1]);
					if(ticketNum>0&&ticketNum<=plugin.getManager().getRange()){
						if(plugin.getManager().buyTicket((Player)sender, ticketNum)){
							BBLottery.economy.withdrawPlayer(sender.getName(), plugin.getManager().getPrice());
						}
					}else{
						sender.sendMessage(prefix+loc.replace(loc.ERROR_RANGE));
					}
				}
			
				
			else{
				if(plugin.getManager().buyTicket((Player)sender)){
					BBLottery.economy.withdrawPlayer(sender.getName(), plugin.getManager().getPrice());
				}
			}
			}else{
				sender.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.ERROR_MONEY));
			}
		}else{
			sendNoPerm(sender);
		}
	}
	
	private void giveTicket(CommandSender sender, String[] args){
		if(Perms.hasPerm(sender, Perms.BUYOTHER)){
			if(plugin.getServer().getPlayerExact(args[1])!=null){
				if(sender instanceof Player){
					if(BBLottery.economy.getBalance(sender.getName())>=plugin.getManager().getPrice()){
						if(args.length==3&&args[2].matches("\\d+")){
							int ticketNum=Integer.parseInt(args[2]);
							if(plugin.getManager().proxyBuyTicket(sender.getName(),plugin.getServer().getPlayerExact(args[1]), ticketNum)){
								BBLottery.economy.withdrawPlayer(sender.getName(), plugin.getManager().getPrice());
							}
						}else{
							if(plugin.getManager().proxyBuyTicket(sender.getName(),plugin.getServer().getPlayerExact(args[1]))){
								BBLottery.economy.withdrawPlayer(sender.getName(), plugin.getManager().getPrice());
							}
						}
					}
					else{
						sender.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.ERROR_MONEY));
						return;
					}
				}else{
					if(args.length==3&&args[2].matches("\\d+")){
						int ticketNum=Integer.parseInt(args[2]);
						if(!plugin.getManager().proxyBuyTicket("Console",plugin.getServer().getPlayerExact(args[1]), ticketNum)){
							sender.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.ERROR_OTHER_MAXTICKETS));
						}
					}else{
						
						if(!plugin.getManager().proxyBuyTicket("Console",plugin.getServer().getPlayerExact(args[1]))){
							sender.sendMessage(prefix+ChatColor.GREEN+loc.replace(loc.ERROR_OTHER_MAXTICKETS));
						}
					}
				}
			}else{
				sender.sendMessage(prefix+loc.replace(loc.ERROR_INVALIDPLAYER).replaceFirst("%pn", args[1]));
			}
		}else{
			sendNoPerm(sender);
		}
	}
	
	private void info(CommandSender sender){
		double jp=plugin.getManager().getJackpot();
		
		sender.sendMessage(prefix+loc.replace(loc.INFO_JACKPOT));
		sender.sendMessage(prefix+loc.replace(loc.INFO_PRICE));
		sender.sendMessage(prefix+loc.replace(loc.INFO_TICKETS_ON_DRAW));
		sender.sendMessage(prefix+loc.replace(loc.INFO_DRAWS_SINCE_WIN).replaceFirst("%[n,m]", ""+plugin.getManager().getDrawsSinceLastWin()));
		sender.sendMessage(prefix+loc.replace(loc.INFO_TAX));
		
		if(sender instanceof Player){
			sender.sendMessage("");
			int[] ticketNums=plugin.getManager().getTickets((Player)sender);
			if(ticketNums.length>0){
				sender.sendMessage(prefix+loc.replace(loc.INFO_INTRO));
				for(int i=0;i<ticketNums.length;i++){
					double fraction=jp/plugin.getManager().getNumOfTicketsForTicket(ticketNums[i]-1);
					sender.sendMessage(prefix+loc.replace(loc.INFO_TICKETS_BOUGHT).replaceFirst("%n", ""+ticketNums[i]).replaceFirst("%n", ""+fraction));
				}
			}
		}
	}
	
	private void stats(CommandSender sender){
		sender.sendMessage(plugin.getManager().getStats());
		sender.sendMessage(prefix+loc.replace(loc.STATS_INTRO));
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
