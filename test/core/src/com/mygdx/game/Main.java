package com.mygdx.game;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;

import box2dLight.RayHandler;

public class Main extends ApplicationAdapter {

	public static RayHandler rh;

	public static ShapeRenderer sr;
	public static OrthographicCamera cam;
	CameraController cc;
	Player player;
	static World world;
	Box2DDebugRenderer b2dr;
	MapManager mm;
	static int seg = 8;
	public static ClientProgram cp;

	public static final int SCALE = 32;

	@Override
	public void create() {

		Box2D.init();
		Bullet.init();

		try {
			cp = new ClientProgram(this);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Input invalid");
		}

		world = new World(new Vector2(0, 9.81f), true);
		b2dr = new Box2DDebugRenderer();
		rh = new RayHandler(world);

		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);

		float w = (float) Gdx.graphics.getWidth();
		float h = (float) Gdx.graphics.getHeight();
		cam = new OrthographicCamera(w, (w + h) / 2);

		cam.setToOrtho(true);
		cam.combined.scl(1000);
		cam.zoom = 7;

		cc = new CameraController(cam);
		Gdx.input.setInputProcessor(cc);

		Texture img = new Texture("map.jpg");
		if (!img.getTextureData().isPrepared()) {
			img.getTextureData().prepare();
		}

		mm = new MapManager(img.getTextureData().consumePixmap(), cam, world);

		player = new Player(64, 3000 / 32, world);

		cp.sendMyCharacter(player);

		mm.looper(seg);
		Explosion.world = world;
	}

	public void update() {
		System.out.println(player.y);
		cam.position.set(new Vector2(player.x, player.y), 0);
		try {
			Gdx.graphics.setTitle(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mm.update();
		player.update();
		cp.updateCharacterPosition(player.body.getPosition());

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.update();
		cc.update();
		Matrix4 matrix = cam.combined.scl(SCALE);
		sr.setProjectionMatrix(matrix);
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.rect((player.body.getPosition().x - 2), player.body.getPosition().y - 2, 4, 4);

		for(com.mygdx.game.PhysicsCharacter character: cp.characterList){
			Network.Character character1 = character.character;
			sr.setColor(Color.WHITE);
			sr.rect((character1.x - 2), character1.y - 2, 4, 4);
		}

		rh.setCombinedMatrix(matrix);
		rh.updateAndRender();
		rh.setCulling(false);

		update();

		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);

		for (Iterator<Body> iter = bodies.iterator(); iter.hasNext();) {
			if (iter.hasNext()) {
				Body body = iter.next();
				if (body != null) {
					if (!world.isLocked()) {
						if (((UserData) body.getUserData()).toDelete) {
							world.destroyBody(body);
							body.setUserData(null);
							body = null;
						}
					}
				}
			}

		}

		world.step(1 / 60f, 6, 6);
		b2dr.render(world, matrix);

		sr.end();
		mm.render(sr);

	}

	public static void sendBullet(com.mygdx.game.Bullet bt, int x, int y) {

		Network.Bullet bullet = new Network.Bullet();
		bullet.xPos = (int) bt.body.getPosition().x;
		bullet.yPos = (int) bt.body.getPosition().y;

		bullet.x = x;
		bullet.y = y;

		bullet.xforce = (int) bt.body.getLinearVelocity().x;
		bullet.yforce = (int) bt.body.getLinearVelocity().y;

		cp.sendBullet(bullet);

	}

	public static void recieveBullet(Network.Bullet bullet) {
		BodyDef bd = new BodyDef();
		bd.position.x = bullet.xPos;
		bd.position.y = bullet.yPos;

		if (!world.isLocked()) {
			com.mygdx.game.Bullet bt = new com.mygdx.game.Bullet(bd, world);

			bt.body.applyLinearImpulse(new Vector2(bullet.xforce, bullet.yforce),
					new Vector2(bullet.x / 32, bullet.y / 32), true);

			Box2dMap.bulletList.add(bt);

		}

	}

	public void dispose() {
		world.dispose();
		b2dr.dispose();
		Gdx.app.exit();
	}

}