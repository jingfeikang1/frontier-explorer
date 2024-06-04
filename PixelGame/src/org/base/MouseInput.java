package org.base;

import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInput implements MouseListener {
	public static Boolean pressed = false;
	public static Boolean clicked = true;
	public static double pastMouseX;
	public static double pastMouseY;
	public static double mouseX;
	public static double mouseY;

	public void mousePressed(MouseEvent e) {
		pressed = true;
	}

	public void mouseReleased(MouseEvent e) {
		pressed = false;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		clicked = true;
	}

	public static void init() {
		pastMouseX = getMouseX();
		pastMouseY = getMouseY();
	}

	public static double getMouseX() {
		return (double) ((MouseInfo.getPointerInfo().getLocation().x - Renderer.canvas.getLocationOnScreen().x)
				* ((double) Renderer.gameWidth / (double) Renderer.canvasWidth));
	}

	public static double getMouseY() {
		return (double) ((MouseInfo.getPointerInfo().getLocation().y - Renderer.canvas.getLocationOnScreen().y)
				* ((double) Renderer.gameHeight / (double) Renderer.canvasHeight));
	}

	protected static void updateMouseData() {
		// update mouse locations
		pastMouseX = mouseX;
		pastMouseY = mouseY;
		mouseX = getMouseX();
		mouseY = getMouseY();
		
		// Update mouse clicked status
		clicked = false;
	}
}
