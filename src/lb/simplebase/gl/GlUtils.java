package lb.simplebase.gl;

import org.lwjgl.glfw.GLFW;

public class GlUtils {

	private static Thread mainGlThread = null;
	
	public static boolean initializeGLFW() {
		if(mainGlThread != null) return false;
		mainGlThread = Thread.currentThread();
		return GLFW.glfwInit();
	}
	
	public static boolean isMainThread() {
		return isMainThread(Thread.currentThread());
	}
	
	public static void avoidTearing() {
		GLFW.glfwSwapInterval(1);
	}
	
	public static boolean isMainThread(Thread thread) {
		return thread == mainGlThread && thread != null;
	}
	
	public static Thread getMainThread() {
		return mainGlThread;
	}
	
	public static void checkMainThread() {
		if(!isMainThread()) {
			throw new GlThreadException("Method must be called on the main GL thread");
		}
	}
	
	public static int glfwBool(boolean value) {
		return value ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE;
	}
	
	public static boolean glfwBool(int value) {
		return value == GLFW.GLFW_TRUE;
	}
	
}
