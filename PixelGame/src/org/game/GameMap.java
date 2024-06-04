package org.game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.base.Renderer;
import org.constants.Constants;

public class GameMap {
	public static ArrayList<ArrayList<String>> currentMap = DictionaryData.gameMap;
	private static Dictionary<String, Location> saves = new Hashtable<String, Location>();

	public static void render(Graphics g) {
		g.setColor(Constants.MAP_BACKGROUND_COLOR);
		g.fillRect(0, 0, Renderer.gameWidth, Renderer.gameHeight);
		// Draw map tiles
		for (int i = 0; i < GameMap.currentMap.size(); i++) {
			ArrayList<String> row = GameMap.currentMap.get(i);
			for (int j = 0; j < row.size(); j++) {
				var locationName = row.get(j);
				Map<String, Object> tileData = DictionaryData.locationData.get(locationName);
				if (tileData == null) { System.out.println("location type doesn't exist " + locationName); }
				String hexColor = (String) tileData.get("color");
				g.setColor(new Color(Integer.valueOf(hexColor.substring(0, 2), 16),
						Integer.valueOf(hexColor.substring(2, 4), 16), Integer.valueOf(hexColor.substring(4, 6), 16)));
				int size = Constants.MAP_TILE_SIZE;

				var x = j;
				var y = i;
				org.game.UserInterface.drawButton(g, j * size, i * size, size, size, locationName,
						(float) Constants.MAP_TILE_SIZE / locationName.length(), () -> {
							if (PlayerData.gameMapX == x * size && PlayerData.gameMapY == y * size) {
								PlayerData.enterLocation(x * size, y * size);
							} else {
								PlayerData.goToLocation(x * size, y * size);
							}
						}, new Color(0, 0, 0));
			}
		}

		// Draw player(s)
		g.setColor(Constants.MAP_PLAYER_COLOR);
		g.fillOval((int) (PlayerData.gameMapX - Constants.MAP_PLAYER_SIZE / 2 + Constants.MAP_PLAYER_OFFSET_X),
				(int) (PlayerData.gameMapY - Constants.MAP_PLAYER_SIZE / 2 + Constants.MAP_PLAYER_OFFSET_Y),
				Constants.MAP_PLAYER_SIZE, Constants.MAP_PLAYER_SIZE);
	}

	public static String getLocationName(int x, int y) {
		return currentMap.get((int)Math.floor(y / Constants.MAP_TILE_SIZE)).get((int)Math.floor(x / Constants.MAP_TILE_SIZE));
	}
	public static void saveLocation(int x, int y, Location loc) {
		saves.put(x + "," + "y" + "," + loc.type, loc);
	}
	public static Location loadLocation(int x, int y, String type) {
		return saves.get(x + "," + "y" + "," + type);
	}
}
