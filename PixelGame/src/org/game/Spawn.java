package org.game;

import org.object.Sprite;

public class Spawn {
	public static void spawnSprite(Sprite sprite, Location location) {
		location.sprites.add(sprite);
		sprite.shouldBeDestroyed = false;
		sprite.location = location;
	}
}
