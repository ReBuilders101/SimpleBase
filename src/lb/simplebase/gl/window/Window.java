package lb.simplebase.gl.window;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowContentScaleCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowMaximizeCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import lb.simplebase.gl.GLHandleLong;
import lb.simplebase.gl.GlUtils;
import lb.simplebase.util.Consumer1B;
import lb.simplebase.util.Consumer2F;
import lb.simplebase.util.Consumer2I;
import lb.simplebase.util.Consumer4I;

public class Window implements GLHandleLong {

	private final long handle;
	
	private Window(long handle) {
		if(handle == 0) throw new RuntimeException("Window handle is 0: Error during window creation");
		this.handle = handle;
		this.framebuffer = new Framebuffer();
		ids.put(handle, this);
	}
	
	@Override
	public long getGLHandle() {
		return handle;
	}
	
	public boolean isFullscreen() {
		return getFullscreenMonitor() != MemoryUtil.NULL;
	}
	
	public long getFullscreenMonitor() {
		return GLFW.glfwGetWindowMonitor(handle);
	}
	
	public boolean isWindowed() {
		return getFullscreenMonitor() == MemoryUtil.NULL;
	}
	
	public void setFullscreen(long monitorHandle, int widthResolution, int heightResolution) {
		setFullscreen(monitorHandle, widthResolution, heightResolution, GLFW.GLFW_DONT_CARE);
	}
	
	public void setFullscreen(long monitorHandle, int widthResolution, int heightResolution, int refreshRate) {
		GLFW.glfwSetWindowMonitor(handle, monitorHandle, 0, 0, widthResolution, heightResolution, refreshRate);
	}
	
	public void setFullscreen(long monitorHandle) {
		setFullscreen(monitorHandle, getWidth(), getHeight());
	}
	
	public void setWindowedFullscreen(long monitorHandle) {
		GLFWVidMode mode = GLFW.glfwGetVideoMode(monitorHandle);
		setFullscreen(monitorHandle, mode.width(), mode.height(), mode.refreshRate());
	}
	
	public void setWindowed(int width, int height) {
		setFullscreen(MemoryUtil.NULL, width, height);
	}
	
	public void setWindowed(int width, int height, int windowXpos, int windowYpos) {
		GLFW.glfwSetWindowMonitor(handle, MemoryUtil.NULL, windowXpos, windowYpos, width, height, GLFW.GLFW_DONT_CARE);
	}
	
	public boolean getCloseFlag() {
		return GLFW.glfwWindowShouldClose(handle);
	}
	
	public void setCloseFlag(boolean value) {
		GLFW.glfwSetWindowShouldClose(handle, value);
	}
	
	public void setSize(int width, int height) {
		GLFW.glfwSetWindowSize(handle, width, height);
	}
	
	public void setWidth(int width) {
		setSize(width, getHeight());
	}
	
	public void setHeight(int height) {
		setSize(getWidth(), height);
	}
	
