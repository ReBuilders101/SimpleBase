package lb.simplebase.gl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class GlUtils {

	private static Thread mainGlThread = null;
	
	public static boolean initializeGLFW() {
		if(mainGlThread != null) return false;
		mainGlThread = Thread.currentThread();
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		return GLFW.glfwInit();
	}
	
	public static void terminateGLFW() {
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}
	
	public static boolean isMainThread() {
		return isMainThread(Thread.currentThread());
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
