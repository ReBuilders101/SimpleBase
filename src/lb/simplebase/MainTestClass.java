package lb.simplebase;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lb.simplebase.core.Framework;
import lb.simplebase.core.FrameworkStateException;
import lb.simplebase.core.InvalidSceneException;
import lb.simplebase.core.Utils;
import lb.simplebase.scene.ColorDemoScene;
import lb.simplebase.scene.ImageScene;

/**
 * @version 1.0
 * @author LB
 * The main class used to test this program
 */
public class MainTestClass {

	public static void main(String[] args){
		try {
			Utils.setSystemLookAndFeel();
			Framework.getFramework().init(30, 30, "Test", new Dimension(600, 400), true, true);
			Framework.getFramework().registerScene(new ColorDemoScene("cds", Color.YELLOW));
			Framework.getFramework().registerScene(new ImageScene("img", ImageIO.read(new File("D:\\jtest5\\img.png")),
					false, ImageScene.ImageTiling.FIT));
			Framework.getFramework().start(true, "cds");
			
			int test = 2147483647;
			test++;
			System.out.println(test);
			
			int data1 = 23;
			int data2 = 178;
			System.out.println((byte) data1);
			System.out.println((byte) data2);
			System.out.println(Integer.toBinaryString(data2));
			System.out.println(Integer.toBinaryString((byte) data2));
			
		}catch(InvalidSceneException | FrameworkStateException | IOException e){
			Framework.getFramework().exitFatal(true, e);
		}
	}

}
