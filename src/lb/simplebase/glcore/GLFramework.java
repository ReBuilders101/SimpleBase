package lb.simplebase.glcore;

import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

import lb.simplebase.core.AnyState;
import lb.simplebase.core.FrameworkState;
import lb.simplebase.core.RequireState;
import lb.simplebase.log.LogHelper;
import lb.simplebase.log.LogLevel;
import lb.simplebase.log.Logger;
import lb.simplebase.log.OutputChannel;


public final class GLFramework {

	private GLFramework() {}
	
	//Make it linked, because we need to add capacity a lot and only have to iterate once and in (any) order
	private static final List<Runnable> disposeActions = new LinkedList<>();
	//The window handle, if created
	private static long windowId = 0;
	//The logger foe this class
	private static Logger logger = LogHelper.create(GLFramework.class, LogLevel.INFO);
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
	private static GLProgram program;
	
	private static boolean shouldStop = false;
	private static boolean useCloseFlag = true;
	
	///////////////FLAGS/////////////
	private static boolean exitOnStop = true;
	private static boolean autoClear = true;
	
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
			logger.warn("GLFW is taking %dms to initialize. Updating your graphics driver may accelerate initialization.", loadTime);
		}
		state = FrameworkState.INITIALIZED;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void gfStart() {
		if(state != FrameworkState.INITIALIZED) {
			logger.error("Cannot call gfStart() when the framework has not been initialized or already been started");
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
		GLFW.glfwSetKeyCallback(windowId, GLProgram.forProgram(program));
		
		state = FrameworkState.STARTED;
		
		gfMainLoopImpl(); //Starts the main program loop. Blocking.
	}
	
	@RequireState(FrameworkState.INITIALIZED) //Actually also Uninitialized
	public static void gfSetProgram(GLProgram program) {
		if(state != FrameworkState.UNINITIALIZED && state != FrameworkState.INITIALIZED) {
			logger.error("Cannot set program if framework has already been started");
			return;
		}
		GLFramework.program = program;
	}
	
	@RequireState(FrameworkState.STARTED)
	public static float gfAspectRatio() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Aspect ratio only exist after the window has been created");
			return 0;
		}
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetWindowSize(windowId, width, height);
		return (float) width[0] / (float) height[0];
	}
	
	//Stop after loop has ended
	private static void gfStopImpl() {
		logger.info("Stopping from state " + state);
		switch (state) {
		case STARTED:
			disposeActions.forEach((r) -> r.run()); //Run all actions first
			GLFW.glfwDestroyWindow(windowId); //Destroy the window if one had been created
			GLFW.glfwTerminate(); //Teminate, if initialized
			if(exitOnStop) System.exit(0); //If requested, exit the Application
			break;
		case INITIALIZED:
			disposeActions.forEach((r) -> r.run()); //Run all actions first
			GLFW.glfwTerminate(); //Teminate, if initialized
			if(exitOnStop) System.exit(0); //If requested, exit the Application
			break;
		case UNINITIALIZED:
			disposeActions.forEach((r) -> r.run()); //Run all actions always, except when state is ENDED, because then they have been run already
			if(exitOnStop) System.exit(0); //If requested, exit the Application
			break;
		case ENDED:
			if(exitOnStop) System.exit(0); //If requested, exit the Application
			break;
		}
		logger.info("Stopped successfully without exiting");
		state = FrameworkState.ENDED;
	}
	
	@AnyState
	public static void gfStop() {
		if(state == FrameworkState.STARTED) {
			shouldStop = true;
		} else {
			gfStopImpl();
		}
	}
	
	@AnyState
	public static void gfEnableAutoClear() {
		autoClear = true;
	}
	@AnyState
	public static void gfDisableAutoClear() {
		autoClear = false;
	}
	
	private static boolean windowClose() {
		return useCloseFlag && GLFW.glfwWindowShouldClose(windowId);
	}
	
	private static void gfMainLoopImpl() {
		if(program == null) {
			logger.error("No program is set. Exiting.");
			gfStopImpl();
			return;
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		program.init();
		
		while(!program.stopProgram() && !shouldStop && !windowClose()) {
			GL11.glViewport(0, 0, width, height);
			if(autoClear) glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			program.update();
			program.render();
			//Update window
			GLFW.glfwPollEvents();
			GLFW.glfwSwapBuffers(windowId);
		}
		
		program.dispose();
		gfStopImpl();
	}
	
	@AnyState
	public static FrameworkState gfGetState() {
		return state;
	}
	
	@AnyState
	public static void gfIgnoreWindowCloseFlag() {
		useCloseFlag = false;
	}
	
	@RequireState(FrameworkState.STARTED)
	public static GLCapabilities gfGetGLCapabilities() {
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
	
	@RequireState(FrameworkState.STARTED)
	public static void gfSwapWindowBuffers() {
		if(state != FrameworkState.STARTED) return;
		GLFW.glfwSwapBuffers(windowId);
	}
	
	@AnyState
	public static void gfAddTerminateTask(Runnable task) {
		disposeActions.add(task);
	}
	
	@AnyState
	public static void gfSetLogOutput(OutputChannel out) {
		logger = LogHelper.create(GLFramework.class, out);
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
	public static void gfEnableStickyKeys() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_KEYS, GL11.GL_TRUE);
	}
	@RequireState(FrameworkState.STARTED)
	public static void gfDisableStickyKeys() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_KEYS, GL11.GL_FALSE);
	}
	@RequireState(FrameworkState.STARTED)
	public static void gfEnableStickyMouse() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GL11.GL_TRUE);
	}
	@RequireState(FrameworkState.STARTED)
	public static void gfDisableStickyMouse() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GL11.GL_FALSE);
	}
	@RequireState(FrameworkState.STARTED)
	public static void gfShowCursor() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
	}
	@RequireState(FrameworkState.STARTED)
	public static void gfHideCursor() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
	}
	@RequireState(FrameworkState.STARTED)
	public static void gfGrabCursor() {
		if(state != FrameworkState.STARTED) {
			logger.warn("Input preferences can only be set if the window has been created and not been destroyed");
			return;
		}
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
	}
}
