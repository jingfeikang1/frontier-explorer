package org.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.base.MouseInput;
import org.base.Renderer;
import org.constants.Constants;
import org.misc.Util;
import org.object.Item;

import com.text.TextAlignment;
import com.text.TextRenderer;

public class UserInterface {
	public static boolean inventoryShown = false;
	private static String inventoryDisplayType = "Transformation";
	private static float inventoryScrollRatio = 0; // Between 0 and 1
	private static boolean inventoryScrolling = false;
	public static Item inventorySelectedItem;
	private static String tooltip = "";

	public static void render(Graphics g) {
		var INVENTORY_ITEM_TRANSFORMATION_LIST_TITLE_HEIGHT = 30;
		if (inventoryShown) {
			drawInventory(g, Renderer.gameWidth / 4, Renderer.gameHeight / 4, Renderer.gameWidth / 2,
					Renderer.gameHeight / 2);
			g.setColor(Constants.INVENTORY_FRAME_COLOR);
			g.fillRect(Renderer.gameWidth / 4 * 3 + Constants.INVENTORY_FRAME_MARGIN,
					Renderer.gameHeight / 4 - INVENTORY_ITEM_TRANSFORMATION_LIST_TITLE_HEIGHT
							- Constants.INVENTORY_FRAME_MARGIN,
					Renderer.gameWidth / 4 - Constants.INVENTORY_FRAME_MARGIN * 2,
					INVENTORY_ITEM_TRANSFORMATION_LIST_TITLE_HEIGHT);
			g.setFont(Library.font1.deriveFont(INVENTORY_ITEM_TRANSFORMATION_LIST_TITLE_HEIGHT));
			g.setColor(new Color(255, 255, 255));
			TextRenderer.drawString(g, getInventoryObjectType(),
					new Rectangle(Renderer.gameWidth / 4 * 3 + Constants.INVENTORY_FRAME_MARGIN * 2,
							Renderer.gameHeight / 4 - INVENTORY_ITEM_TRANSFORMATION_LIST_TITLE_HEIGHT
									- Constants.INVENTORY_FRAME_MARGIN,
							Renderer.gameWidth / 4 - Constants.INVENTORY_FRAME_MARGIN * 2,
							INVENTORY_ITEM_TRANSFORMATION_LIST_TITLE_HEIGHT),
					TextAlignment.MIDDLE_LEFT);

			// Other UI
			drawItemTransformationList(g, Renderer.gameWidth / 4 * 3 + Constants.INVENTORY_FRAME_MARGIN,
					Renderer.gameHeight / 4, Renderer.gameWidth / 4 - Constants.INVENTORY_FRAME_MARGIN * 2,
					Renderer.gameHeight / 2);
		}

		// Draw tooltip
		if (tooltip != "") {
			drawTooltip(g);
			tooltip = "";
		}
	}

	private static String getInventoryObjectType() {
		if (PlayerData.interactionObject == null) {
			return "Craft";
		} else {
			return PlayerData.interactionObject.type;
		}
	}

	public static void openInventory(String type) {
		inventoryShown = true;
		inventoryDisplayType = type;
	}

	public static void closeInventory() {
		inventoryShown = false;
	}

	private static void drawTooltip(Graphics g) {
		var mouseX = MouseInput.getMouseX();
		var mouseY = MouseInput.getMouseY();
		var UI_BUTTON_TIP_COLOR = new Color(255, 248, 184);
		var UI_BUTTON_TIP_TEXT_SIZE = 15.0f;
		var UI_BUTTON_TIP_OFFSET_X = 20;
		g.setColor(UI_BUTTON_TIP_COLOR);
		g.setFont(Library.font1.deriveFont(UI_BUTTON_TIP_TEXT_SIZE));
		var width = g.getFontMetrics().stringWidth(tooltip);
		var lineNum = tooltip.length() - tooltip.replace("\n", "").length() + 1;
		g.fillRect((int) mouseX + UI_BUTTON_TIP_OFFSET_X, (int) mouseY, width, (int) UI_BUTTON_TIP_TEXT_SIZE * lineNum);
		g.setColor(Color.black);
		TextRenderer.drawString(g, tooltip, new Rectangle((int) mouseX + UI_BUTTON_TIP_OFFSET_X, (int) mouseY,
				width + 1, (int) UI_BUTTON_TIP_TEXT_SIZE * lineNum));
	}

