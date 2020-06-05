import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape; 

public class Goal extends Item {

    public Goal(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setPosition(x, y);
        setBounds(getX(), getY(), 35 / MyGdxGame.PPM, 35 / MyGdxGame.PPM);
        scale = 1/2f; 
        setScale(scale);
        toDestroy = false;
        destroyed = false;
        defineItem();
        setRegion(screen.getAtlas().findRegion("goal"), 0, 0, 35, 35);
    }

    @Override public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        float halfWidth = 35 / MyGdxGame.PPM / 2 * scale; float halfHeight = 35 / MyGdxGame.PPM / 2 * scale; 
        shape.setAsBox(halfWidth, halfHeight);  
        
        fdef.filter.categoryBits = MyGdxGame.ITEM_BIT;
        fdef.filter.maskBits = MyGdxGame.PLAYER_BIT |
            MyGdxGame.OBJECT_BIT |
            MyGdxGame.GROUND_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
        shape.dispose(); 
    }

    @Override public void use(Player player) {
        destroy();
        MyGdxGame.manager.get("assets/cashin.mp3", Sound.class).play();	// non gradle version
        // MyGdxGame.manager.get("cashin.mp3", Sound.class).play();
        player.reachedGoal(); 
    }

    @Override public void update(float dt) {
        super.update(dt); 
        setPosition(body.getPosition().x - getWidth() / 2 * scale, body.getPosition().y - getHeight() / 2 * scale);
    }
}
