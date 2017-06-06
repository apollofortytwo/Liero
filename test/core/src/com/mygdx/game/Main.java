package com.mygdx.game;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.bullet.Bullet;

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

		cc = new CameraController(cam);
		Gdx.input.setInputProcessor(cc);

		Texture img = new Texture("map.jpg");
		if (!img.getTextureData().isPrepared()) {
			img.getTextureData().prepare();
		}

		mm = new MapManager(img.getTextureData().consumePixmap(), cam, world);

		player = new Player(64, 0, world);

		cp.sendMyCharacter(player);

		mm.looper(seg);
		Explosion.world = world;
	}

	public void update() {
		cam.position.set(new Vector2(player.x, player.y), 0);
		Gdx.graphics.setTitle(String.valueOf(Gdx.graphics.getFramesPerSecond()));
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

		for (Network.Character character : cp.characterList) {
			sr.setColor(Color.WHITE);
			sr.rect((character.x - 2), character.y - 2, 4, 4);
		}

		
		for(Network.Bullet bullet: cp.bulletList){
			sr.setColor(Color.GREEN);
			sr.rect(bullet.x, bullet.y, 0.5f, 0.25f);
		}
		rh.setCombinedMatrix(matrix);
		rh.updateAndRender();
		rh.setCulling(false);

		player.render(sr);

		update();

		world.step(1 / 60f, 12, 12);

		b2dr.render(world, matrix);

		sr.end();

	}

	public static void sendBullet(com.mygdx.game.Bullet bt, int x, int y) {
		Network.Bullet bullet = new Network.Bullet();
		bullet.x = x;
		bullet.y = y;
		bullet.id = cp.id;
		bullet.index = bt.index;
		cp.sendBullet(bullet);
	}
	
	

	public static void recieveBullet(Network.Bullet bullet) {


	}

}