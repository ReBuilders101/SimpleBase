package lb.simplebase.core;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

import lb.simplebase.log.LogHelper;
import lb.simplebase.log.Logger;
import lb.simplebase.log.OutputChannel;


public final class GLFramework {

	private GLFramework() {}
	
	//Make it linked, because we need to add capacity a lot and only have to iterate once and in (any) order
	private static final List<Runnable> disposeActions = new LinkedList<>();
	//The window handle, if created
	private static long windowId = 0;
	//The logger foe this class
	private static Logger logger = LogHelper.create(GLFramework.class);
	//The exit code to use when an error occurrs
	private static int errorExitCode = 1;
	//The current state of the framework
	private static FrameworkState state = FrameworkState.UNINITIALIZED;
	//
	private static int width = 800;
	private static int height = 600;
	private static CharSequence title = "";
	private static long monitor = MemoryUtil.NULL;
	private static int swapInterval = 1;
	
	private static GLCapabilities capabilities;
	
	///////////////FLAGS/////////////
	private static boolean exitOnStop = false;
	
	/**
	 * Initializes GLFW.
	 */
	@RequireState(FrameworkState.UNINITIALIZED)
	public static void gfInit() {
		if(state != FrameworkState.UNINITIALIZED) {
			logger.error("Cannot call gfInit() when the framework has already been initialized");
			return;
		}
		//Setup error handler
		GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err); //Create to stderr
		GLFW.glfwSetErrorCallback(errorCallback); //Set callback
		disposeActions.add(() -> errorCallback.free()); //Add action to free callback
		
		logger.info("Initializing GLFW");
		final long startTime = System.currentTimeMillis(); //Get current time to measure init process
		final boolean success = GLFW.glfwInit();
		logger.info("GLFW initialization done");
		if(!success) {
			logger.fatal("GLFW could not be initialized");
			System.exit(errorExitCode);
		}
		final long loadTime = System.currentTimeMillis() - startTime;
		if(loadTime > 15000) {
			logger.warn("GLFW is taking %dms to initialize. Updating your graphics driver may accelerate initializastion.", loadTime);
		}
		state = FrameworkState.INITIALIZED;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfStart() {
		if(state != FrameworkState.INITIALIZED) {
			logger.error("Cannot call gfIStart() when the framework has not been initialized or already been started");
			return;
		}
		windowId = GLFW.glfwCreateWindow(width, height, title, monitor, MemoryUtil.NULL);
		if(windowId == MemoryUtil.NULL) {
			logger.error("Window could not be created.");
			return;
		}
		GLFW.glfwMakeContextCurrent(windowId);
		capabilities = GL.createCapabilities();
		GLFW.glfwSwapInterval(swapInterval);
		
		state = FrameworkState.STARTED;
	}
	
	@AnyState
	public static void gfStop() {
		switch (state) {
		case STARTED: 
			GLFW.glfwDestroyWindow(windowId); //Destroy the window if one had been created
		case INITIALIZED:
			GLFW.glfwTerminate(); //Teminate, if initialized
		case UNINITIALIZED:
			disposeActions.forEach((r) -> r.run()); //Run all actions always, except when state is ENDED, because then they have been run already
		case ENDED:
			if(exitOnStop) System.exit(0); //If requested, exit the Application
		}
		state = FrameworkState.ENDED;
	}
	
	@AnyState
	public static FrameworkState gfGetState() {
		return state;
	}
	
	@RequireState(FrameworkState.STARTED)
	public static GLCapabilities getGLCapabilities() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Capabilities only exist after the window has been created");
			return null;
		}
		return capabilities;
	}
	
	@RequireState(FrameworkState.STARTED)
	public static long gfGetWindowId() {
		if(state != FrameworkState.STARTED) return MemoryUtil.NULL;
		return windowId;
	}
	
	@AnyState
	public static void gfSetLogOutput(OutputChannel out) {
		logger = LogHelper.create(GLFramework.class, out);
	}
	
	@AnyState
	public static void gfExitOnStop() {
		exitOnStop = true;
	}
	
	@AnyState
	public static void gfContinueOnStop() {
		exitOnStop = false;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetSwapInterval(int interval) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Window settings will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		swapInterval = interval;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetWindowTitle(CharSequence title) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Window settings will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFramework.title = title;
	}
	
	@RequireState(FrameworkState.STARTED)
	public static void gfUpdateWindowTitle(CharSequence title) {
		if(state != FrameworkState.STARTED) {
			logger.warn("Window title cannot be changed if window does not exist");
			return;
		}
		GLFW.glfwSetWindowTitle(windowId, title);
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetWindowHeight(int height) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Window settings will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFramework.height = height;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetWindowWidth(int width) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Window settings will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFramework.width = width;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetWindowSize(int width, int height) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Window settings will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFramework.width = width;
		GLFramework.height = height;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetWindowSize(Dimension size) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Window settings will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFramework.width = size.width;
		GLFramework.height = size.height;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetFullscreen(int monitor) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Window settings will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFramework.monitor = monitor;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetWindowed() {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Window settings will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFramework.monitor = MemoryUtil.NULL;
	}
	
	//HINTS
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetHintDirect(int hintId, int newValue) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Hints will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFW.glfwWindowHint(hintId, newValue);
	}
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetMaximized() {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Hints will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
	}
	public static void gfSetInvisible() {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Hints will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
	}
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetNotResizable() {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Hints will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
	}
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfSetGLVersion(int major, int minor) {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Hints will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
	}
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfEnableForwardCompatibility() {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Hints will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
	}
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfRequestCoreProfile() {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Hints will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
	}
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfRequestCompatProfile() {
		if(state != FrameworkState.INITIALIZED) {
			logger.warn("Hints will have no effect if the framework is not initialized or the window has already been created");
			return;
		}
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);
	}
	
	@RequireState(FrameworkState.STARTED)
	public static void enableStickyKeys() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_KEYS, GL11.GL_TRUE);
	}
	@RequireState(FrameworkState.STARTED)
	public static void disableStickyKeys() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_KEYS, GL11.GL_FALSE);
	}
	@RequireState(FrameworkState.STARTED)
	public static void enableStickyMouse() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GL11.GL_TRUE);
	}
	@RequireState(FrameworkState.STARTED)
	public static void disableStickyMouse() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GL11.GL_FALSE);
	}
	@RequireState(FrameworkState.STARTED)
	public static void showCursor() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
	}
	@RequireState(FrameworkState.STARTED)
	public static void hideCursor() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
	}
	@RequireState(FrameworkState.STARTED)
	public static void grabCursor() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}
	
}
