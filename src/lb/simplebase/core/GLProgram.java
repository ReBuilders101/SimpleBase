package lb.simplebase.core;

public interface GLProgram {
	
	public void init();
	public void render();
	public void update();
	public void dispose();
	public default boolean stopProgram() {
		return false;
	}
	
}
