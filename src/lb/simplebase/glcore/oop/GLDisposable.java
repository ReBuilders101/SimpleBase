package lb.simplebase.glcore.oop;

import lb.simplebase.glcore.GLFramework;

@Deprecated
public interface GLDisposable {

	public static boolean AUTO_DISPOSE = true;
	
	public Runnable getDisposeAction();
	
	public default void dispose() {
		getDisposeAction().run();
	}
	
	@Deprecated
	public static void registerTask(GLDisposable task) {
		if(AUTO_DISPOSE) {
			GLFramework.gfAddTerminateTask(task.getDisposeAction());
		}
	}
	
}
