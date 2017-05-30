package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class Explosion {
	static ArrayList<LightExplosion> lightEList = new ArrayList<LightExplosion>();

	static World world;

	public static void explode(final int numRays, float blastRadius, final float blastPower, float posX, float posY) {
		final Vector2 center = new Vector2(posX, posY);
		Vector2 rayDir = new Vector2();
		Vector2 rayEnd = new Vector2();

		for (int i = 0; i < numRays; i++) {
			float angle = (i / (float) numRays) * 360 * MathUtils.degreesToRadians;
			rayDir.set(MathUtils.sin(angle), MathUtils.cos(angle));
			rayEnd.set(center.x + blastRadius * rayDir.x, center.y + blastRadius * rayDir.y);

			RayCastCallback callback = new RayCastCallback() {
				@Override
				public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

					applyBlastImpulse(fixture.getBody(), center, point, blastPower / (float) numRays);

					return 0;
				}
			};

			world.rayCast(callback, center, rayEnd);
		}
	}

	public static void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
		Vector2 blastDir = applyPoint.cpy().sub(blastCenter);
		float distance = blastDir.len();
		if (distance == 0)
			return;

		float invDistance = 1f / distance;
		float impulseMag = Math.min(blastPower * invDistance, blastPower * 0.5f);
		body.setLinearVelocity(blastDir.nor().scl(impulseMag));

		body.applyLinearImpulse(body.getLinearVelocity(), applyPoint, true);

	}

	public static void update() {
		for (LightExplosion le : lightEList) {
			le.update();

		}
	}

	public static void drawExplosion(Circle c) {
		lightEList.add(new LightExplosion(c.x, c.y, c.radius));
	}

}
