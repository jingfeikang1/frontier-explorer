package org.object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.game.DictionaryData;
import org.game.Library;
import org.game.Location;
import org.game.PlayerData;

public class Sprite {
	public String type = "";
	public float posX = 0;
	public float posY = 0;
	public float velX = 0;
	public float velY = 0;
	public float accelX = 0;
	public float accelY = 0;
	public Boolean solid = true;

	public Integer width;
	public Integer height;
	public BufferedImage image;
	public Integer topDecorationWidth;
	public Integer topDecorationHeight;
	public BufferedImage topDecoration;
	public float highlight = 0;

	public Location location;
	public boolean shouldBeDestroyed = false;

	public Sprite(String type) {
		this.type = type;
		try {
			var data = DictionaryData.objectData.get(type);
			if (data == null) {
				System.out.println("Wrong sprite type");
			}
			image = Library.getImage(type);
			width = (Integer) data.get("width");
			height = (Integer) data.get("height");
			var topDecorationName = (String) data.get("topDecoration");
			if (topDecorationName != null) {
				topDecoration = Library.getImage(topDecorationName);
				topDecorationWidth = (Integer) data.get("topDecorationWidth");
				topDecorationHeight = (Integer) data.get("topDecorationHeight");
			}
			if (this instanceof GameObject) {
				solid = (Boolean) data.get("solid");
			}
		} catch (Exception e) {
			System.out.println("Failed sprite creation: " + type + "; " + e.getMessage());
		}
	}

	public void update(float deltaTime) {
		// Decrease highlight
		highlight /= 1.01;

		// Do collisions
		try {
			checkCollisions(deltaTime);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Friction
		velX /= Math.pow(30, deltaTime);
		velY /= Math.pow(30, deltaTime);
		
		// Update position and velocity
		velX += accelX * deltaTime * 0.5f;
		velY += accelY * deltaTime * 0.5f;
		posX += velX * deltaTime;
		posY += velY * deltaTime;
		velX += accelX * deltaTime * 0.5f;
		velY += accelY * deltaTime * 0.5f;
		accelX = 0;
		accelY = 0;
	}

	public void highlight() {
		highlight = 0.7f;
	}

	private void checkCollisions(float deltaTime) {
		Rectangle playerXRect = new Rectangle((int) (posX + (velX + accelX * deltaTime * 0.5f) * deltaTime - width / 2), (int) (posY - height / 2),
				(int) width, (int) height);
		Rectangle playerYRect = new Rectangle((int) (posX - width / 2), (int) (posY + (velY + accelY * deltaTime * 0.5f) * deltaTime - height / 2),
				(int) width, (int) height);
		var lastIndex = location.sprites.size() - 1;
		for (int i = lastIndex; i >= 0; i--) {
			Sprite sprite = location.sprites.get(i);
			if (sprite == this || !sprite.solid) {
				continue;
			}

			// TODO: clean code
			Rectangle otherRect = new Rectangle((int) (sprite.posX - sprite.width / 2),
					(int) (sprite.posY - sprite.height / 2), (int) sprite.width, (int) sprite.height);
			var collided = false;
			if (playerXRect.intersects(otherRect)) {
				velX = 0;
				accelX = 0;
				collided = true;
			}
			if (playerYRect.intersects(otherRect)) {
				velY = 0;
				accelY = 0;
				collided = true;
			}

			// Pick up items
			if (DictionaryData.objectList.get("Mob").contains(type) && collided && sprite.getClass() == Item.class) {
				PlayerData.inventory.add((Item) sprite);
				sprite.shouldBeDestroyed = true;
			}
		}
	}

	public void render(Graphics g) {
		int realX = (int) posX - (int) width / 2;
		int realY = (int) posY - (int) height / 2;

		// Base image
		g.drawImage(image, realX, realY, (int) width, (int) height, null);

		// Top decoration
		if (topDecoration != null) {
			int realDecX = (int) posX - (int) topDecorationWidth / 2;
			int realDecY = (int) posY - (int) topDecorationHeight / 2;
			g.drawImage(topDecoration, realDecX, realDecY, (int) topDecorationWidth, (int) topDecorationHeight, null);
		}

		// Highlight
		g.setColor(new Color(255, 255, 200, (int) (highlight * 255.0f)));
		g.fillRect(realX, realY, width, height);
	}

	public void destroy() {
		location.sprites.remove(this);
	}
}
