import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion; 
import com.badlogic.gdx.physics.box2d.Body; 
import com.badlogic.gdx.physics.box2d.World; 

abstract public class SpriteObject extends Sprite {
	public World world; 
	public Body body; 
    public PlayScreen screen;
    public float scale;  // scale between sprite real and displayed size 
    protected boolean turnedToRight;
    protected float stateTimer;

	SpriteObject (PlayScreen screen) {
		this.screen = screen;
		this.world = screen.getWorld(); 
        turnedToRight = true;
	}

	protected void checkDirectionAndFlip(TextureRegion region) {
        if((body.getLinearVelocity().x < 0 || !turnedToRight) && !region.isFlipX()) {   //if sprite is running left and the texture isnt facing left... flip it.
            region.flip(true, false);
            turnedToRight = false;
        } else if((body.getLinearVelocity().x > 0 || turnedToRight) && region.isFlipX()) {  //if sprite is running right and the texture isnt facing right... flip it.
            region.flip(true, false);
            turnedToRight = true;
        }
    }

	abstract public void update(float deltaTime); 
}
