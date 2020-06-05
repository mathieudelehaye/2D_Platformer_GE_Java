import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*; 
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {
    @Override public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA(); 
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case MyGdxGame.ENEMY_HEAD_BIT | MyGdxGame.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == MyGdxGame.ENEMY_HEAD_BIT) {
                    ((Enemy)fixA.getUserData()).hitOnHead((Player) fixB.getUserData());
                    // System.out.println("WorldContactListener beginContact: contact detected bewteen fixB of type player and fixA of type Enemy head");
                } else {
                    ((Enemy)fixB.getUserData()).hitOnHead((Player) fixA.getUserData());
                    // System.out.println("WorldContactListener beginContact: contact detected bewteen fixA of type player and fixB of type Enemy head");
                }
                break;
            case MyGdxGame.ENEMY_BIT | MyGdxGame.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MyGdxGame.ENEMY_BIT) {
                    float beginXA = fixA.getBody().getPosition().x - ((Enemy)fixA.getUserData()).getWidth() * ((Enemy)fixA.getUserData()).scale / 2;
                    float endXA = beginXA + ((Enemy)fixA.getUserData()).getWidth() * ((Enemy)fixA.getUserData()).scale;
                    float beginXB = fixB.getBody().getPosition().x-((Rectangle)fixB.getUserData()).getWidth() / MyGdxGame.PPM /2; 
                    float endXB = beginXB+((Rectangle)fixB.getUserData()).getWidth() / MyGdxGame.PPM;  
                    if(beginXA>endXB||endXA<beginXB) {  // detect if enemy is touching object to the left or right 
                        // System.out.println("WorldContactListener beginContact: contact detected bewteen fixB of type object and fixA of type Enemy");
                        // System.out.println("WorldContactListener beginContact: fixA = "+fixA+", beginXA = "+beginXA+", endXA = "+endXA+", beginXB = "+beginXB+", endXB = "+endXB);
                        ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                    }
                } else {
                    float beginXA = fixA.getBody().getPosition().x-((Rectangle)fixA.getUserData()).getWidth() / MyGdxGame.PPM /2; 
                    float endXA = beginXA+((Rectangle)fixA.getUserData()).getWidth() / MyGdxGame.PPM;  
                    float beginXB = fixB.getBody().getPosition().x - ((Enemy)fixB.getUserData()).getWidth() * ((Enemy)fixB.getUserData()).scale / 2;
                    float endXB = beginXB + ((Enemy)fixB.getUserData()).getWidth() * ((Enemy)fixB.getUserData()).scale;
                    if(beginXA>endXB||endXA<beginXB) {  // detect if enemy is touching object to the left or right 
                        // System.out.println("WorldContactListener beginContact: contact detected bewteen fixA of type object and fixB of type Enemy");
                        // System.out.println("WorldContactListener beginContact: fixB = "+fixB+", beginXA = "+beginXA+", endXA = "+endXA+", beginXB = "+beginXB+", endXB = "+endXB);
                        ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                    }
                }
                break;
            case MyGdxGame.PLAYER_BIT | MyGdxGame.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == MyGdxGame.PLAYER_BIT) {
                    ((Player) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                    // System.out.println("WorldContactListener beginContact: contact detected bewteen fixB of type player and fixA of type Enemy");
                } else {
                    ((Player) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                    // System.out.println("WorldContactListener beginContact: contact detected bewteen fixA of type player and fixB of type Enemy");
                }
                break;
            case MyGdxGame.ENEMY_BIT | MyGdxGame.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).hitByEnemy((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).hitByEnemy((Enemy)fixA.getUserData());
                break;
            case MyGdxGame.ITEM_BIT | MyGdxGame.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == MyGdxGame.ITEM_BIT) {
                    // System.out.println("WorldContactListener beginContact: contact detected bewteen fixB of type player and fixA of type Item");
                    ((Item)fixA.getUserData()).use((Player) fixB.getUserData());
                } else {
                    // System.out.println("WorldContactListener beginContact: contact detected bewteen fixA of type player and fixB of type Item");
                    ((Item)fixB.getUserData()).use((Player) fixA.getUserData());
                }
                break;
        }
    }

    @Override public void endContact(Contact contact) {
    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
