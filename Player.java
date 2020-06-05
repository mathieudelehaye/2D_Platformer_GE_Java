import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.*; 
import com.badlogic.gdx.physics.box2d.*; 
import com.badlogic.gdx.utils.*; 

public class Player extends SpriteObject {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, DEAD, WON }; 
    public State currentState; 
    public State previousState;
    private TextureRegion playerStand;
    private Animation<TextureRegion> playerRun;
    private boolean playerIsDead;
    private boolean playerHasWon;
	
	Player (PlayScreen screen) {
        super(screen); 
        scale = 1/2f; 

        // Define physical body  
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(50 / MyGdxGame.PPM, 70 / MyGdxGame.PPM);
        // bodyDef.position.set(this.screen.stage.initialPlayerPosition.x / MyGdxGame.PPM, this.screen.stage.initialPlayerPosition.y / MyGdxGame.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        // create rectangular shape to allow interactions (collisions) with other objects
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        float halfWidth = 83 / MyGdxGame.PPM / 2 * scale; float halfHeight = 132 / MyGdxGame.PPM / 2 * scale; 
        shape.setAsBox(halfWidth, halfHeight);  

        fixtureDef.filter.categoryBits = MyGdxGame.PLAYER_BIT;
        fixtureDef.filter.maskBits = MyGdxGame.GROUND_BIT | 
            MyGdxGame.ENEMY_BIT |
            MyGdxGame.OBJECT_BIT |
            MyGdxGame.ENEMY_HEAD_BIT |
            MyGdxGame.ITEM_BIT;

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose(); 

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;

		Array<TextureRegion> frames = new Array<TextureRegion>();
        //get run animation frames and add them to playerRun Animation
        for(int i = 1; i < 7; i++) {
        	frames.add(new TextureRegion(screen.getAtlas().findRegion("sprite"), i * 83, 0, 83, 132));
        }
        playerRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

		playerStand = new TextureRegion(screen.getAtlas().findRegion("sprite"), 0, 0, 83, 132);

        playerIsDead = false; 
        playerHasWon = false; 

		setRegion(playerStand);
		setBounds(0, 0, playerStand.getRegionWidth() / MyGdxGame.PPM, playerStand.getRegionHeight() / MyGdxGame.PPM);
        setScale(scale);
	}

    public State getState() {
    	//Test to Box2D for velocity on the X and Y-Axis
        //if player is going positive in Y-Axis he is jumping... or if he just jumped and is falling remain in jump state
        if(playerIsDead) {
            return State.DEAD;
        } else if (playerHasWon) {
            return State.WON; 
        } else if(body.getLinearVelocity().y > 0 && currentState == State.JUMPING) {
            return State.JUMPING;
        } else if(body.getLinearVelocity().y < 0) {	//if negative in Y-Axis mario is falling
            return State.FALLING;
        } else if(body.getLinearVelocity().x != 0 && previousState != State.JUMPING) {	//if player is positive or negative in the X axis he is running
            return State.RUNNING;
        } else {	//if none of these return then he must be standing
            return State.STANDING;
        }
    }

    public TextureRegion getFrame(float deltaTime) {
        //get player current state. ie. jumping, running, standing...
        currentState = getState();

        // System.out.println("Player getFrame: currentState = "+currentState);

        TextureRegion region;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState) {
            case RUNNING:
                region = playerRun.getKeyFrame(stateTimer, true);
                break;
            case JUMPING:
            case FALLING:
            case DEAD:
            case WON:
            case STANDING:
            default:
                region = playerStand;
                break;
        }

        checkDirectionAndFlip(region); 

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;
    }

	@Override public void update(float deltaTime) {
     	//update our sprite to correspond with the position of our Box2D body
       	setPosition(body.getPosition().x - getWidth() / 2 * scale, body.getPosition().y - getHeight() / 2 * scale);
        //update sprite with the correct frame depending on marios current action
        setRegion(getFrame(deltaTime));
    }

    public float getStateTimer() {
        return stateTimer;
    }

	public void jump() {
		// System.out.println("Player jump: currentState = "+currentState);
	    if (currentState != State.JUMPING && currentState != State.FALLING) {
		    body.applyLinearImpulse(new Vector2(0, 7f), body.getWorldCenter(), true);
	        currentState = State.JUMPING;
            MyGdxGame.manager.get("assets/jump.mp3", Sound.class).play();	// non gradle version
            // MyGdxGame.manager.get("jump.mp3", Sound.class).play();
	    }
    }

    public void hit(Enemy enemy) {
        if (!playerIsDead) {
            playerIsDead = true;
            setBodyFilterToNothing();  
            body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
        }
    }

    public void reachedGoal() {
        playerHasWon = true; 
        setBodyFilterToNothing();  
    }

    private void setBodyFilterToNothing() {
        Filter filter = new Filter();
        filter.maskBits = MyGdxGame.NOTHING_BIT;
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
    }
}
