import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx; 
import com.badlogic.gdx.Input; 
import com.badlogic.gdx.Screen; 
import com.badlogic.gdx.graphics.GL20; 
import com.badlogic.gdx.graphics.OrthographicCamera; 
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer; 
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2; 
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer; 
import com.badlogic.gdx.physics.box2d.World; 
import com.badlogic.gdx.utils.Array; 
import com.badlogic.gdx.utils.viewport.FitViewport; 
import com.badlogic.gdx.utils.viewport.Viewport; 
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
	final static private boolean debug_draw_box2d_world = false; 
	// boolean gameOver = false;
	private MyGdxGame game;
	private WorldCreator wCreator; 
	private Player player; 
    private TextureAtlas atlas;
	//basic playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

 	public PlayScreen(MyGdxGame game) {
 		System.out.println("PlayScreen: Gdx.graphics.getWidth() = "+Gdx.graphics.getWidth()+", Gdx.graphics.getHeight() = "+Gdx.graphics.getHeight());
 		this.game = game;

		//create cam used to follow mario through cam world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(MyGdxGame.V_WIDTH / MyGdxGame.PPM, MyGdxGame.V_HEIGHT / MyGdxGame.PPM, gamecam);

        //create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        //Load our map and setup our map renderer
        maploader = new TmxMapLoader();
        // map = maploader.load("assets/level.tmx");	// non gradle version 
        map = maploader.load("assets/level.tmx");
 		System.out.println("PlayScreen: MyGdxGame.PPM = "+MyGdxGame.PPM+", (1 / MyGdxGame.PPM) = "+(1 / MyGdxGame.PPM));
        tileMapRenderer = new OrthogonalTiledMapRenderer(map, 1 / MyGdxGame.PPM);
        world = new World(new Vector2(0, -10), true);
        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();

        atlas = new TextureAtlas("assets/my_atlas.atlas");	// non gradle version
        // atlas = new TextureAtlas("my_atlas.atlas");
        
        wCreator = new WorldCreator(this);

        //initially set our gamcam to be centered correctly at the start of of map
 		System.out.println("PlayScreen: gamePort.getWorldWidth() = "+gamePort.getWorldWidth()+", gamePort.getWorldHeight()  = "+gamePort.getWorldHeight());
		gamecam.setToOrtho(false, gamePort.getWorldWidth(), gamePort.getWorldHeight()); 
        gamecam.position.set(gamePort.getWorldWidth() / 2 + gamePort.getWorldWidth() % 2 / MyGdxGame.PPM, gamePort.getWorldHeight() / 2 + gamePort.getWorldHeight() % 2 / MyGdxGame.PPM, 0);

		player = new Player(this); 

		world.setContactListener(new WorldContactListener());

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
        spawnItem(new ItemDef(new Vector2(1000 / MyGdxGame.PPM, 512 / MyGdxGame.PPM), Goal.class));
	}

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if(!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Goal.class) {
                items.add(new Goal(this, idef.position.x, idef.position.y));
            }
        }
    }

	public TextureAtlas getAtlas() {
        return atlas;
    }

 	public TiledMap getMap() {
    	return map;
    }

	public World getWorld() {
    	return world;
    }

	@Override public void render (float deltaTime) {
		// Press escape to quit 
	    if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
	    	Gdx.app.exit(); 
	    }

	    // update variables: separate our update logic from render
        update(deltaTime);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

         //render our game map
        tileMapRenderer.render();

        //renderer our Box2DDebugLines
        if(debug_draw_box2d_world) { b2dr.render(world, gamecam.combined); }

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : wCreator.getEnemies()) {
            enemy.draw(game.batch);
        }
        for (Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        if(victory()) {
            game.setScreen(new VictoryScreen(game));
            dispose();
        }
	}

	private boolean gameOver() {
        if(player.currentState == Player.State.DEAD && player.getStateTimer() > 3) {
            return true;
        }
        return false;
    }

    private boolean victory() {
        if(player.currentState == Player.State.WON) {
        	return true;
        }
        return false;
    }

	 public void handleInput() {
	 	if(player.currentState != Player.State.DEAD) {
        	if (Gdx.input.isKeyJustPressed(Input.Keys.UP)
                || (Gdx.input.justTouched()&&Gdx.input.getY() < Gdx.graphics.getHeight() / 2)) {
	            player.jump();
        	}
	        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) 
                || (Gdx.input.isTouched()&&Gdx.input.getX() > Gdx.graphics.getWidth() / 2))
                && player.body.getLinearVelocity().x <= 2) {
	            player.body.applyLinearImpulse(new Vector2(0.1f, 0), player.body.getWorldCenter(), true);
	        }
	        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT)
                || (Gdx.input.isTouched()&&Gdx.input.getX() < Gdx.graphics.getWidth() / 2))
                && player.body.getLinearVelocity().x >= -2) {
	            player.body.applyLinearImpulse(new Vector2(-0.1f, 0), player.body.getWorldCenter(), true);
	        }
	        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) { 
	        }
	    }
    }

	private void update(float deltaTime) {
        //handle user input first
		handleInput();
        handleSpawningItems();

		//takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);

        player.update(deltaTime);
       	for(Enemy enemy : wCreator.getEnemies()) {
       		enemy.update(deltaTime);
            if(enemy.getX() < player.getX() + 224 / MyGdxGame.PPM) {
                enemy.body.setActive(true);
            }
        }

        for(Item item : items) {
            item.update(deltaTime);
        }

        hud.update(deltaTime);
        if(hud.isTimeUp()) {
        	player.hit(null);	// kill player  
        }

	    //attach our gamecam to our players.x coordinate
        if(player.currentState != Player.State.DEAD) {
            gamecam.position.x = player.body.getPosition().x; gamecam.position.y = player.body.getPosition().y;

            // correct camera position to avoid it leaves tile map  
            if(gamecam.position.x < gamePort.getWorldWidth() / 2 + gamePort.getWorldWidth() % 2 / MyGdxGame.PPM) {
		    	gamecam.position.x = gamePort.getWorldWidth() / 2 + gamePort.getWorldWidth() % 2 / MyGdxGame.PPM; 
		    } 
		    if(gamecam.position.x > wCreator.tileWidthInPixel*wCreator.mapWidthInTile / MyGdxGame.PPM - gamePort.getWorldWidth() / 2) {
		    	gamecam.position.x = wCreator.tileWidthInPixel*wCreator.mapWidthInTile / MyGdxGame.PPM - gamePort.getWorldWidth() / 2; 
		    }
		    if(gamecam.position.y < gamePort.getWorldHeight() / 2 + gamePort.getWorldHeight() % 2 / MyGdxGame.PPM) {
		    	gamecam.position.y = gamePort.getWorldHeight() / 2 + gamePort.getWorldHeight() % 2 / MyGdxGame.PPM; 
		    }
		    if(gamecam.position.y > wCreator.tileHeightInPixel*wCreator.mapHeightInTile /MyGdxGame.PPM  - gamePort.getWorldHeight() / 2) {
		    	gamecam.position.y = wCreator.tileHeightInPixel*wCreator.mapHeightInTile /MyGdxGame.PPM  - gamePort.getWorldHeight() / 2; 
		    }
        }

        //update our gamecam with correct coordinates after changes
        gamecam.update();
        //tell our renderer to draw only what our camera can see in our game world.
        tileMapRenderer.setView(gamecam);
	}

	@Override public void resize(int width, int height) { 
		 //updated our game viewport
        gamePort.update(width,height);
	}
	
	@Override public void dispose () {
		world.dispose(); 
        b2dr.dispose();
        map.dispose(); 
        tileMapRenderer.dispose(); 
        hud.dispose();
	}
	
	@Override public void pause() { }

    @Override public void resume() { }

	@Override public void show() { }

    @Override public void hide() { }
}
