package lb.simplebase.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import lb.simplebase.scene.Scene;

/**
 * @version 1.1
 * @author LB
 * The main class of the framework project. Only one instance of this class should exist, and it is obtainable
 * through the static method {@link #getFramework()}. If other instances are created through reflection, the program may break.
 * The framework has a basic lifecycle:
 * <br>1. {@link #init()}: used for setting window size, tickrate and other properties that can not be altered after starting the framework
 * <br>2. {@link #registerScene(Scene)}: register scenes for the appilcation
 * <br>3. {@link #start()}: starts the timers and displays the application window(s) if desired. This should be the last call in the main method.
 * <br>4. {@link #stop()}: stops the timers and exits the application. This method never returns.
 */
public final class Framework {

	private static final Framework INSTANCE = new Framework();

	//Setup
	private boolean running;
	private boolean setup;
	private long tick;
	private Timer logicTick;
	private Timer frameTick;
	private JFrame mainFrame;
	private JFrame optionFrame;
	private DrawPanel panel;
	private JPanel optionsPanel;
	//Scenes
	private HashMap<String, Scene> scenes;
	private Scene activeScene;
	
	/**
	 * Private constructor. Creates a new uninitialized instance.
	 */
	private Framework(){
		running = false;
		setup = false;
	}
	
	/**
	 * Gets the single instance of this class.
	 * @return the {@link Framework} instance
	 */
	public static Framework getFramework(){
		return INSTANCE;
	}
	
	/**
	 * Initialize the framework before starting it.
	 * @param fps The amount of frames per second
	 * @param tps The amount of ticks per second
	 * @param title The title of the Frame. If a option frame is enabled, its title will be '$TITLE - Options'.
	 * @param initialFrameSize The starting frame size for the main frame
	 * @param optionFrame If an option frame should be shown
	 * @param centerFrame If the main frame should be centered on the screen
	 * @throws FrameworkStateException When the framework is already running
	 */
	public void init(int fps, int tps, String title, Dimension initialFrameSize, boolean optionFrame, boolean centerFrame)
			throws FrameworkStateException{
		if(running) throw new FrameworkStateException("Cannot initialize the framework while it is already running.", true);
		if(setup) throw new FrameworkStateException("Cannot initialize the framework more than once.", false);
		tick = 0;
		logicTick = new Timer(1000 / tps, Framework.getFramework()::logicTickHandler);
		frameTick = new Timer(1000 / fps, Framework.getFramework()::frameTickHandler);
		mainFrame = new JFrame(title);
		mainFrame.setSize(initialFrameSize);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new DrawPanel();
		mainFrame.setContentPane(panel);
		if(centerFrame){
			mainFrame.setLocationRelativeTo(null);
		}
		optionsPanel = new JPanel(new FlowLayout());
		if(optionFrame){
			this.optionFrame = new JFrame(title + " - Options");
			this.optionFrame.setContentPane(optionsPanel);
		}
		scenes = new HashMap<>();
		setup = true;
	}
	
	/**
	 * Registers a new scene by its name. Scenes can only be registered before calling {@link #start(boolean, String)}
	 *  and after calling {@link #init(int, int, String, Dimension, boolean, boolean)}.
	 * @param scene The scene to register
	 * @throws InvalidSceneException When a scene with this name is already registered
	 * @throws FrameworkStateException When the framework is already running
	 */
	public void registerScene(Scene scene) throws InvalidSceneException, FrameworkStateException{
		if(running) throw new FrameworkStateException("Cannot register new scenes while the framework is already running.", true);
		if(!setup) throw new FrameworkStateException("Cannot register new scenes when the framework not yet initialized.", false);
		String name = scene.getName();
		if(scenes.containsKey(name)){
			throw new InvalidSceneException("A scene with the name '" + name + "' is already registered");
		}
		if(scene.getOptions() != null){
			optionsPanel.add(scene.getOptions());
		}
		scenes.put(name, scene);
	}
	
