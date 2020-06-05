import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Item extends SpriteObject {
    protected boolean toDestroy;
    protected boolean destroyed;

    public Item(PlayScreen screen, float x, float y) {
        super(screen); 
        setPosition(x, y);
        setBounds(getX(), getY(), 35 / MyGdxGame.PPM, 35 / MyGdxGame.PPM);
        scale = 1/2f; 
        setScale(scale);
        toDestroy = false;
        destroyed = false;
    }

    public abstract void defineItem();
    
    public abstract void use(Player player);

    @Override public void update(float dt) {
        if(toDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }
    }

    public void draw(Batch batch) {
        if(!destroyed) {
            super.draw(batch);
        }
    }

    public void destroy() {
        toDestroy = true;
    }
}
