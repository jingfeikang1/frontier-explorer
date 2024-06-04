package org.object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;

import org.base.Camera;
import org.base.KeyInput;
import org.base.MouseInput;
import org.game.DictionaryData;
import org.game.ItemTransformationTracker;
import org.game.PlayerData;
import org.game.Spawn;
import org.game.UserInterface;
import org.misc.Util;

public class GameObject extends Sprite {
	public Boolean interactable;
	public float hitpointsHighlight = 0;

	public Integer maxHitpoints;
	public Integer hitpoints;
	protected float runSpeed = 500.0f;
	public Item equippedItem;
	public ItemTransformationTracker itemTransformationTracker = new ItemTransformationTracker();

	public GameObject(String type) {
		super(type);
		try {
			var data = DictionaryData.objectData.get(type);
			if (data == null) {
				System.out.println("Wrong sprite type");
			}
			interactable = (Boolean) data.get("interactable");
			maxHitpoints = DictionaryData.<Integer>getObjectData(type, "hitpoints");
			hitpoints = maxHitpoints;
		} catch (Exception e) {
			System.out.println("Failed GameObject creation: " + type + "; " + e.getMessage());
		}
	}

	public void update(float deltaTime) {
		if (PlayerData.currentLocation.player == this) {
			handlePlayer(deltaTime);
		}
		hitpointsHighlight /= 1.001;
		super.update(deltaTime);
	}

	private void handlePlayer(float deltaTime) {
		if (KeyInput.getKey(KeyEvent.VK_A)) {
			accelX -= runSpeed;
		}
		if (KeyInput.getKey(KeyEvent.VK_D)) {
			accelX += runSpeed;
		}
		if (KeyInput.getKey(KeyEvent.VK_W)) {
			accelY -= runSpeed;
		}
		if (KeyInput.getKey(KeyEvent.VK_S)) {
			accelY += runSpeed;
		}

		if (posX - width / 2 < 0 || posX + width / 2 > PlayerData.currentLocation.size || posY - height / 2 < 0
				|| posY + height / 2 > PlayerData.currentLocation.size) {
			PlayerData.scene = "Map";
		}

		GameObject closestInteractable = (GameObject)PlayerData.currentLocation.getClosestSprite(posX, posY, 50, (Sprite sprite) -> {
			return sprite != this && sprite.getClass() == GameObject.class && ((GameObject) sprite).interactable != null
					&& ((GameObject) sprite).interactable == true;
		});

		// punch
		if (MouseInput.clicked) {
			punch(Math.atan2(Camera.getMouseY() - posY, Camera.getMouseX() - posX));
		}
		
		if (closestInteractable != null) {
			closestInteractable.highlight();
			if (KeyInput.keyWasTyped(KeyEvent.VK_E) && !KeyInput.getDebounce(KeyEvent.VK_E)) {
				PlayerData.interactionObject = closestInteractable;
				UserInterface.openInventory("Transformation");
				KeyInput.debounce(KeyEvent.VK_E);
			}
		}
	}

	public void punch(double angle) {
		var size = location.sprites.size();
		for (var i = size - 1; i >= 0; i--) {
			Sprite sprite = location.sprites.get(i);
			if (sprite.getClass() == GameObject.class && sprite != this) {
				var angleToTarget = Math.atan2(sprite.posY - this.posY, sprite.posX - this.posX);
				var angleDiff = Math.abs(angleToTarget - angle);
				if (Util.distance(posY, posX, sprite.posY, sprite.posX) < 60 && angleDiff < Math.PI / 2) {
					var damage = 1;
					if (equippedItem != null) {
						damage = DictionaryData.getObjectData(equippedItem.type, "damage");
					}
					((GameObject) sprite).takeDamage(damage);
				}
			}
		}
	}

	public void takeDamage(int damage) {
		hitpoints -= damage;
		hitpointsHighlight = 1;
	}

	public void render(Graphics g) {
		super.render(g);

		// Draw hitpoints bar
		if (hitpoints != null && hitpoints < maxHitpoints && hitpoints > 0 && hitpointsHighlight > 0.5) {
			int realX = (int) posX - (int) width / 2;
			int realY = (int) posY - (int) height / 2;
			int OBJECT_HITPOINT_BAR_HEIGHT = 8;
			int OBJECT_HITPOINT_BAR_MARGIN = 2;
			g.setColor(new Color(0, 0, 0));
			g.fillRect(realX, realY - OBJECT_HITPOINT_BAR_HEIGHT - OBJECT_HITPOINT_BAR_MARGIN, (int) width, OBJECT_HITPOINT_BAR_HEIGHT);
			g.setColor(new Color(227, 22, 108));
	        g.fillRect(realX, realY - OBJECT_HITPOINT_BAR_HEIGHT - OBJECT_HITPOINT_BAR_MARGIN, (int) (width * hitpoints / maxHitpoints), OBJECT_HITPOINT_BAR_HEIGHT);
	    }
	}
	
	public void destroy() {
		super.destroy();
		var drops = (Map<String, Object>) DictionaryData.getObjectData(type, "drops");
		if (drops == null) {
			return;
		}
		for (Map.Entry<String, Object> entry : drops.entrySet()) {
			var itemType = entry.getKey();
			var itemCountData = entry.getValue();
			int itemCount;
			if (itemCountData instanceof Integer) {
				itemCount = (Integer)itemCountData;
			} else {
				ArrayList<Integer> range = (ArrayList<Integer>)itemCountData;
				itemCount = Util.random(range.get(0), range.get(1));
			}
			for (int i = 0; i < itemCount; i++) {
				var item = new Item(itemType);
				int itemWidth = DictionaryData.getObjectData(itemType, "width");
				int itemHeight = DictionaryData.getObjectData(itemType, "height");

				item.posX = posX + (float) (Util.random(-width / 2 + itemWidth / 2, width / 2 - itemWidth / 2));
				item.posY = posY + (float) (Util.random(-height / 2 + itemHeight / 2, height / 2 - itemHeight / 2));
				Spawn.spawnSprite(item, location);
			}
		}
	}
}
