package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.PointLight;

public class Bullet {
	public static int bulletsShot = 0;
	
	World world;
	int index;
	Body body;
	BodyDef bd;
	Fixture fix;
	PointLight light;

	Bullet(BodyDef bd, World world) {
		Bullet.bulletsShot++;
		index = Bullet.bulletsShot;
		this.world = world;
		this.bd = bd;

		bd.type = BodyDef.BodyType.DynamicBody;
		bd.bullet = true;

		body = world.createBody(bd);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.25f);
		Fixture fix = body.createFixture(shape, 1);
		fix.setUserData("Bullet");
		Filter filter = new Filter();
		filter.groupIndex = -2;
		fix.setFilterData(filter);
		addFlare();
	}

	public void addFlare() {
		light = new PointLight(Main.rh, 30, null, 15, bd.position.x, bd.position.y);
		light.setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
		light.attachToBody(body);
	}
	
	public void removeFlare(){
		light.remove();
	}
}
