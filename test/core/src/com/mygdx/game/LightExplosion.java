package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import box2dLight.PointLight;

public class LightExplosion {
	float lifeSpan;
	boolean alive = false;
	float x, y;
	PointLight light;
	float delta = 0;

	LightExplosion(float x2, float y2, float radius) {
		this.x = x2;
		this.y = y2;
		light = new PointLight(Main.rh, 400, null, 0, x2, y2);
		light.setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 0);
		light.setSoft(true);
		lifeSpan = 1/2f;
		alive = true;
	}

	public void update() {
		Color c = light.getColor();
		delta += Gdx.graphics.getDeltaTime();
		
		
		light.setDistance(parabola(delta, 10*32f, 0.1f, 0.1f));
		
		c.a = parabola(delta, 2f, 0.1f, 0.1f);
		light.setColor(c.r, c.g, c.b, c.a);
		
		if (delta >= lifeSpan && alive) {
			light.remove();
			alive = false;
		}

	}
	
	public float parabola(float x, float max, float duration, float start){
		float e1 = (start - max);
		float e2 = (float) Math.pow(duration/2, 2);
		float e3 = (float) Math.pow(x-(duration/2), 2);
		float end =  (e1/e2)*(e3)+max;
		return end;
	}
	
	
}
