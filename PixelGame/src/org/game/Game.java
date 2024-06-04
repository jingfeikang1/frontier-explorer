package org.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.base.Camera;
import org.base.Renderer;
import org.object.GameObject;

public class Game {

	public static void main(String[] args) {
		Renderer.init();
		Library.init();
		DictionaryData.init();
		PlayerData.init();

		PlayerData.scene = "Map";
		/*
		 * currentLocation = new Location(); var b = new Mob("Player"); b.posX = 20;
		 * b.posY = 20; b.width = 20; b.height = 20; currentLocation.sprites.add(b);
		 */
	}

	public static void update(Graphics2D g, float deltaTime) {
		// g.scale(2, 2);
		if (PlayerData.scene == "Map") {
			GameMap.render(g);
		} else if (PlayerData.scene == "Location") {
			if (PlayerData.currentLocation == null) {
				return;
			}
			
			// Transform
			GameObject plr = PlayerData.currentLocation.player;
            Camera.setPosition(plr.posX - Renderer.canvasWidth / 2 / Camera.scale, plr.posY - Renderer.canvasHeight / 2 / Camera.scale);
            AffineTransform oldTransform = g.getTransform();
            AffineTransform newTransform = new AffineTransform();
            newTransform.scale(Camera.scale, Camera.scale);
            newTransform.translate(-Camera.translationX, -Camera.translationY);
			g.setTransform(newTransform);
            
			PlayerData.currentLocation.update(g, deltaTime);
			
			g.setColor(Color.blue);
			g.fillRect((int)Camera.getMouseX(), (int)Camera.getMouseY(), 2, 2);
			
			// Reset Transform
			g.setTransform(oldTransform);
		}

		PlayerData.update(deltaTime);

		UserInterface.render(g);
	}

	public static void quit() {
		System.exit(0);
	}

}
