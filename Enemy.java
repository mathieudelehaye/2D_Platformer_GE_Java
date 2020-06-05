import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.*; 
import com.badlogic.gdx.physics.box2d.*; 
import com.badlogic.gdx.utils.*; 

public class Enemy extends SpriteObject {
    private Vector2 velocity;
    private Animation<TextureRegion> walkAnimation;
    private boolean setToDestroy;
    private boolean destroyed;

    Enemy(PlayScreen screen, float x, float y) {
        super(screen); 
        
        scale = 1/2f; 
        setPosition(x, y);
        
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        // float halfWidth = 40 / MyGdxGame.PPM / 2 * scale; float halfHeight = 22 / MyGdxGame.PPM / 2 * scale; 
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-20, 3).scl(1 / MyGdxGame.PPM * scale);
        vertice[1] = new Vector2(20, 3).scl(1 / MyGdxGame.PPM * scale);
        vertice[2] = new Vector2(-20, -11).scl(1 / MyGdxGame.PPM * scale);
        vertice[3] = new Vector2(20, -11).scl(1 / MyGdxGame.PPM * scale);
        shape.set(vertice);
        // shape.setAsBox(halfWidth, halfHeight);  
        fdef.filter.categoryBits = MyGdxGame.ENEMY_BIT;
        fdef.filter.maskBits = MyGdxGame.GROUND_BIT |
            MyGdxGame.ENEMY_BIT |
            MyGdxGame.OBJECT_BIT |
            MyGdxGame.PLAYER_BIT;
        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
        shape.dispose(); 

        // Create the Head here:
        PolygonShape head = new PolygonShape();
        vertice[0] = new Vector2(-17, 3).scl(1 / MyGdxGame.PPM * scale);
        vertice[1] = new Vector2(13, 3).scl(1 / MyGdxGame.PPM * scale);
        vertice[2] = new Vector2(-17, 11).scl(1 / MyGdxGame.PPM * scale);
        vertice[3] = new Vector2(13, 11).scl(1 / MyGdxGame.PPM * scale);
        head.set(vertice);
        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MyGdxGame.ENEMY_HEAD_BIT;
        body.createFixture(fdef).setUserData(this);

        velocity = new Vector2(-1, -2);
        turnedToRight = false; 
        body.setActive(false);

        walkAnimation = new Animation<TextureRegion>(0.4f, new TextureRegion(screen.getAtlas().findRegion("slime"), 0, 0, 40, 22));
        stateTimer = 0;
        setBounds(0, 0, 40 / MyGdxGame.PPM, 22 / MyGdxGame.PPM);
        setScale(scale);
        setToDestroy = false;
        destroyed = false;
    }

    @Override public void update(float deltaTime) {
    	stateTimer += deltaTime;
        if(setToDestroy && !destroyed) {
            TextureRegion tr = walkAnimation.getKeyFrame(stateTimer, true); 
            // System.out.println("Enemy update 1: turnedToRight = "+turnedToRight+", tr.isFlipX() = "+tr.isFlipX()+", body.getLinearVelocity().x = "+body.getLinearVelocity().x);

            // flip frame upside down taking into account horizontal flip 
            if(!turnedToRight && !tr.isFlipX()) {
	            tr.flip(true, false);
            } else if(turnedToRight && tr.isFlipX()) {
	            tr.flip(true, false);
            }
            tr.flip(false, true); 

            // System.out.println("Enemy update 2: turnedToRight = "+turnedToRight+", tr.isFlipX() = "+tr.isFlipX()+", body.getLinearVelocity().x = "+body.getLinearVelocity().x);
            setRegion(tr);
            world.destroyBody(body);
            destroyed = true;
            stateTimer = 0;
            MyGdxGame.manager.get("assets/death.mp3", Sound.class).play();	// non gradle version
            // MyGdxGame.manager.get("death.mp3", Sound.class).play();
            Hud.addScore(100);
        } else if(!destroyed) {
            body.setLinearVelocity(velocity);
            // System.out.println("Enemy update: body.getPosition().x = "+body.getPosition().x+", body.getPosition().y = "+body.getPosition().y+", getWidth() = "+getWidth()+", getHeight() = "+getHeight());
            // System.out.println("Enemy update: velocity = "+velocity+", body.getLinearVelocity().x = "+body.getLinearVelocity().x);
            setPosition(body.getPosition().x - getWidth() / 2 * scale, body.getPosition().y - getHeight() / 2 * scale);
            TextureRegion tr = walkAnimation.getKeyFrame(stateTimer, true); 
            // System.out.println("Enemy update before: turnedToRight = "+turnedToRight+", tr.isFlipX() = "+tr.isFlipX());
            checkDirectionAndFlip(tr); 
            // System.out.println("Enemy update after: turnedToRight = "+turnedToRight+", tr.isFlipX() = "+tr.isFlipX());
            setRegion(tr);
        }
    }

    public void draw(Batch batch) {
    	if(!destroyed || stateTimer < 1) {
            super.draw(batch);
        }
    }

    public void hitOnHead(Player player) {
    	System.out.println("Enemy hitOnHead: body.getLinearVelocity().x = "+body.getLinearVelocity().x);
        setToDestroy = true;
    }

    public void hitByEnemy(Enemy enemy) {
        reverseVelocity(true, false);
    }

    public void reverseVelocity(boolean x, boolean y) {
        if(x) {
        	velocity.x = -velocity.x;
        }
        if(y) {
            velocity.y = -velocity.y;
        }
    }
}
