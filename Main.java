import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class Main {
	// final private static boolean rebuildAtlas = false;
	// final private static boolean drawDebugOutline = false;

	public static void main (String[] arg) {
		/*if (rebuildAtlas) {
		 	Settings settings = new Settings();
		 	settings.maxWidth = 2048;
		 	settings.maxHeight = 256;
		 	settings.debug = drawDebugOutline;
		 	TexturePacker.process(settings, "assets-raw", "assets", "my_atlas");
		}*/

		System.out.println("Here we start");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// config.fullscreen = false; 
		config.title = "My LibGDX application";
		config.width = 1200;
		config.height = 624;
		new LwjglApplication(new MyGdxGame(), config);	// run game if game is over 
	}
}


