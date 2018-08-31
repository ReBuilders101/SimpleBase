package lb.simplebase.core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * @version 1.1
 * @author LB
 * The main class of the framework project. Only one instance of this class should exist, and it is obtainable
 * through the static method {@link #getFramework()}. If other instances are created through reflection, the program may break.
 * The framework has a basic lifecycle:
 * <br>1. {@link #setup()}: used for setting window size, tickrate and other properties that can not be altered after starting the framework
 * <br>2. {@link #start()}: starts the timers and displays the application window(s) if desired. This should be the last call in the main method.
 * <br>3. {@link #stop()}: stops the timers and exits the application. This method never returns.
 */
public final class Framework {

	private static final Framework INSTANCE = new Framework();

	private boolean running;
	private long tick;
	private Timer logicTick;
	private Timer frameTick;
	private JFrame mainFrame;
	private JFrame optionFrame;
	private DrawPanel panel;
	
	/**
	 * Private constructor. Creates a new uninitialized instance.
	 */
	private Framework(){
		running = false;
	}
	
	/**
	 * Gets the single instance of this class.
	 * @return the {@link Framework} instance
	 */
	public static Framework getFramework(){
		return INSTANCE;
	}
	
	/**
	 * Set up the framework before starting it.
	 * @param fps The amount of frames per second
	 * @param tps The amount of ticks per second
	 * @param title The title of the Frame. If a option frame is enabled, its title will be '$TITLE - Options'.
	 * @param initialFrameSize The starting frame size for the main frame
	 * @param optionFrame If an option frame should be shown
	 * @param centerFrame If the main frame should be centered on the screen
	 * @throws FrameworkStateException When the framework is already running
	 */
	public void setup(int fps, int tps, String title, Dimension initialFrameSize, boolean optionFrame, boolean centerFrame)
			throws FrameworkStateException{
		if(running) throw new FrameworkStateException("Cannot setup the framework while it is already running.", true);
		tick = 0;
		logicTick = new Timer(1000 / tps, Framework.getFramework()::logicTickHandler);
		frameTick = new Timer(1000 / fps, Framework.getFramework()::frameTickHandler);
		mainFrame = new JFrame(title);
		mainFrame.setSize(initialFrameSize);
		panel = new DrawPanel();
		mainFrame.setContentPane(panel);
		if(centerFrame){
			mainFrame.setLocationRelativeTo(null);
		}
		if(optionFrame){
			this.optionFrame = new JFrame(title + " - Options");
		}
	}
	
	/**
	 * Starts the framework and the timers for logic and rendering
	 * @param showFrames If true, the main and oftion frame (if enabled) will be shown
	 * @throws FrameworkStateException When the framework is already running
	 */
	public void start(boolean showFrames) throws FrameworkStateException{
		if(running) throw new FrameworkStateException("Cannot (re)start the framework while it is already running.", true);
		if(showFrames){
			mainFrame.setVisible(true);
			if(optionFrame != null) optionFrame.setVisible(true);
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
		running = false;
		System.exit(status);
	}
	
	/**
	 * The handler for the logic timer. This method is called $TPS times per second.
	 * This method should never be called manually, because scenes may rely on the fact that these calls happen regularly
	 * @param e The {@link ActionEvent} passed by the timer.
	 */
	private void logicTickHandler(ActionEvent e){
		tick++;
		//ALL THE LOGIC
	}
	
	private void frameTickHandler(ActionEvent e){
		panel.repaint();
	}
	
	/**
	 * Callback for {@link DrawPanel} instances. This method is called everytime a DrawPanel is repainted.
	 * @param g The graphics object belonging to the DrawPanel that called this method.
	 */
	void paintCallback(Graphics g){
		//ALL THE GRAPHICS
	}
	
	/**
	 * The total amount of logic ticks since the framework was started
	 * @return The amount of ticks
	 */
	public long getTotalTicks(){
		return tick;
	}
	
}
