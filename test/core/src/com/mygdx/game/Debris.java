package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Debris {
	float lifeSpan = 5;
	Body body;
	World world;
	
	Debris(World world, Rectangle rect) {
		this.world = world;
		BodyDef bd = new BodyDef();
	
		bd.position.set(rect.x + rect.getWidth() / 2, rect.y + rect.getHeight() / 2);
		
		bd.type = BodyDef.BodyType.DynamicBody;

		body = world.createBody(bd);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(5 / 32f, 5 / 32f);
		Fixture fix = body.createFixture(shape, 1);

		fix.setUserData("Debris");
	}

	public void shorten(ArrayList<Debris> debris){
		lifeSpan -= Gdx.graphics.getDeltaTime();
		if(lifeSpan <= 0){
			world.destroyBody(body);
		}
		
	}
}
