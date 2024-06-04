package org.base;

public class Camera {

	static public double translationX = 0;
	static public double translationY = 0;
	static public double scale = 1.25;

	public static void setPosition(double x, double y) {
		translationX = x;
		translationY = y;
	}
	public static double getMouseX() {
		return (MouseInput.mouseX) / scale + translationX;
	}
	public static double getMouseY() {
		return (MouseInput.mouseY) / scale + translationY;
	}
}
