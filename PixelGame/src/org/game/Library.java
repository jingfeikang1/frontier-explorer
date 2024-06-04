package org.game;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.base.Renderer;

public class Library {
	public static Font font1 = new Font("Courier new", Font.PLAIN, 12);
	public static Dictionary<String, BufferedImage> images = new Hashtable<>();

	public static void init() {
		File folder = new File("src/resources/images");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				try {
					var name = listOfFiles[i].getName();
					images.put(name.replaceFirst("[.][^.]+$", ""), Renderer.loadImage("/resources/images/" + name));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static BufferedImage getImage(String name) {
		var image = images.get(name);
		if (image != null) {
			return image;
		}
		
		image = images.get(DictionaryData.getObjectData(name, "image", "default"));
		return image;
	}
}
