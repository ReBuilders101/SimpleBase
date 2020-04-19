package lb.simplebase.gl.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import lb.simplebase.gl.GlUtils;

public interface WindowHints {

	public void apply();
	public boolean isFullscreen();
	public long getMonitor();
	public int getWidthRes();
	public int getHeightRes();
	
	public static WindowedHints windowed() {
		return new WindowedHints();
	}
	
	public static FullscreenHints fullscreen() {
		return new FullscreenHints();
	}
	
	public static class WindowedHints implements WindowHints {

		private WindowedHints() {}
		
		//Actual hints
		private int resizable = GLFW.GLFW_TRUE;
		private int visible = GLFW.GLFW_TRUE;
		private int decorated = GLFW.GLFW_TRUE;
		private int focused = GLFW.GLFW_TRUE;
		private int floating = GLFW.GLFW_FALSE;
		private int maximized = GLFW.GLFW_FALSE;
		private int transparent = GLFW.GLFW_FALSE;
		private int focusOnShow = GLFW.GLFW_TRUE;
		private int scaleToMonitor = GLFW.GLFW_FALSE;
		
		//Related
		private int width = 0;
		private int height = 0;
		
		public WindowedHints setSize(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}
		
		public WindowedHints setBorderlessMaximized() {
			setResizable(false);
			setDecorated(false);
			setMaximized(true);
			return this;
		}
		
		public WindowedHints setResizable(boolean value) {
			resizable = GlUtils.glfwBool(value);
			return this;
		}
		
		public WindowedHints setVisible(boolean value) {
			visible = GlUtils.glfwBool(value);
			return this;
		}
		
		public WindowedHints setDecorated(boolean value) {
			decorated = GlUtils.glfwBool(value);
			return this;
		}
		
		public WindowedHints setFocused(boolean value) {
			focused = GlUtils.glfwBool(value);
			return this;
		}
		
		public WindowedHints setFloating(boolean value) {
			floating = GlUtils.glfwBool(value);
			return this;
		}
		
		public WindowedHints setMaximized(boolean value) {
			maximized = GlUtils.glfwBool(value);
			return this;
		}
		
		public WindowedHints setTransparent(boolean value) {
			transparent = GlUtils.glfwBool(value);
			return this;
		}
		
		public WindowedHints setFocusOnShow(boolean value) {
			focusOnShow = GlUtils.glfwBool(value);
			return this;
		}
		
		public WindowedHints setScaleToMonitor(boolean value) {
			scaleToMonitor = GlUtils.glfwBool(value);
			return this;
		}
		
		@Override
		public void apply() {
			GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable);
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, visible);
			GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, decorated);
			GLFW.glfwWindowHint(GLFW.GLFW_FOCUSED, focused);
			GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, floating);
			GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, maximized);
			GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparent);
			GLFW.glfwWindowHint(GLFW.GLFW_FOCUS_ON_SHOW, focusOnShow);
			GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, scaleToMonitor);
		}

		@Override
		public boolean isFullscreen() {
			return false;
		}

		@Override
		public long getMonitor() {
			return MemoryUtil.NULL;
		}

		@Override
		public int getWidthRes() {
			return width;
		}

		@Override
		public int getHeightRes() {
			return height;
		}
		
	}

	public static class FullscreenHints implements WindowHints {

		private FullscreenHints() {}
		
		//Hints
		private int autoIconify = GLFW.GLFW_TRUE;
		private int centerCursor = GLFW.GLFW_TRUE;
		private int transparent = GLFW.GLFW_FALSE;
		private int focusOnShow = GLFW.GLFW_TRUE;
		private int scaleToMonitor = GLFW.GLFW_FALSE;
		private int refreshRate = GLFW.GLFW_DONT_CARE;
		
		//Related
		private long monitor = MemoryUtil.NULL;
		private int widthRes = 0;
		private int heightRes = 0;
		private boolean borderless = false;
		private boolean primary = false;

		
		public FullscreenHints setAutoMinimize(boolean value) {
			this.autoIconify = GlUtils.glfwBool(value);
			return this;
		}
		
		public FullscreenHints setFullscreenResolution(int xRes, int yRes) {
			this.widthRes = xRes;
			this.heightRes = yRes;
			this.borderless = false;
			return this;
		}
		
		public FullscreenHints setBorderlessFullscreen() {
			this.borderless = true;
			return this;
		}
		
		public FullscreenHints setRefreshRate(int rate) {
			this.refreshRate = rate;
			this.borderless = false;
			return this;
		}
		
		public FullscreenHints setMonitor(long monitorHandle) {
			this.monitor = monitorHandle;
			this.primary = false;
			return this;
		}
		
		public FullscreenHints setPrimaryMonitor() {
			this.primary = true;
			return this;
		}
		
		public FullscreenHints setTransparent(boolean value) {
			transparent = GlUtils.glfwBool(value);
			return this;
		}
		
		public FullscreenHints setFocusOnShow(boolean value) {
			focusOnShow = GlUtils.glfwBool(value);
			return this;
		}
		
		public FullscreenHints setScaleToMonitor(boolean value) {
			scaleToMonitor = GlUtils.glfwBool(value);
			return this;
		}
		
		@Override
		public void apply() {
			
			if(borderless) { //Copy vidmode
				GLFWVidMode mode = GLFW.glfwGetVideoMode(getMonitor());
				GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, mode.redBits());
				GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, mode.greenBits());
				GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, mode.blueBits());
				GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, mode.refreshRate());
			} else {
				GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, refreshRate);
			}
			
			GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, autoIconify);
			GLFW.glfwWindowHint(GLFW.GLFW_CENTER_CURSOR, centerCursor);
			GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, transparent);
			GLFW.glfwWindowHint(GLFW.GLFW_FOCUS_ON_SHOW, focusOnShow);
			GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, scaleToMonitor);
		}

		@Override
		public boolean isFullscreen() {
			return true;
		}

		@Override
		public long getMonitor() {
			if(primary) {
				return GLFW.glfwGetPrimaryMonitor();
			} else {
				return monitor;
			}
		}

		@Override
		public int getWidthRes() {
			if(borderless) {
				GLFWVidMode mode = GLFW.glfwGetVideoMode(getMonitor());
				return mode.width();
			}
			return widthRes;
		}

		@Override
		public int getHeightRes() {
			if(borderless) {
				GLFWVidMode mode = GLFW.glfwGetVideoMode(getMonitor());
				return mode.height();
			}
			return heightRes;
		}
		
	}
	
}
