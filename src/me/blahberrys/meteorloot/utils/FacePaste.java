package me.blahberrys.meteorloot.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class FacePaste {

	private enum ChatColor {
		ZERO("§0", 0, 0, 0), ONE("§1", 0, 0, 85), TWO("§2", 0, 85, 0), THREE("§3", 0, 85, 85), FOUR("§4", 85, 0, 0), FIVE("§5", 85, 0, 85), SIX("§6", 85, 85, 0), SEVEN("§7", 85, 85, 85), EIGHT("§8", 170, 170, 170), NINE("§9", 170, 170, 255), TEN("§a", 170, 255, 170), ELEVEN("§b", 170, 255, 255), TWELVE("§c", 255, 170, 170), THIRTEEN("§d", 255, 170, 255), FOURTEEN("§e", 255, 255, 170), FIFTEEN("§f", 255, 255, 255);

		public String string;
		public int red;
		public int green;
		public int blue;

		private ChatColor(String str, int r, int g, int b) {
			this.string = str;
			this.red = r;
			this.green = g;
			this.blue = b;
		}
	}

	public static BufferedImage getFaceImage(String player) {
		BufferedImage faceImage = null;
		try {
			faceImage = ImageIO.read(new URL("http://s3.amazonaws.com/MinecraftSkins/" + player + ".png")).getSubimage(8, 8, 8, 8);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return faceImage;
	}

	public static String getFace(String of) {
		BufferedImage faceImage = null;
		String rs = "";

		try {
			faceImage = ImageIO.read(new URL("http://s3.amazonaws.com/MinecraftSkins/" + of + ".png")).getSubimage(8, 8, 8, 8);
		} catch (Exception e) {
			return "\n§4Error: " + e.getMessage();
		}
		for (int asdfy = 0; asdfy < 8; asdfy++) {
			for (int asdfx = 0; asdfx < 8; asdfx++) {
				rs = rs + getClosestChatColor(new Color(faceImage.getRGB(asdfx, asdfy))) + "█";
			}
			rs = rs + "\n";
		}
		return rs;
	}

	public static String getClosestChatColor(Color colorpar) {
		String rs = ChatColor.ZERO.string;
		int bestSoFar = 1000000;
		int tempscore = 0;
		for (ChatColor chco : ChatColor.values()) {
			tempscore = Math.abs(colorpar.getRed() - chco.red) + Math.abs(colorpar.getGreen() - chco.green) + Math.abs(colorpar.getBlue() - chco.blue);
			if (tempscore < bestSoFar) {
				bestSoFar = tempscore;
				rs = chco.string;
			}
		}
		return rs;
	}
}