	private static void drawInventory(Graphics g, int x, int y, int w, int h) {
		// Draw inventory frame
		var tableHeight = h - Constants.INVENTORY_ACTION_BUTTON_MARGIN - Constants.INVENTORY_ACTION_BUTTON_HEIGHT;
		g.setColor(Constants.INVENTORY_FRAME_COLOR);
		g.fillRect(x, y, w, tableHeight);

		var halfSlotSize = Constants.INVENTORY_SLOT_SIZE / 2;

		var slotsPerRow = Math.floorDiv(
				w - Constants.INVENTORY_SCROLL_BUTTON_MARGIN - Constants.INVENTORY_SCROLL_BUTTON_WIDTH
						- Constants.INVENTORY_SLOT_MARGIN,
				Constants.INVENTORY_SLOT_SIZE + Constants.INVENTORY_SLOT_MARGIN);

		// Get slot data
		Map<String, Integer> slotData = new HashMap<>();
		for (Item item : PlayerData.inventory) {
			slotData.compute(item.type, (key, count) -> (count == null) ? 1 : count + 1);
		}
		String[] slots = slotData.keySet().toArray(new String[0]);

		// Old code, where slots was a 2D array
		// Util.to2DArray(Util.removeDuplicates(PlayerData.inventory, (Item item) -> {
		// return item.type;
		// }), slotsPerRow);

		int rowNum = Math.floorDiv(slots.length, slotsPerRow);
		int scrollableHeight = Math.max(0, (rowNum) * (Constants.INVENTORY_SLOT_SIZE + Constants.INVENTORY_SLOT_MARGIN)
				+ Constants.INVENTORY_SLOT_MARGIN - tableHeight);

		// Draw slots
		int offY = Constants.INVENTORY_SLOT_MARGIN - (int) (scrollableHeight * inventoryScrollRatio);
		int offX = Constants.INVENTORY_SLOT_MARGIN;
		for (int i = 0; i < slots.length; i++) {
			String type = slots[i];
			if (offY >= 0 && offY + Constants.INVENTORY_SLOT_SIZE <= tableHeight) {
				var count = slotData.get(type);
				if (inventorySelectedItem != null && inventorySelectedItem.type == type) {
					g.setColor(Constants.INVENTORY_SLOT_SELECTED_COLOR);
				} else {
					g.setColor(Constants.INVENTORY_SLOT_COLOR);
				}
				drawInventoryButton(g, x + offX, y + offY, Constants.INVENTORY_SLOT_SIZE, Constants.INVENTORY_SLOT_SIZE,
						type, Constants.INVENTORY_SLOT_HIGHLIGHT);
				g.drawImage(Library.getImage(type), x + offX + halfSlotSize / 2, y + offY + halfSlotSize / 2,
						halfSlotSize, halfSlotSize, null);

				// Draw text
				g.setColor(Constants.INVENTORY_TEXT_COLOR);
				g.setFont(Library.font1.deriveFont(Constants.INVENTORY_SLOT_TEXT_SIZE));
				TextRenderer.drawString(g, String.valueOf(count),
						new Rectangle(x + offX + Constants.INVENTORY_SLOT_MARGIN / 2,
								y + offY + Constants.INVENTORY_SLOT_MARGIN / 2,
								Constants.INVENTORY_SLOT_SIZE - Constants.INVENTORY_SLOT_MARGIN,
								Constants.INVENTORY_SLOT_SIZE - Constants.INVENTORY_SLOT_MARGIN),
						TextAlignment.TOP_RIGHT);
			}
			offX += Constants.INVENTORY_SLOT_SIZE + Constants.INVENTORY_SLOT_MARGIN;
			if (Math.floorMod(i + 1, slotsPerRow) == 0) {
				offX = Constants.INVENTORY_SLOT_MARGIN;
				offY += Constants.INVENTORY_SLOT_SIZE + Constants.INVENTORY_SLOT_MARGIN;
			}
		}

		// Draw scroller
		var scrollButtonHeight = tableHeight - scrollableHeight;
		g.setColor(Constants.INVENTORY_SCROLL_BUTTON_COLOR);

		drawButton(g, x + w - Constants.INVENTORY_SCROLL_BUTTON_MARGIN - Constants.INVENTORY_SCROLL_BUTTON_WIDTH,
				y + (int) (inventoryScrollRatio * (scrollableHeight)), Constants.INVENTORY_SCROLL_BUTTON_WIDTH,
				scrollButtonHeight, "", 0.0f, () -> {
					inventoryScrolling = true;
				}, Constants.INVENTORY_SCROLL_BUTTON_HIGHLIGHT);

		if (inventoryScrolling) {
			var mouseYDiff = MouseInput.mouseY - MouseInput.pastMouseY;
			inventoryScrollRatio = (float) Math.max(0,
					Math.min(1, inventoryScrollRatio + (mouseYDiff) / (double) (scrollableHeight)));
		}

		if (MouseInput.pressed == false) {
			inventoryScrolling = false;
		}

		// Draw action buttons
		var xOff = 0;
		if (inventorySelectedItem != null) {
			var actions = new ArrayList<String>();
			actions.add("Delete");
			actions.addAll(DictionaryData.<ArrayList<String>>getObjectData(inventorySelectedItem.type, "actions",
					new ArrayList<String>()));
			for (String action : actions) {
				g.setColor(Constants.INVENTORY_ACTION_BUTTON_COLOR);
				drawButton(g, x + xOff, y + tableHeight + Constants.INVENTORY_ACTION_BUTTON_MARGIN,
						(w + Constants.INVENTORY_ACTION_BUTTON_MARGIN) / actions.size()
								- Constants.INVENTORY_ACTION_BUTTON_MARGIN,
						Constants.INVENTORY_ACTION_BUTTON_HEIGHT, action,
						Constants.INVENTORY_ACTION_BUTTON_HEIGHT / 3 * 2, () -> {
							PlayerData.useItem(inventorySelectedItem, action);
						}, Constants.INVENTORY_ACTION_BUTTON_HIGHLIGHT);
				xOff += (w + Constants.INVENTORY_ACTION_BUTTON_MARGIN) / actions.size();
			}
		}
	}

