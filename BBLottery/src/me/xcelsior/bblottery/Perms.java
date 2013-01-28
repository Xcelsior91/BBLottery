package me.xcelsior.bblottery;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Perms {BUY("bblottery.buy"),
	BUYMULTI("bblottery.buy.multi"),
	DRAW("bblottery.draw"),
	RELOAD("bblottery.reload"),
	ALL("bblottery.*");

String perm;

private Perms(String perm){
	this.perm=perm;	
}

@Override
public String toString(){
	return perm;
}

public static boolean hasPerm(CommandSender sender, Perms perm){ 
	return (BBLottery.permission.has(sender, perm.perm)||BBLottery.permission.has(sender, Perms.ALL.perm)||sender.isOp());
}

public static boolean hasPerm(Player pl, Perms perm){
	return (BBLottery.permission.has(pl, perm.perm)||BBLottery.permission.has(pl, Perms.ALL.perm)||pl.isOp());
}

}
