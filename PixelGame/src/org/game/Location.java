package org.game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

import org.misc.Util;
import org.object.GameObject;
import org.object.Sprite;

public class Location {

	public ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	public String type = "";
	public GameObject player;
	public final int size;
	public final Color color;

	public Location(String type) {
		this.type = type;
		Map<String, Object> data = DictionaryData.locationData.get(type);

		// Set constants
		this.size = (int) data.get("size");
		String hexColor = (String) data.get("color");
		this.color = new Color(Integer.valueOf(hexColor.substring(0, 2), 16),
				Integer.valueOf(hexColor.substring(2, 4), 16), Integer.valueOf(hexColor.substring(4, 6), 16));

		// Add sprites
		Map<String, Integer> spriteData = (Map<String, Integer>) data.get("content");
		for (Map.Entry<String, Integer> entry : spriteData.entrySet()) {
			var spriteType = entry.getKey();
			var spriteCount = entry.getValue();
			for (int i = 0; i < spriteCount; i++) {
				var sprite = new GameObject(spriteType);
				int size = (int) data.get("size");
				sprite.posX = (float) (size * Math.random());
				sprite.posY = (float) (size * Math.random());
				Spawn.spawnSprite(sprite, this);
			}
		}
	}

	public void update(Graphics g, float deltaTime) {
		g.setColor(color);
		g.fillRect(0, 0, size, size);
		for (Sprite sprite : sprites) {
			sprite.update(deltaTime);
			sprite.render(g);
		}
		ArrayList<Sprite> originalSprites = (ArrayList<Sprite>) sprites.clone();
		for (Sprite sprite : originalSprites) {
			if (sprite.shouldBeDestroyed) {
				sprite.destroy();
				return;
			}
			if (sprite.getClass() == GameObject.class) {
				var gameObject = (GameObject)sprite;
				if (gameObject.hitpoints <= 0) {
					gameObject.destroy();
				}
			}
		}
	}

	public Sprite getClosestSprite(float x, float y, Function<Sprite, Boolean> predicate) {
		var closestDistance = Float.MAX_VALUE;
		Sprite closestSprite = null;
		for (Sprite sprite : sprites) {
			if (predicate.apply(sprite)) {
				float distance = (float) Util.distance(sprite.posX, sprite.posY, x, y);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestSprite = sprite;
				}
			}
		}
		return closestSprite;
	}

	public Sprite getClosestSprite(float x, float y, float maxDistance, Function<Sprite, Boolean> predicate) {
		var closestDistance = Float.MAX_VALUE;
		Sprite closestSprite = null;
		for (Sprite sprite : sprites) {
			if (predicate.apply(sprite)) {
				float distance = (float) Util.distance(sprite.posX, sprite.posY, x, y);
				if (distance < closestDistance && distance <= maxDistance) {
					closestDistance = distance;
					closestSprite = sprite;
				}
			}
		}
		return closestSprite;
	}

}