	private static void drawItemTransformationList(Graphics g, int x, int y, int w, int h) {
		// Draw list frame
		var tableHeight = h - Constants.INVENTORY_ACTION_BUTTON_MARGIN - Constants.INVENTORY_ACTION_BUTTON_HEIGHT;
		g.setColor(Constants.INVENTORY_FRAME_COLOR);
		g.fillRect(x, y, w, tableHeight);

		String type;
		ItemTransformationTracker itemTransformationTracker;
		if (PlayerData.interactionObject == null) {
			type = "Craft";
			itemTransformationTracker = PlayerData.itemTransformationTracker;
		} else {
			type = PlayerData.interactionObject.type;
			itemTransformationTracker = PlayerData.interactionObject.itemTransformationTracker;
		}

		var itemTransformations = DictionaryData.itemTransformation.get(type);
		if (type == null) {
			System.out.println("Incorrect Item Transformation type " + type);
		}

		var INVENTORY_ITEM_LIST_HEIGHT = Constants.INVENTORY_SLOT_SIZE / 2;
		var offY = 0;
		for (Map<String, Object> itemTransformation : itemTransformations) {

			// Draw button
			g.setColor(Constants.INVENTORY_SLOT_COLOR);
			drawItemTransformationButton(g, x + Constants.INVENTORY_SLOT_MARGIN * 2 + INVENTORY_ITEM_LIST_HEIGHT,
					y + offY + Constants.INVENTORY_SLOT_MARGIN,
					w - Constants.INVENTORY_SLOT_MARGIN * 3 - INVENTORY_ITEM_LIST_HEIGHT, INVENTORY_ITEM_LIST_HEIGHT,
					itemTransformation, itemTransformationTracker, Constants.INVENTORY_SLOT_HIGHLIGHT);

			// Draw highlight
			g.setColor(Constants.INVENTORY_SLOT_HIGHLIGHT);
			g.fillRect(x + Constants.INVENTORY_SLOT_MARGIN * 2 + INVENTORY_ITEM_LIST_HEIGHT,
					y + offY + Constants.INVENTORY_SLOT_MARGIN,
					(int) ((w - Constants.INVENTORY_SLOT_MARGIN * 3 - INVENTORY_ITEM_LIST_HEIGHT)
							* itemTransformationTracker.timeLeft / itemTransformationTracker.duration),
					INVENTORY_ITEM_LIST_HEIGHT);

			// Draw image
			String title = (String) itemTransformation.get("title");
			g.drawImage(Library.getImage(title), x + Constants.INVENTORY_SLOT_MARGIN,
					y + offY + Constants.INVENTORY_SLOT_MARGIN, INVENTORY_ITEM_LIST_HEIGHT, INVENTORY_ITEM_LIST_HEIGHT,
					null);
			offY += INVENTORY_ITEM_LIST_HEIGHT + Constants.INVENTORY_SLOT_MARGIN;
		}
	}

