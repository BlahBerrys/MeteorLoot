package me.blahberrys.meteorloot.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import me.blahberrys.meteorloot.MeteorLoot;
import me.blahberrys.meteorloot.Settings;
import me.blahberrys.meteorloot.meteor.Meteor;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeHandler {

	public static boolean timerStarted = false;
	public static int timerID = 0;

	public static HashMap<World, Integer> dropTimes = new HashMap<World, Integer>();
	public static HashMap<World, Integer> waitTimes = new HashMap<World, Integer>();

	public static void setNextDropTimes() {
		for (World world : Settings.getInstance().worlds)
			setNextDropTime(world);
	}

	public static void setNextDropTime(World world) {
		Random r = new Random();
		int minTime = Settings.getInstance().minTime;
		int maxTime = Settings.getInstance().maxTime;
		int rTime = (minTime == maxTime ? minTime : maxTime - minTime < 1 ? 1 : r.nextInt(maxTime - minTime) + minTime);

		dropTimes.put(world, rTime);
		if (Settings.getInstance().broadcastNext)
			MeteorLoot.getInstance().broadcastMessage(world, formatMsg(Settings.getInstance().broadcastNextMsg, rTime));
		MeteorLoot.getInstance().getServer().getConsoleSender().sendMessage(Settings.getInstance().title + "[" + world.getName().toUpperCase() + "] " + formatMsg(Settings.getInstance().broadcastNextMsg, rTime));
	}

	public static void startTimeHandler() {
		new BukkitRunnable() {
			public void run() {
				timerID = this.getTaskId();

				reduceDropTimes();
				reduceWaitTimes();
			}
		}.runTaskTimer(MeteorLoot.getInstance(), 1200L, 1200L);
	}

	public static void stopTimeHandler() {
		if (timerStarted)
			Bukkit.getServer().getScheduler().cancelTask(timerID);
	}

	private static void reduceWaitTimes() {
		for (Iterator<World> iterator = waitTimes.keySet().iterator(); iterator.hasNext();) {
			World world = iterator.next();
			if (waitTimes.get(world) > 1) {
				waitTimes.put(world, waitTimes.get(world) - 1);
			} else {
				iterator.remove();
				BlockHandler.repairWorldBlocks(world);
			}
		}
	}

	private static void reduceDropTimes() {
		for (Iterator<World> iterator = dropTimes.keySet().iterator(); iterator.hasNext();) {
			World world = iterator.next();

			if (dropTimes.get(world) > 1) {
				dropTimes.put(world, dropTimes.get(world) - 1);
				if (Arrays.asList(Settings.getInstance().warnIntervals.split(",")).contains(String.valueOf(dropTimes.get(world))))
					MeteorLoot.getInstance().broadcastMessage(world, formatMsg(Settings.getInstance().broadcastNextMsg, dropTimes.get(world)));
			} else {
				Meteor.getInstance().startMeteorCrash(world);
				iterator.remove();
			}
		}
	}

	public static String formatMsg(String msg, int dropTime) {
		List<String> tags = Arrays.asList(new String[] { "(S)", "(s)", "(n)", "(N)" });

		if (msg.contains("minut") || msg.contains("MINUT"))
			for (String tag : tags)
				if (msg.contains(tag))
					if (dropTime == 1)
						msg.replace(tag, "");
					else
						msg.replace(tag, tag.replace("(", "").replace(")", ""));
		return msg.replace("@TIME", String.valueOf(dropTime));
	}
}
