package lb.simplebase.javacore;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lb.simplebase.core.AnyState;
import lb.simplebase.core.FrameworkState;
import lb.simplebase.core.RequireState;

public final class Framework {

	public static final String DEFAULT_SCENE_NAME = "default";
	
	private Framework() {};
	
	private static FrameworkState state = FrameworkState.UNINITIALIZED;
	private static boolean exitOnStop = true;
	private static Scene currentScene;
	
	private static Map<String, Scene> scenes;
	private static JFrame mainFrame;
	private static JFrame smallFrame;
	private static JPanel sceneOptions;
	
	private static DrawCallbackPanel smallDcp;
	private static DrawCallbackPanel mainDcp;
	
	private static int fps;
	private static int tps;
	
	private static long tick;
	
	private static Timer tickTimer;
	private static Timer frameTimer;
	
	@RequireState(FrameworkState.UNINITIALIZED)
	public static void init() {
		//Validate state
		if(getState() != FrameworkState.UNINITIALIZED) return;
		//Create the Frames
		//Main Frame first
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainDcp = new DrawCallbackPanel(Framework::onMainPanelDraw);
		mainFrame.add(mainDcp);
		//Then small frame
		smallFrame = new JFrame();
		smallFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		smallFrame.addWindowListener(new WindowListenerStopOnClose());
		smallDcp = new DrawCallbackPanel(Framework::onSmallPanelDraw);
		smallFrame.setLayout(new GridLayout(1, 2));
		JPanel leftSide = new JPanel(new GridLayout(2, 1));
		JPanel preview = new JGroupBox("Live Preview", new BorderLayout());
		preview.add(smallDcp); //Add at top left
		leftSide.add(preview);
		JPanel generalOptions = new JGroupBox("General Options");
		leftSide.add(generalOptions);
		smallFrame.add(leftSide);
		sceneOptions = new JGroupBox("Scene Options");
		smallFrame.add(sceneOptions);
		
		//Init timers
		tickTimer = new Timer("TickTimerThread", true);
		frameTimer = new Timer("FrameTimerThread", true);
		//Tasks will be set in start()
		//Set variables
		fps = 60;
		tps = 30;
		tick = 0;
		scenes = new HashMap<>();
		//Add default states
		Scene defaultScene = Scene.createEmpty(DEFAULT_SCENE_NAME);
		scenes.put(DEFAULT_SCENE_NAME, defaultScene);
		//Update state
		state = FrameworkState.INITIALIZED;
	}
	
	@AnyState
	public static void stop() {
		switch(getState()) {
		case ENDED:
		case STARTED:
			//stop timers
			tickTimer.cancel();
			frameTimer.cancel();
		case INITIALIZED:
			//Dispose frames
			mainFrame.dispose();
			smallFrame.dispose();
		case UNINITIALIZED:
			if(exitOnStop) System.exit(0);
		}
		state = FrameworkState.ENDED;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void start() {
		if(getState() != FrameworkState.INITIALIZED) return;
		//Show frames
		mainFrame.pack();
		mainFrame.setVisible(true);
		smallFrame.pack();
		smallFrame.setVisible(true);
		tick = 0;
		//Start timers
		tickTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				onUpdateTask();
			}
		}, 0, 1000 / tps);
		
		frameTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				mainDcp.repaint();
				smallDcp.repaint();
			}
		}, 0, 1000 / fps);
		//Set current scene
		currentScene = scenes.get(DEFAULT_SCENE_NAME);
		
		state = FrameworkState.STARTED;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void setTitle(String title) {
		if(getState() != FrameworkState.INITIALIZED) return;
		mainFrame.setTitle(title);
		smallFrame.setTitle(title + " - Options");
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void setBorderlessFullscreen(boolean active) {
		if(getState() != FrameworkState.INITIALIZED) return;
		mainFrame.setUndecorated(active);
		if(active) mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		if(active) smallFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	@AnyState
	public static FrameworkState getState() {
		return state;
	}
	
	@AnyState
	public static void setExitOnStop(boolean eos) {
		exitOnStop = eos;
	}
	
	public static void setFPS(int fps) {
		Framework.fps = fps;
	}
	
	public static void setTPS(int tps) {
		Framework.tps = tps;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static boolean addScene(Scene scene) {
		if(getState() != FrameworkState.INITIALIZED) return false;
		if(scene == null) return false;
		String name = scene.getName();
		if(name == null || name.isEmpty()) return false;
		if(scenes.containsKey(name)) return false;
		scenes.put(name, scene);
		return true;
	}
	
	@RequireState(FrameworkState.STARTED)
	public static boolean setActiveScene(String name) {
		if(getState() != FrameworkState.STARTED) return false;
		if(name == null || name.isEmpty()) return false;
		if(!scenes.containsKey(name)) return false;
		Scene requested = scenes.get(name);
		if(requested == null) return false;
		currentScene = requested;
		return true;
	}
	
	@AnyState
	public static long getCurrentTick() {
		return tick;
	}
	
	private static void onMainPanelDraw(Graphics2D g, int width, int height) {
		if(currentScene != null) currentScene.draw(g, width, height);
	}
	
	private static void onSmallPanelDraw(Graphics2D g, int width, int height) {
		if(currentScene != null) currentScene.draw(g, width, height);
	}
	
	private static void onUpdateTask() {
		if(currentScene != null) currentScene.update(tick);
		tick++; //Increment for every update
	}
	
	private static class WindowListenerStopOnClose implements WindowListener {
		@Override
		public void windowOpened(WindowEvent e) {}
		@Override
		public void windowIconified(WindowEvent e) {}
		@Override
		public void windowDeiconified(WindowEvent e) {}
		@Override
		public void windowDeactivated(WindowEvent e) {}
		@Override
		public void windowClosed(WindowEvent e) {}
		@Override
		public void windowActivated(WindowEvent e) {}
		@Override
		public void windowClosing(WindowEvent e) {
			stop();
		}
	}
	
}
