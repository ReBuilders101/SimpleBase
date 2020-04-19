package lb.simplebase.gl.window;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import lb.simplebase.gl.GLHandle;
import lb.simplebase.gl.GlUtils;

public class Window implements GLHandle {

	private final long handle;
	
	private Window(long handle) {
		this.handle = handle;
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
		final IntBuffer x = BufferUtils.createIntBuffer(1);
		final IntBuffer y = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(handle, x, y);
		return new int[] {x.get(0), y.get(0)};
	}
	
	public Dimension getSizeDim() {
		final IntBuffer x = BufferUtils.createIntBuffer(1);
		final IntBuffer y = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(handle, x, y);
		return new Dimension(x.get(0), y.get(0));
	}
	
	public int getWidth() {
		final IntBuffer x = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(handle, x, null);
		return x.get(0);
	}
	
	public int getHeight() {
		final IntBuffer y = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(handle, null, y);
		return y.get(0);
	}
	
	public int[] getWindowFrameSize() {
		final IntBuffer left = BufferUtils.createIntBuffer(1);
		final IntBuffer top = BufferUtils.createIntBuffer(1);
		final IntBuffer right = BufferUtils.createIntBuffer(1);
		final IntBuffer bottom = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowFrameSize(handle, left, top, right, bottom);
		return new int[] {left.get(0), top.get(0), right.get(0), bottom.get(0)};
	}
	
	public int[] getFramebufferSize() {
		final IntBuffer x = BufferUtils.createIntBuffer(1);
		final IntBuffer y = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetFramebufferSize(handle, x, y);
		return new int[] {x.get(0), y.get(0)};
	}
	
	public float[] getContentScale() {
		final FloatBuffer x = BufferUtils.createFloatBuffer(1);
		final FloatBuffer y = BufferUtils.createFloatBuffer(1);
		GLFW.glfwGetWindowContentScale(handle, x, y);
		return new float[] {x.get(0), y.get(0)};
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
		final IntBuffer x = BufferUtils.createIntBuffer(1);
		final IntBuffer y = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowPos(handle, x, y);
		return new int[] {x.get(0), y.get(0)};
	}
	
	public Point getPositionPoint() {
		final IntBuffer x = BufferUtils.createIntBuffer(1);
		final IntBuffer y = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowPos(handle, x, y);
		return new Point(x.get(0), y.get(0));
	}
	
	public void setTitle(CharSequence title) {
		GLFW.glfwSetWindowTitle(handle, title);
	}
	
	public void setIcon(BufferedImage image) {
		setIcon(createImage(image));
	}
	
	public void setIcon(BufferedImage image, BufferedImage...moreImages) {
		GLFWImage[] images = new GLFWImage[moreImages.length];
		for(int i = 0; i < moreImages.length; i++) {
			images[i] = createImage(moreImages[i]);
		}
		setIcon(createImage(image), images);
	}
	
	public void setIcon(GLFWImage image) {
		GLFWImage.Buffer buffer = GLFWImage.create(1);
		buffer.put(0, image);
		setIcon(buffer);
	}
	
	public void setIcon(GLFWImage image, GLFWImage...moreImages) {
		GLFWImage.Buffer buffer = GLFWImage.create(moreImages.length + 1);
		buffer.put(0, image);
		for(int i = 0; i < moreImages.length; i++) {
			buffer.put(i+1, moreImages[i]);
		}
		setIcon(buffer);
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
	
	
	//////////////////// BUILDER   ////////////////////
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private CharSequence title = null;
		private WindowHints windowHints = null;
		private GlContextHints contextHints = null;
		private int doubleBuffer = GLFW.GLFW_TRUE;
		private int stereoscopic = GLFW.GLFW_FALSE;
		
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
		
		//TODO FramebufferHints interface?
		
		public Builder setWindowHints(WindowHints hints) {
			this.windowHints = hints;
			return this;
		}
		
		public Builder setContextHints(GlContextHints hints) {
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
			Objects.requireNonNull(windowHints, "Window hints must not be null");
			Objects.requireNonNull(contextHints, "GL Context hints must not be null");
			GlUtils.checkMainThread();
			
			windowHints.apply();
			contextHints.apply();
			GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, doubleBuffer);
			GLFW.glfwWindowHint(GLFW.GLFW_STEREO, stereoscopic);
			long handle = GLFW.glfwCreateWindow(windowHints.getWidthRes(), windowHints.getHeightRes(), title, windowHints.getMonitor(), sharedContext);
			
			return new Window(handle);
		}
		
	}	
	
	public static GLFWImage createImage(BufferedImage image) {
		DataBufferByte dataBuf = (DataBufferByte) image.getRaster().getDataBuffer();
		byte[] data = dataBuf.getData();
		ByteBuffer byteBuf = BufferUtils.createByteBuffer(data.length);
		byteBuf.put(data);
		byteBuf.flip();
		return GLFWImage.create().set(image.getWidth(), image.getHeight(), byteBuf);
	}
	
}