	public static void drawInventoryButton(Graphics g, int x, int y, int w, int h, String type, Color highlightColor) {
		var mouseX = MouseInput.getMouseX();
		var mouseY = MouseInput.getMouseY();
		if (mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h) {
			UserInterface.tooltip = type;

			var color = g.getColor();
			g.setColor(new Color((color.getRed() + highlightColor.getRed()) / 2,
					(color.getGreen() + highlightColor.getGreen()) / 2,
					(color.getBlue() + highlightColor.getBlue()) / 2));

			if (MouseInput.pressed == true) {
				inventorySelectedItem = PlayerData.inventory.stream().filter(item -> item.type == type).findFirst()
						.get();
			}
		}

		g.fillRect(x, y, w, h);
	}

	public static void drawItemTransformationButton(Graphics g, int x, int y, int w, int h,
			Map<String, Object> itemTransformation, ItemTransformationTracker tracker, Color highlightColor) {
		var mouseX = MouseInput.getMouseX();
		var mouseY = MouseInput.getMouseY();
		if (mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h) {
			var color = g.getColor();
			g.setColor(new Color((color.getRed() + highlightColor.getRed()) / 2,
					(color.getGreen() + highlightColor.getGreen()) / 2,
					(color.getBlue() + highlightColor.getBlue()) / 2));

			if (MouseInput.pressed == true) {
				var recipe = (Map<String, Integer>) itemTransformation.get("recipe");
				var product = (Map<String, Integer>) itemTransformation.get("product");
				var duration = (int) itemTransformation.get("duration");
				PlayerData.transformItem(tracker, recipe, product, duration);
			}
		}

		g.fillRect(x, y, w, h);

		// Draw text
		String title = (String) itemTransformation.get("title");
		g.setFont(Library.font1.deriveFont(Constants.INVENTORY_ITEM_LIST_TEXT_SIZE));
		g.setColor(new Color(255, 255, 255));
		TextRenderer.drawString(g, title, new Rectangle(x, y, w, h), TextAlignment.MIDDLE);
	}

	public static void drawButton(Graphics g, int x, int y, int w, int h, String text, float textSize,
			Runnable function, Color highlightColor) {
		var mouseX = MouseInput.getMouseX();
		var mouseY = MouseInput.getMouseY();
		if (mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h) {
			var color = g.getColor();
			g.setColor(new Color((color.getRed() + highlightColor.getRed()) / 2,
					(color.getGreen() + highlightColor.getGreen()) / 2,
					(color.getBlue() + highlightColor.getBlue()) / 2));

			if (MouseInput.pressed == true) {
				function.run();
			}
		}

		g.fillRect(x, y, w, h);

		g.setFont(Library.font1.deriveFont(textSize));
		g.setColor(new Color(255, 255, 255));
		TextRenderer.drawString(g, text, new Rectangle(x, y, w, h), TextAlignment.MIDDLE);
	}

	public static void drawButton(Graphics g, int x, int y, int w, int h, String text, float textSize,
			Runnable function, Color highlightColor, String tooltip) {
		var mouseX = MouseInput.getMouseX();
		var mouseY = MouseInput.getMouseY();
		var buttonColor = g.getColor();
		if (mouseX > x && mouseY > y && mouseX < x + w && mouseY < y + h) {
			UserInterface.tooltip = tooltip;
			buttonColor = new Color((buttonColor.getRed() + highlightColor.getRed()) / 2,
					(buttonColor.getGreen() + highlightColor.getGreen()) / 2,
					(buttonColor.getBlue() + highlightColor.getBlue()) / 2);

			if (MouseInput.pressed == true) {
				function.run();
			}
		}

		g.setColor(buttonColor);
		g.fillRect(x, y, w, h);

		g.setFont(Library.font1.deriveFont(textSize));
		g.setColor(new Color(255, 255, 255));
		TextRenderer.drawString(g, text, new Rectangle(x, y, w, h), TextAlignment.MIDDLE);
	}

}
