package lb.simplebase.gl;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;

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
	
	public static float glColor(int value) {
		return (float) value / 255f;
	}
	
	public static boolean glfwBool(int value) {
		return value == GLFW.GLFW_TRUE;
	}

	public static GLFWImage createImage(BufferedImage image) {
		if(image.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
			DataBufferByte dataBuf = (DataBufferByte) image.getRaster().getDataBuffer();
			byte[] data = dataBuf.getData();
			ByteBuffer byteBuf = BufferUtils.createByteBuffer(data.length);
			byteBuf.put(data);
			byteBuf.flip();
			return GLFWImage.create().set(image.getWidth(), image.getHeight(), byteBuf);
		} else {
			throw new IllegalArgumentException("BufferedImage type must be TYPE_4BYTE_ABGR");
		}
		
		
	}
	
}
