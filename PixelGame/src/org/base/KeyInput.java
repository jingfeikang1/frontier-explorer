package org.base;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {

	private static boolean[] currentKeys = new boolean[128];
	private static boolean[] typedKeys = new boolean[128];
	public static boolean[] keyDebounce = new boolean[128]; // prevents interaction by key input
	private static boolean[] futureKeyDebounce = new boolean[128];

	public static void debounce(int keyCode) {
		futureKeyDebounce[keyCode] = true;
	}

	public static boolean getDebounce(int keyCode) {
		return keyDebounce[keyCode];
	}


	public static void updateDebounce() {
		keyDebounce = futureKeyDebounce.clone();
	}

	public static boolean getKey(int keyCode) {
		return currentKeys[keyCode];
	}

	public static boolean keyWasTyped(int keyCode) {
		return typedKeys[keyCode];
	}

	public void keyPressed(KeyEvent e) {
		var keyCode = e.getKeyCode();
		if (getKey(keyCode) == true) {
			typedKeys[keyCode] = false;
		} else {
			typedKeys[keyCode] = true;
		}
		currentKeys[keyCode] = true;
	}

	public void keyReleased(KeyEvent e) {
		var keyCode = e.getKeyCode();
		currentKeys[keyCode] = false;
		typedKeys[keyCode] = false;

		futureKeyDebounce[keyCode] = false;
	}

	public void keyTyped(KeyEvent e) {

	}

}
