import com.badlogic.gdx.maps.*; 
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*; 
import com.badlogic.gdx.math.*; 
import com.badlogic.gdx.physics.box2d.*; 
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType; 
import com.badlogic.gdx.utils.*; 
import java.util.*; 

public class WorldCreator {
	public int mapWidthInTile; 
    public int mapHeightInTile; 
    public int tileWidthInPixel; 
    public int tileHeightInPixel; 
    public Vector2 initialPlayerPosition; 
    public Vector2 initialGoalPosition;
    private Array<Enemy> enemies;

	WorldCreator (PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        MapProperties mp = map.getProperties();	// Get tilemap properties  
		tileWidthInPixel = (Integer) mp.get("tilewidth"); tileHeightInPixel = (Integer) mp.get("tileheight"); 
		mapWidthInTile = (Integer) mp.get("width"); mapHeightInTile = (Integer) mp.get("height"); 

        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            // System.out.println("Stage: rect.getX() = "+rect.getX()+", rect.getY()  = "+rect.getY());
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MyGdxGame.PPM, (rect.getY() + rect.getHeight() / 2) / MyGdxGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MyGdxGame.PPM, rect.getHeight() / 2 / MyGdxGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MyGdxGame.OBJECT_BIT;
            // System.out.println("Stage: rect = "+rect); 
            body.createFixture(fdef).setUserData(rect);
        }

        //create all enemies
        enemies = new Array<Enemy>();
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            // System.out.println("Stage: 1 layer 3 object detected: rect.getX() = "+rect.getX()+", rect.getY()  = "+rect.getY());
            enemies.add(new Enemy(screen, rect.getX() / MyGdxGame.PPM, rect.getY() / MyGdxGame.PPM));
        }
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }
}
