package lb.simplebase.glcore;

import org.lwjgl.glfw.GLFWKeyCallbackI;

public interface GLProgram {
	
	public void init();
	public void render();
	public void update();
	public void dispose();
	public void onKeyInput(int key, int scancode, int action, int mods);
	public default boolean stopProgram() {
		return false;
	}
	
	public static GLFWKeyCallbackI forProgram(GLProgram program) {
		return (window, key, scancode, action, mods) -> program.onKeyInput(key, scancode, action, mods);
	}
}
