package org.game;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;

import org.base.Camera;
import org.base.KeyInput;
import org.base.MouseInput;
import org.constants.Constants;
import org.misc.Util;
import org.object.GameObject;
import org.object.Item;
import org.object.Sprite;

public class PlayerData {
	public static int health = 100;
	public static ArrayList<Item> inventory = new ArrayList<Item>();
	public static Item equippedItem;

	public static Location currentLocation;
	public static String scene;

	public static float gameMapX = 0;
	public static float gameMapY = 0;
	public static float gameMapDestinationX = 0;
	public static float gameMapDestinationY = 0;
	public static GameObject interactionObject;
	public static ItemTransformationTracker itemTransformationTracker = new ItemTransformationTracker();

	public static Boolean isMoving() {
		return gameMapX != gameMapDestinationX || gameMapY != gameMapDestinationY;
	}

	public static void init() {
		inventory.add(new Item("TreeLeaves"));
		inventory.add(new Item("Berry"));
		inventory.add(new Item("Stone"));
		inventory.add(new Item("Book1"));
		inventory.add(new Item("Stone"));
	}

	public static void updateInMap(float deltaTime) {
		if (isMoving()) {
			var angle = Math.atan2(gameMapDestinationY - gameMapY, gameMapDestinationX - gameMapX);
			var xCathetus = Math.abs(gameMapDestinationX - gameMapX);
			var yCathetus = Math.abs(gameMapDestinationY - gameMapY);
			gameMapX += Math.min(xCathetus, Math.cos(angle) * Constants.PLAYER_MAP_MOVE_SPEED * deltaTime);
			gameMapY += Math.min(yCathetus, Math.sin(angle) * Constants.PLAYER_MAP_MOVE_SPEED * deltaTime);
		}
	}

	public static void updateInLocation(float deltaTime) {
	}

	public static void update(float deltaTime) {
		if (KeyInput.keyWasTyped(KeyEvent.VK_Q) && !KeyInput.getDebounce(KeyEvent.VK_Q)) {
			if (UserInterface.inventoryShown) {
				UserInterface.closeInventory();
			} else {
				PlayerData.interactionObject = null;
				UserInterface.openInventory("Transformation");
			}
			KeyInput.debounce(KeyEvent.VK_Q);
		}
		itemTransformationTracker.update(deltaTime);
		if (scene == "Map") {
			updateInMap(deltaTime);
		} else if (scene == "Location") {
			updateInLocation(deltaTime);
		}
	}

	public static void goToLocation(int x, int y) {
		gameMapDestinationX = x;
		gameMapDestinationY = y;
	}

	public static void enterLocation(int x, int y) {
		String locationName = GameMap.getLocationName(x, y);
		Location loc = GameMap.loadLocation(x, y, locationName);
		if (loc == null) {
			loc = new Location(locationName);

			// Add player
			Map<String, Object> data = DictionaryData.locationData.get(locationName);
			var player = new GameObject("Player");
			int size = (int) data.get("size");
			player.posX = (float) (size * Math.random());
			player.posY = (float) (size * Math.random());
			loc.player = player;
			Spawn.spawnSprite(player, loc);
		}
		currentLocation = loc;
		scene = "Location";
	}

	public static void useItem(Item item, String action) {
		var type = item.type;
		switch (action) {
		case "Equip":
			equipItem(item);
			break;
		case "Delete":
			removeFromInventory(item);
			break;
		}
	}

	private static void equipItem(Item item) {
		equippedItem = item;
		if (PlayerData.scene == "Location") {

		}
	}

	private static void unequipItem() {
		equippedItem = null;
	}

	public static void addToInventory(Item item) {
		inventory.add(item);
	}

	public static void addToInventory(String type) {
		inventory.add(new Item(type));
	}

	public static void removeFromInventory(Item item) {
		if (UserInterface.inventorySelectedItem == item) {
			UserInterface.inventorySelectedItem = null;
		}
		if (!inventory.remove(item)) {
			System.out.println("item " + item.type + " not in inventory");
		}
	}

	public static void removeFromInventory(String type) {
		for (Item item : inventory) {
			if (item.type.equals(type)) {
				removeFromInventory(item);
				return;
			}
		}
		System.out.println("item type " + type + " not in inventory");
	}

	public static void transformItem(ItemTransformationTracker itemTransformationTracker, Map<String, Integer> recipe,
			Map<String, Integer> product, int duration) {
		if (itemTransformationTracker.timeLeft > 0) {
			// System.out.println("already transforming");
			return;
		}
		for (Map.Entry<String, Integer> entry : recipe.entrySet()) {
			String itemType = entry.getKey();
			Integer requiredCount = entry.getValue();
			int count = Util.count(inventory, (Item obj) -> {
				return obj.type.equals(itemType);
			});
			if (count < requiredCount) {
				System.out.println("not enough res " + itemType + " " + itemType);
				return; // not enough resources
			}
		}

		// Remove required items
		for (Map.Entry<String, Integer> entry : recipe.entrySet()) {
			String itemType = entry.getKey();
			Integer requiredCount = entry.getValue();
			for (int i = 0; i < requiredCount; i++) {
				PlayerData.removeFromInventory(itemType);
			}
		}

		// Start item transformation
		itemTransformationTracker.duration = duration;
		itemTransformationTracker.timeLeft = duration;
		itemTransformationTracker.product = product;
	}
}
