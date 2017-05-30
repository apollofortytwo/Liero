package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import box2dLight.PointLight;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class Player {
	int x, y;
	BodyDef bd;
	Body body;
	PolygonShape shape = new PolygonShape();
	Fixture fixture;
	World world;

	Player(int x, int y, World world) {
		this.x = x;
		this.y = y;
		this.world = world;
		shape.setAsBox(64 / 32, 64 / 32);

		bd = new BodyDef();
		bd.position.set(x, y);
		bd.type = BodyType.DynamicBody;

		body = world.createBody(bd);
		fixture = body.createFixture(shape, 10);
		fixture.setUserData("Player");
		fixture.setFriction(10.0f);
		body.setGravityScale(10f);

		body.setFixedRotation(true);
	}

	public void render(ShapeRenderer sr) {

	}

	float cooldown = 0;

	public void update() {
		if (Gdx.input.isKeyPressed(Keys.W)) {
			Vector2 vel = body.getLinearVelocity();
			vel.y = -50;
			body.setLinearVelocity(vel);
			body.applyLinearImpulse(body.getLinearVelocity(), body.getPosition(), true);

		}

		if (Gdx.input.isKeyPressed(Keys.A)) {
			Vector2 vel = body.getLinearVelocity();
			vel.x = -50;
			body.setLinearVelocity(vel);
			body.applyLinearImpulse(body.getLinearVelocity(), body.getPosition(), true);

		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			Vector2 vel = body.getLinearVelocity();
			vel.x = 50;
			body.setLinearVelocity(vel);
			body.applyLinearImpulse(body.getLinearVelocity(), body.getPosition(), true);
		}

		BodyDef bd = new BodyDef();
		Vector3 pos = Main.cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		pos.x /= 32;
		pos.y /= 32;

		bd.position.set(x / 32, y / 32);

		double angle = Math.atan(((y / 32) - pos.y) / ((x / 32) - pos.x));
		int i = 7;
		if (pos.x < (x / 32)) {
			i = -i;
		}

		float dy = (float) (i * Math.sin(angle));
		float dx = (float) (i * Math.cos(angle));

		bd.position.set(dx + (x / 32), dy + (y / 32));
		bd.bullet = true;

		Main.sr.line(new Vector2(x / 32, y / 32), bd.position);

		cooldown -= Gdx.graphics.getDeltaTime();

		if (Gdx.input.isTouched()) {
			if (cooldown <= 0) {
				cooldown = 0.1f;
				Bullet bullet = new Bullet(bd, world);
				Box2dMap.bulletList.add(bullet);

				Vector2 blastDir = bd.position.cpy().sub(new Vector2(x / 32, y / 32));
				float distance = blastDir.len();
				if (distance == 0)
					return;

				float invDistance = 1f / distance;
				float blastPower = 320000f;
				float impulseMag = Math.min(blastPower * invDistance, blastPower * 0.5f);
				bullet.body.setLinearVelocity(blastDir.nor().scl(impulseMag));
				bullet.body.applyLinearImpulse(body.getLinearVelocity(), new Vector2(x / 32, y / 32), true);

				Main.sendBullet(bullet, x, y);

			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.L)) {

		}

		x = (int) body.getPosition().x * 32;
		y = (int) body.getPosition().y * 32;
	}
}
