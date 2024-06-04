package org.constants;

import java.awt.Color;
import java.awt.geom.AffineTransform;

public class Constants {
	
	public static final int GAME_WIDTH = 1080;
	public static final int GAME_HEIGHT = 250;
	
	// GameMap

	public static final Color MAP_BACKGROUND_COLOR = new Color(129, 156, 235);
	public static final int MAP_TILE_SIZE = 100;
	public static final Color MAP_PLAYER_COLOR = new Color(255, 0, 0);
	public static final int MAP_PLAYER_SIZE = 20;
	public static final int MAP_PLAYER_OFFSET_X = 20;
	public static final int MAP_PLAYER_OFFSET_Y = 20;
	
	public static final float PLAYER_MAP_MOVE_SPEED = 100.0f;
	
	// Inventory
	public static final Color INVENTORY_FRAME_COLOR = new Color(48, 52, 71);
	public static final int INVENTORY_FRAME_MARGIN = 10;
	public static final Color INVENTORY_TEXT_COLOR = new Color(240, 240, 240);
	public static final int INVENTORY_SLOT_SIZE = 40;
	public static final int INVENTORY_SLOT_MARGIN = 10;
	public static final Color INVENTORY_SLOT_COLOR = new Color(23, 23, 23);
	public static final Color INVENTORY_SLOT_SELECTED_COLOR = new Color(60, 23, 23);
	public static final Color INVENTORY_SLOT_HIGHLIGHT = new Color(255, 255, 255, 100);
	public static final int INVENTORY_SLOT_TEXT_SIZE = 12;
	public static final float INVENTORY_ITEM_LIST_TEXT_SIZE = 18.0f;
	public static final int INVENTORY_ACTION_BUTTON_HEIGHT = 25;
	public static final Color INVENTORY_ACTION_BUTTON_COLOR = new Color(73, 113, 214);
	public static final Color INVENTORY_ACTION_BUTTON_HIGHLIGHT = new Color(0, 0, 0);
	public static final int INVENTORY_ACTION_BUTTON_MARGIN = 10;
	public static final int INVENTORY_SCROLL_BUTTON_MARGIN = 0;
	public static final int INVENTORY_SCROLL_BUTTON_WIDTH = 5;
	public static final Color INVENTORY_SCROLL_BUTTON_COLOR = new Color(20, 20, 20);
	public static final Color INVENTORY_SCROLL_BUTTON_HIGHLIGHT = new Color(255, 255, 255);
}
