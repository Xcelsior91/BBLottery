/*
 * BBLottery - by Xcelsior
 * http://
 *
 * powered by Kickstarter
 */

package me.xcelsior.bblottery.tasks;



import java.util.TimerTask;

import me.xcelsior.bblottery.BBLottery;


public class Task_Draw extends TimerTask{
	private BBLottery plugin;

	public Task_Draw(BBLottery plugin){
		this.plugin = plugin;
	}

	@Override
	public void run(){
		plugin.getManager().draw();
	}
}
