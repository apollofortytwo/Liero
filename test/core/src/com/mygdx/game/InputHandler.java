package com.mygdx.game;

import java.util.HashMap;

public class InputHandler {
	/**
	 * DYNAMIC DATA STRUCTURE EXIST HERE!!!!
	 */
	private static HashMap<String, Boolean> keyPressed = new HashMap<String, Boolean>();
	
	public static HashMap<String, Boolean> getKeyPressed() {
		return keyPressed;
	}

	public static void setKeyPressed(HashMap<String, Boolean> keyPressed) {
		InputHandler.keyPressed = keyPressed;
	}

	public static void setKey(String code, boolean state) {
		keyPressed.put(code, state);
	}

	public static boolean checkKey(String code) {
		if (keyPressed.containsKey(code)) {
			return keyPressed.get(code);
		}
		return false;
	}

}
