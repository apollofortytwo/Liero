package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraController implements InputProcessor {


	OrthographicCamera cam;
	float speed = 50f;
	float scroll;
	InputHandler inputHandler;
	CameraController(OrthographicCamera cam) {
		this.cam = cam;
		inputHandler = new InputHandler();
	}

	public void update() {
		handleInputs();
	}
	
	/**
	 * moves the camera around with the WASD keys
	 * zooms in and out with the scroll wheel
	 */
	public void handleInputs(){
		if (InputHandler.checkKey("W")) {
			cam.translate(0, -speed);
		}
		if (InputHandler.checkKey("D")) {
			cam.translate(speed, 0);
		}
		if (InputHandler.checkKey("S")) {
			cam.translate(0, speed);
		}
		if (InputHandler.checkKey("A")) {
			cam.translate(-speed, 0);
		}
		if(InputHandler.checkKey("Z")){
			cam.zoom -= 0.1f;
					
			//scroll = 0;
		}
		if(InputHandler.checkKey("X")){
			cam.zoom += 0.1;
					
			//scroll = 0;
		}

	}

	@Override
	public boolean keyDown(int keycode) {
		String input = Input.Keys.toString(keycode);
		InputHandler.setKey(input, true);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		String input = Input.Keys.toString(keycode);
		InputHandler.setKey(input, false);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		scroll = amount;
		return false;
	}

}
