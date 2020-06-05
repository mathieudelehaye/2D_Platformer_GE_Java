import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Filter;

public class MyGdxGame extends Game {
	//Virtual Screen size and Box2D Scale(Pixels Per Meter)
	public static final int V_WIDTH = 327;	// tilemap height (see below) * aspect ratio (= Gdx.graphics.getWidth() / Gdx.graphics.getHeight()) 
	public static final int V_HEIGHT = 170;	// tilemap height to display in pixel 
	public static final float PPM = 37;

	//Box2D Collision Bits
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short OBJECT_BIT = 4;
	public static final short ENEMY_BIT = 8;
	public static final short ENEMY_HEAD_BIT = 16;
	public static final short ITEM_BIT = 32;

	public SpriteBatch batch;

	/* WARNING Using AssetManager in a static way can cause issues, especially on Android.
	Instead you may want to pass around Assetmanager to those the classes that need it.
	We will use it in the static context to save time for now. */
	public static AssetManager manager;
	
	@Override public void create () {
		batch = new SpriteBatch();

		manager = new AssetManager();
		manager.load("assets/gameover.mp3", Music.class);	// non gradle version
		manager.load("assets/jump.mp3", Sound.class);	// non gradle version
		manager.load("assets/cashin.mp3", Sound.class);	// non gradle version
		manager.load("assets/death.mp3", Sound.class);	// non gradle version
		manager.finishLoading();

		setScreen(new PlayScreen(this));
	}
	// non gradle version 
	@Override public void dispose() {
		super.dispose();
		manager.dispose();
		batch.dispose();
	}

	@Override public void render () {
		super.render();
	}
}