	public int[] getSize() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer x = stack.mallocInt(1);
			final IntBuffer y = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(handle, x, y);
			return new int[] {x.get(0), y.get(0)};
		}
	}
	
	public Dimension getSizeDim() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer x = stack.mallocInt(1);
			final IntBuffer y = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(handle, x, y);
			return new Dimension(x.get(0), y.get(0));
		}
	}
	
	public int getWidth() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer x = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(handle, x, null);
			return x.get(0);
		}
	}
	
	public int getHeight() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer y = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(handle, null, y);
			return y.get(0);
		}
	}
	
	public int[] getWindowFrameSize() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer left = stack.mallocInt(1);
			final IntBuffer top = stack.mallocInt(1);
			final IntBuffer right = stack.mallocInt(1);
			final IntBuffer bottom = stack.mallocInt(1);
			GLFW.glfwGetWindowFrameSize(handle, left, top, right, bottom);
			return new int[] {left.get(0), top.get(0), right.get(0), bottom.get(0)};
		}
	}
	
	public float[] getContentScale() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final FloatBuffer x = stack.mallocFloat(1);
			final FloatBuffer y = stack.mallocFloat(1);
			GLFW.glfwGetWindowContentScale(handle, x, y);
			return new float[] {x.get(0), y.get(0)};
		}
	}
	
	public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
		GLFW.glfwSetWindowSizeLimits(handle, minWidth, minHeight, maxWidth, maxHeight);
	}
	
	public void resetSizeLimits() {
		GLFW.glfwSetWindowSizeLimits(handle, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
	}
	
	public void setMaximumSize(int maxWidth, int maxHeight) {
		GLFW.glfwSetWindowSizeLimits(handle, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE, maxWidth, maxHeight);
	}
	
	public void setMinimumSize(int minWidth, int minHeight) {
		GLFW.glfwSetWindowSizeLimits(handle, minWidth, minHeight, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
	}
	
	public void setAspectRatio(int relativeWidth, int relativeHeight) {
		GLFW.glfwSetWindowAspectRatio(handle, relativeWidth, relativeHeight);
	}
	
	public void setCurrentAspectRatio() {
		int[] dims = getSize();
		setAspectRatio(dims[0], dims[1]);
	}
	
	public void resetAspectRatio() {
		setAspectRatio(GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
	}
	
	public void setPosition(int x, int y) {
		GLFW.glfwSetWindowPos(handle, x, y);
	}
	
	public int[] getPosition() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer x = stack.mallocInt(1);
			final IntBuffer y = stack.mallocInt(1);
			GLFW.glfwGetWindowPos(handle, x, y);
			return new int[] {x.get(0), y.get(0)};
		}
	}
	
	public Point getPositionPoint() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer x = stack.mallocInt(1);
			final IntBuffer y = stack.mallocInt(1);
			GLFW.glfwGetWindowPos(handle, x, y);
			return new Point(x.get(0), y.get(0));
		}
	}
	
	public void centerWindow(long monitorHandle) {
		GLFWVidMode mode = GLFW.glfwGetVideoMode(monitorHandle);
		Dimension size = getSizeDim();
		setPosition((mode.width() - size.width) / 2, (mode.height() - size.height) / 2);
	}
	
	public void setTitle(CharSequence title) {
		GLFW.glfwSetWindowTitle(handle, title);
	}
	
	public void setIcon(BufferedImage image) {
		setIcon(GlUtils.createImage(image));
	}
	
	public void setIcon(BufferedImage image, BufferedImage...moreImages) {
		GLFWImage[] images = new GLFWImage[moreImages.length];
		for(int i = 0; i < moreImages.length; i++) {
			images[i] = GlUtils.createImage(moreImages[i]);
		}
		setIcon(GlUtils.createImage(image), images);
	}
	
	public void setIcon(GLFWImage image) {
		try(GLFWImage.Buffer buffer = GLFWImage.mallocStack(1)) {
			buffer.put(0, image);
			setIcon(buffer);
		}
	}
	
	public void setIcon(GLFWImage image, GLFWImage...moreImages) {
		try(GLFWImage.Buffer buffer = GLFWImage.mallocStack(moreImages.length + 1)) {
			buffer.put(0, image);
			for(int i = 0; i < moreImages.length; i++) {
				buffer.put(i+1, moreImages[i]);
			}
			setIcon(buffer);
		}
	}
	
	private void setIcon(GLFWImage.Buffer buffer) {
		GLFW.glfwSetWindowIcon(handle, buffer);
	}
	
	public void setMinimized(boolean value) {
		final boolean state = isMinimized();
		if(value == state) return;
		
		if(value) {
			GLFW.glfwIconifyWindow(handle);
		} else {
			GLFW.glfwRestoreWindow(handle);
		}
	}
	
	public boolean isMinimized() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_ICONIFIED));
	}
	
	public void setMaximized(boolean value) {
		final boolean state = isMaximized();
		if(value == state) return;
		
		if(value) {
			GLFW.glfwMaximizeWindow(handle);
		} else {
			GLFW.glfwRestoreWindow(handle);
		}
	}
	
	public boolean isMaximized() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_MAXIMIZED));
	}
	
	public void setVisible(boolean value) {
		final boolean state = isMaximized();
		if(value == state) return;
		
		if(value) {
			GLFW.glfwShowWindow(handle);
		} else {
			GLFW.glfwHideWindow(handle);
		}
	}
	
	public boolean isVisible() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_VISIBLE));
	}
	
	public void setFocused() {
		GLFW.glfwFocusWindow(handle);
	}
	
	public boolean isFocused() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_FOCUSED));
	}
	
	public void highlightWindow() {
		GLFW.glfwRequestWindowAttention(handle);
	}
	
	public boolean isFramebufferTransparent() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_TRANSPARENT_FRAMEBUFFER));
	}
	
	public float getWindowOpacity() {
		return GLFW.glfwGetWindowOpacity(handle);
	}
	
	public void setWindowOpacity(float value) {
		GLFW.glfwSetWindowOpacity(handle, value);
	}
	
	public void setDecorated(boolean value) {
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GlUtils.glfwBool(value));
	}
	
	public boolean isDecorated() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_DECORATED));
	}
	
	public void setResizable(boolean value) {
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_RESIZABLE, GlUtils.glfwBool(value));
	}
	
	public boolean isResizable() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_RESIZABLE));
	}
	
	public void setFloating(boolean value) {
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FLOATING, GlUtils.glfwBool(value));
	}
	
	public boolean isFloating() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_FLOATING));
	}
	
	public void setAutoMinimizing(boolean value) {
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_AUTO_ICONIFY, GlUtils.glfwBool(value));
	}
	
	public boolean isAutoMinimizing() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_AUTO_ICONIFY));
	}
	
	public void setFocusingOnShow(boolean value) {
		GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_FOCUS_ON_SHOW, GlUtils.glfwBool(value));
	}
	
	public boolean isFocusingOnShow() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_FOCUS_ON_SHOW));
	}
	
	public boolean isCursorHovered() {
		return GlUtils.glfwBool(GLFW.glfwGetWindowAttrib(handle, GLFW.GLFW_HOVERED));
	}
	
	
	public void swapBuffers() {
		GLFW.glfwSwapBuffers(handle);
	}
	
	public void setUserPointer(long pointer) {
		GLFW.glfwSetWindowUserPointer(handle, pointer);
	}
	
	public long getUserPointer() {
		return GLFW.glfwGetWindowUserPointer(handle);
	}
	
	public double[] getCursorPosition() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer x = stack.mallocDouble(1);
			DoubleBuffer y = stack.mallocDouble(1);
			GLFW.glfwGetCursorPos(handle, x, y);
			return new double[] {x.get(0), y.get(0)};
		}
	}
	
	public Point2D getCursorPositionPoint() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer x = stack.mallocDouble(1);
			DoubleBuffer y = stack.mallocDouble(1);
			GLFW.glfwGetCursorPos(handle, x, y);
			return new Point2D.Double(x.get(0), y.get(0));
		}
	}
	
	public double getCursorPositionX() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer x = stack.mallocDouble(1);
			GLFW.glfwGetCursorPos(handle, x, null);
			return x.get(0);
		}
	}
	
	public double getCursorPositionY() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer y = stack.mallocDouble(1);
			GLFW.glfwGetCursorPos(handle, null, y);
			return y.get(0);
		}
	}
	
	public void setCursorPosition(double x, double y) {
		GLFW.glfwSetCursorPos(handle, x, y);
	}
	
	public void setCursor(long pointer) {
		GLFW.glfwSetCursor(handle, pointer);
	}
	
	public void setStickyKeys(boolean value) {
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_STICKY_KEYS, GlUtils.glfwBool(value));
	}
	
	public boolean getStickyKeys() {
		return GlUtils.glfwBool(GLFW.glfwGetInputMode(handle, GLFW.GLFW_STICKY_KEYS));
	}
	
	public void setStickyMouseButtons(boolean value) {
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_STICKY_MOUSE_BUTTONS, GlUtils.glfwBool(value));
	}
	
	public void setLockKeyMods(boolean value) {
		GLFW.glfwSetInputMode(handle, GLFW.GLFW_LOCK_KEY_MODS, GlUtils.glfwBool(value));
	}
	
	public boolean getLockKeyMods() {
		return GlUtils.glfwBool(GLFW.glfwGetInputMode(handle, GLFW.GLFW_LOCK_KEY_MODS));
	}
	
	public boolean isKeyPressed(int key) {
		return GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
	}
	
	public boolean isMouseButtonPressed(int key) {
		return GLFW.glfwGetMouseButton(handle, key) == GLFW.GLFW_PRESS;
	}
	
	public boolean getStickyMouseButtons() {
		return GlUtils.glfwBool(GLFW.glfwGetInputMode(handle, GLFW.GLFW_STICKY_MOUSE_BUTTONS));
	}
	
	//Callbacks
	
	public GLFWWindowCloseCallback setCloseCallback(Consumer<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetWindowCloseCallback(handle, null);
		} else {
			return GLFW.glfwSetWindowCloseCallback(handle, (w) -> callback.accept(this));
		}
	}
	
	public GLFWWindowSizeCallback setResizeCallback(Consumer2I<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetWindowSizeCallback(handle, null);
		} else {
			return GLFW.glfwSetWindowSizeCallback(handle, (w,x,y) -> callback.accept(this, x, y));
		}
	}
	
	public GLFWWindowContentScaleCallback setContentScaleCallback(Consumer2F<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetWindowContentScaleCallback(handle, null);
		} else {
			return GLFW.glfwSetWindowContentScaleCallback(handle, (w,x,y) -> callback.accept(this, x, y));
		}
	}
	
	public GLFWWindowPosCallback setMoveCallback(Consumer2I<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetWindowPosCallback(handle, null);
		} else {
			return GLFW.glfwSetWindowPosCallback(handle, (w,x,y) -> callback.accept(this, x, y));
		}
	}
	
	public GLFWWindowIconifyCallback setMinimizeCallback(Consumer1B<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetWindowIconifyCallback(handle, null);
		} else {
			return GLFW.glfwSetWindowIconifyCallback(handle, (w,v) -> callback.accept(this, v));
		}
	}
	
	public GLFWWindowMaximizeCallback setMaximizeCallback(Consumer1B<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetWindowMaximizeCallback(handle, null);
		} else {
			return GLFW.glfwSetWindowMaximizeCallback(handle, (w,v) -> callback.accept(this, v));
		}
	}
	
	public GLFWWindowFocusCallback setFocusCallback(Consumer1B<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetWindowFocusCallback(handle, null);
		} else {
			return GLFW.glfwSetWindowFocusCallback(handle, (w,v) -> callback.accept(this, v));
		}
	}
	
	public GLFWWindowRefreshCallback setRefreshCallback(Consumer<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetWindowRefreshCallback(handle, null);
		} else {
			return GLFW.glfwSetWindowRefreshCallback(handle, (w) -> callback.accept(this));
		}
	}
	
	public GLFWKeyCallback setKeyCallback(Consumer4I<Window> callback) {
		GlUtils.checkMainThread();
		if(callback == null) {
			return GLFW.glfwSetKeyCallback(handle, null);
		} else {
			return GLFW.glfwSetKeyCallback(handle, (w, a, b, c, d) -> callback.accept(this, a, b, c, d));
		}
	}
	
	public void resetAllCallbacks() {
		Callbacks.glfwFreeCallbacks(handle);
	}
	
	public void destroy() {
		GlUtils.checkMainThread();
		Callbacks.glfwFreeCallbacks(handle);
		GLFW.glfwDestroyWindow(handle);
	}
	
	public void makeContextCurrent() {
		GLFW.glfwMakeContextCurrent(handle);
	}
	
	public void updateGlViewport() {
		int w, h;
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer width = stack.mallocInt(1);
			final IntBuffer height = stack.mallocInt(1);
			GLFW.glfwGetFramebufferSize(handle, width, height);
			w = width.get(0);
			h = height.get(0);
		}
		GL11.glViewport(0, 0, w, h);
	}
	
	public void updateGlViewportSquared() {
		int w, h;
		try(MemoryStack stack = MemoryStack.stackPush()) {
			final IntBuffer width = stack.mallocInt(1);
			final IntBuffer height = stack.mallocInt(1);
			GLFW.glfwGetFramebufferSize(handle, width, height);
			w = width.get(0);
			h = height.get(0);
		}
		if(w == 0 || h == 0) { //effectively invisible, no need to calculate
			GL11.glViewport(0, 0, w, h);
		} if(w > h) {
			int x = (w - h) / 2;
			GL11.glViewport(x, 0, h, h);
		} else {
			int y = (h- w) / 2;
			GL11.glViewport(0, y, w, w);
		}
	}
	
	//////////////////// FRAMEBUFFER  /////////////////
	
	private final Framebuffer framebuffer;
	
	public Framebuffer getFramebuffer() {
		return framebuffer;
	}
	
	public class Framebuffer {
		
		public Window getWindow() {
			return Window.this;
		}
		
		public int[] getSize() {
			try(MemoryStack stack = MemoryStack.stackPush()) {
				final IntBuffer x = stack.mallocInt(1);
				final IntBuffer y = stack.mallocInt(1);
				GLFW.glfwGetFramebufferSize(handle, x, y);
				return new int[] {x.get(0), y.get(0)};
			}
		}
		
		public int getWidth() {
			try(MemoryStack stack = MemoryStack.stackPush()) {
				final IntBuffer x = stack.mallocInt(1);
				GLFW.glfwGetFramebufferSize(handle, x, null);
				return x.get(0);
			}
		}
		
		public int getHeight() {
			try(MemoryStack stack = MemoryStack.stackPush()) {
				final IntBuffer y = stack.mallocInt(1);
				GLFW.glfwGetFramebufferSize(handle, null, y);
				return y.get(0);
			}
		}
		
		public GLFWFramebufferSizeCallback setResizeCallback(Consumer2I<Window.Framebuffer> callback) {
			GlUtils.checkMainThread();
			if(callback == null) {
				return GLFW.glfwSetFramebufferSizeCallback(handle, null);
			} else {
				return GLFW.glfwSetFramebufferSizeCallback(handle, (w,x,y) -> callback.accept(this, x, y));
			}
		}
		
	}
	
	
	//////////////////// BUILDER   ////////////////////
	
	public static void restoreBuilderDefaults() {
		GlUtils.checkMainThread();
		GLFW.glfwDefaultWindowHints();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private CharSequence title = null;
		private WindowHints windowHints = null;
		private WindowHints fullscreenHints = null;
		private ContextHints contextHints = null;
		private int doubleBuffer = GLFW.GLFW_TRUE;
		private int stereoscopic = GLFW.GLFW_FALSE;
		private int multisample = 0;
		private boolean fullscreen = false;
		
		private Builder() {}
		
		public Builder setTitle(CharSequence title) {
			this.title = title;
			return this;
		}
		
		public Builder setDoubleBuffered(boolean value) {
			this.doubleBuffer = GlUtils.glfwBool(value);
			return this;
		}
		
		public Builder setStereoscopic(boolean value) {
			this.stereoscopic = GlUtils.glfwBool(value);
			return this;
		}
		
		public Builder setFullscreen(boolean value) {
			fullscreen = value;
			return this;
		}
		
		public Builder setMultisampleCount(int count) {
			multisample = count;
			return this;
		}
		
		public Builder setWindowedHints(WindowHints.WindowedHints hints) {
			this.windowHints = hints;
			return this;
		}
		
		public Builder setFullscreenHints(WindowHints.FullscreenHints hints) {
			this.fullscreenHints = hints;
			return this;
		}
		
		public Builder setContextHints(ContextHints hints) {
			this.contextHints = hints;
			return this;
		}
		
		public Builder forceHint(int hint, int value) {
			GlUtils.checkMainThread();
			GLFW.glfwWindowHint(hint, value);
			return this;
		}
		
		public Window createWindow() {
			return createWindow(MemoryUtil.NULL);
		}
		
		public Window createWindow(Window sharedContext) {
			return createWindow(sharedContext.getGLHandle());
		}
		
		private Window createWindow(long sharedContext) {
			Objects.requireNonNull(title, "Window title must not be null");
			Objects.requireNonNull(contextHints, "GL Context hints must not be null");
			GlUtils.checkMainThread();
			
			synchronized (ids) {
				if(windowHints != null) windowHints.apply();
				if(fullscreenHints != null) fullscreenHints.apply();
				contextHints.apply();
				GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, doubleBuffer);
				GLFW.glfwWindowHint(GLFW.GLFW_STEREO, stereoscopic);
				GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, multisample);
				
				final long handle;
				if(fullscreen) {
					handle = GLFW.glfwCreateWindow(fullscreenHints.getWidthRes(), fullscreenHints.getHeightRes(), title, fullscreenHints.getMonitor(), sharedContext);
				} else {
					handle = GLFW.glfwCreateWindow(windowHints.getWidthRes(), windowHints.getHeightRes(), title, windowHints.getMonitor(), sharedContext);
				}
				return new Window(handle);
			}
		}
		
	}
	
	public static Window find(long handle) {
		synchronized (ids) {
			return ids.get(handle);
		}
	}
	
	public static Window findOrCreate(long handle) {
		synchronized (ids) {
			Window win = ids.get(handle);
			if(win == null) {
				return new Window(handle);
			} else {
				return win;
			}
		}
	}
	
	private static final Map<Long, Window> ids = new HashMap<>();
}
