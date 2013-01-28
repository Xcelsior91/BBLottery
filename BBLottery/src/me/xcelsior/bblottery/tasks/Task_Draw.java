/*
 * BBLottery - by Xcelsior
 * http://
 *
 * powered by Kickstarter
 */

package me.xcelsior.bblottery.tasks;



import me.xcelsior.bblottery.BBLottery;


public class Task_Draw implements Runnable{
	private BBLottery plugin;

	public Task_Draw(BBLottery plugin){
		this.plugin = plugin;
	}

	@Override
	public void run(){
		plugin.getManager().draw();
	}
}