	/**
	 * Starts the framework and the timers for logic and rendering. {@link #init(int, int, String, Dimension, boolean, boolean)} must be called before starting. 
	 * @param showFrames If true, the main and oftion frame (if enabled) will be shown
	 * @param startScene The registered scene that should be displayed first
	 * @throws FrameworkStateException When the framework is already running
	 */
	public void start(boolean showFrames, String startScene) throws FrameworkStateException, InvalidSceneException{
		if(running) throw new FrameworkStateException("Cannot (re)start the framework while it is already running.", true);
		if(!setup) throw new FrameworkStateException("Cannot start the framework when it is not initialized correctly(call setup()).", false);
		Scene startScene2 = scenes.get(startScene);
		if(startScene2 == null){
			throw new InvalidSceneException("The scene '" + startScene + "' was not found." );
		}else{
			activeScene = startScene2;
			activeScene.enable();
		}
		if(showFrames){
			mainFrame.setVisible(true);
			if(optionFrame != null) {
				optionFrame.pack();
				optionFrame.setVisible(true);
			}
		}
		logicTick.start();
		frameTick.start();
		running = true;
	}
	
	/**
	 * Stops all timers and exits the appilcation by calling {@link System#exit(int)}. This method never returns.
	 * @param status The exit code
	 * @throws FrameworkStateException When the framework is not running
	 */
	public void stop(int status) throws FrameworkStateException{
		if(!running) throw new FrameworkStateException("Cannot stop the framework while it is not running.", false);
		logicTick.stop();
		frameTick.stop();
		mainFrame.dispose();
		if(optionFrame != null) optionFrame.dispose();
		running = false;
		System.exit(status);
	}
	
	/**
	 * The handler for the logic timer. This method is called $TPS times per second.
	 * This method should never be called manually, because scenes may rely on the fact that these calls happen regularly
	 * @param e The {@link ActionEvent} passed by the {@link Timer}.
	 */
	private void logicTickHandler(ActionEvent e){
		tick++;
		//ALL THE LOGIC
	}
	
	/**
	 * The handler for the logic timer. This method is called $FPS times per second.
	 * This method should never be called manually; this would cause unnessecary repaints
	 * @param e The {@link ActionEvent} passed by the {@link Timer}.
	 */
	private void frameTickHandler(ActionEvent e){
		panel.repaint();
	}
	
	/**
	 * Callback for {@link DrawPanel} instances. This method is called everytime a DrawPanel is repainted.
	 * @param g The graphics object belonging to the DrawPanel that called this method.
	 * @param width The width of the area to draw into
	 * @param height The height of the area to draw into
	 */
	void paintCallback(Graphics g, int width, int height){
		//ALL THE GRAPHICS
		if(g instanceof Graphics2D){
			activeScene.draw((Graphics2D) g, width, height);
		}else{
			g.setColor(new Color(255, 0, 255));
			g.fillRect(0, 0, width, height);
			g.setColor(Color.BLACK);
			g.drawString("Error", 10, 10);
		}
	}
	
	/**
	 * The total amount of logic ticks since the framework was started
	 * @return The amount of ticks
	 */
	public long getTotalTicks(){
		return tick;
	}
	
	/**
	 * The scene that is currently active. It can be null if the framework has not yet started
	 * @return The active scene
	 */
	public Scene getActiveScene(){
		return activeScene;
	}
	
	/**
	 * The name of the scene that is currently active. It can be null if the framework has not yet started
	 * @return The active scene name
	 */
	public String getActiveSceneName(){
		return activeScene == null ? null : activeScene.getName();
	}
	
	/**
	 * Changes the active scene
	 * @param name The name of the new Scene
	 * @throws FrameworkStateException When the framework is not running
	 * @throws InvalidSceneException When the scene was not found
	 */
	public void setScene(String name) throws FrameworkStateException, InvalidSceneException{
		if(!running) throw new FrameworkStateException("Cannot set new Scene when the framework it is not running.", false);
		Scene startScene = scenes.get(name);
		if(startScene == null){
			throw new InvalidSceneException("The scene '" + name + "' was not found." );
		}else{
			activeScene.disable();
			activeScene = startScene;
			activeScene.enable();
		}
	}
}
