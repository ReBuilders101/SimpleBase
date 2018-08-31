package lb.simplebase;

import java.awt.Color;
import java.awt.Dimension;

import lb.simplebase.core.Framework;
import lb.simplebase.core.FrameworkStateException;
import lb.simplebase.core.InvalidSceneException;
import lb.simplebase.scene.ColorDemoScene;

/**
 * @version 1.0
 * @author LB
 * The main class used to test this program
 */
public class MainTestClass {

	public static void main(String[] args) throws FrameworkStateException, InvalidSceneException {
		Framework.getFramework().init(30, 30, "Test", new Dimension(600, 400), true, true);
		Framework.getFramework().registerScene(new ColorDemoScene("cds", Color.YELLOW));
		Framework.getFramework().start(true, "cds");
	}

}
