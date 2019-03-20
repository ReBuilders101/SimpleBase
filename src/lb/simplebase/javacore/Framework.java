package lb.simplebase.javacore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lb.simplebase.core.AnyState;
import lb.simplebase.core.FrameworkState;
import lb.simplebase.core.RequireState;

public final class Framework {

	public static final String DEFAULT_SCENE_NAME = "default";
	
	private Framework() {};
	
	private static FrameworkState state = FrameworkState.UNINITIALIZED;
	private static boolean exitOnStop = true;
	private static Scene currentScene;
	private static Scene previewScene;
	
	private static Map<String, Scene> scenes;
	private static Map<String, JComponent> scenePanels;
	private static List<JButton> previewButtons;
	private static JFrame mainFrame;
	private static JFrame smallFrame;
	private static JPanel sceneOptions;
	private static JPanel scenePanel;
	private static JLabel infoLabel;
	
	private static JButton pnext;
	private static JButton pprev;
	
	private static DrawCallbackPanel smallDcp;
	private static DrawCallbackPanel mainDcp;
	
	private static int tps;
	
	private static long tick;
	
	private static Timer tickTimer;
	
	@RequireState(FrameworkState.UNINITIALIZED)
	public static void init() {
		//Validate state
		if(getState() != FrameworkState.UNINITIALIZED) return;
		scenes = new LinkedHashMap<>(); //To preserve order
		scenePanels = new LinkedHashMap<>();
		previewButtons = new ArrayList<>();
		infoLabel = new JLabel("Info");
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
		JPanel topControls = new JPanel(new GridBagLayout()); //will center components
		JPanel topButtons = new JPanel();
		JButton prev = new JButton("<< Set Previous");
		JButton next = new JButton("Set Next >>");
		pprev = new JButton("<< Preview Previous");
		pnext = new JButton("Preview Next >>");
		prev.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		next.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		pprev.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		pnext.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		prev.addActionListener((e) -> setActiveScene(currentScene.getPreviousSceneOptional().orElse(currentScene).getName()));
		next.addActionListener((e) -> setActiveScene(currentScene.getNextSceneOptional().orElse(currentScene).getName()));
		pprev.addActionListener((e) -> handlePreviewPress(pprev, currentScene.getPreviousSceneOptional().orElse(currentScene), "<< Normal View", "<< Preview Previous"));
		pprev.addActionListener((e) -> handlePreviewPress(pprev, currentScene.getPreviousSceneOptional().orElse(currentScene), "Normal View >>", "Preview Next >>"));
		topButtons.add(prev);
		topButtons.add(pprev);
		topButtons.add(pnext);
		topButtons.add(next);
		topControls.add(topButtons);
		generalOptions.setLayout(new BorderLayout());
		generalOptions.add(topControls, BorderLayout.NORTH);
		scenePanel = new JPanel();
		JScrollPane sceneListPane = new JScrollPane(scenePanel);
		generalOptions.add(sceneListPane, BorderLayout.CENTER);
		generalOptions.add(infoLabel, BorderLayout.SOUTH);
		leftSide.add(generalOptions);
		smallFrame.add(leftSide);
		sceneOptions = new JGroupBox("Scene Options");
		smallFrame.add(sceneOptions);
		
		//Init timers
		tickTimer = new Timer("TickTimerThread", true);
		//Tasks will be set in start()
		//Set variables
		tps = 30;
		tick = 0;
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
		case INITIALIZED:
			//Dispose frames
			mainFrame.dispose();
			smallFrame.dispose();
		case UNINITIALIZED:
			if(exitOnStop) System.exit(0);
		}
		state = FrameworkState.ENDED;
	}
	
	@RequireState(FrameworkState.STARTED)
	public static void displayInformation(String text) {
		if(getState() != FrameworkState.STARTED) return;
		infoLabel.setText(text);
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void start() {
		if(getState() != FrameworkState.INITIALIZED) return;
//		canvas = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		//Create scene list
		scenePanel.setLayout(new BoxLayout(scenePanel, BoxLayout.Y_AXIS));
		int index = 1;
		for(Scene scene : scenes.values()) {
			final JComponent sp = createComponent(scene, index++); 
			scenePanel.add(sp);
			scenePanels.put(scene.getName(), sp);
		}
		scenePanel.add(Box.createHorizontalGlue());
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
		//Set current scene
		currentScene = scenes.get(DEFAULT_SCENE_NAME);
		previewScene = currentScene;
		scenePanels.get(currentScene.getName()).setBorder(BorderFactory.createLineBorder(Color.BLUE));
		currentScene.setActive(true);
		state = FrameworkState.STARTED;
	}
	
	private static JComponent createComponent(Scene value, int index) {
		JPanel ret = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel();
		JButton set = new JButton("Set active");
		JButton pre = new JButton("Preview");
		set.addActionListener((e) -> Framework.handleActivatePress(set, value));
		pre.addActionListener((e) -> Framework.handlePreviewPress(pre, value, "Normal View", "Preview"));
		previewButtons.add(pre);
		buttons.add(pre);
		buttons.add(set);
		ret.add(buttons, BorderLayout.EAST);
		JLabel idx = new JLabel(String.format("%02d", index));
		idx.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		ret.add(idx, BorderLayout.WEST);
		JLabel main = new JLabel(value.getName());
		main.setToolTipText(value.getDescription());
		ret.add(main, BorderLayout.CENTER);
		if(value.isActive()) {
			ret.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		} else {
			ret.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		}
		ret.setMaximumSize(new Dimension((int) ret.getMaximumSize().getWidth(), (int) ret.getPreferredSize().getHeight())); //setmaximumSize is bad, but works for boxLayout
		return ret;
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void setTitle(String title) {
		if(getState() != FrameworkState.INITIALIZED) return;
		mainFrame.setTitle(title);
		smallFrame.setTitle(title + " - Options");
	}
	
	@RequireState(FrameworkState.INITIALIZED)
	public static void setBorderlessFullscreen() {
		if(getState() != FrameworkState.INITIALIZED) return;
		mainFrame.setUndecorated(true);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		smallFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	@AnyState
	public static FrameworkState getState() {
		return state;
	}
	
	@AnyState
	public static void setExitOnStop(boolean eos) {
		exitOnStop = eos;
	}
	
	public static void setTPS(int tps) {
		Framework.tps = tps;
	}
	
	private static void handlePreviewPress(JButton button, Scene scene, String normal, String pre) {
		if(button.getText().equals(normal)) { //no pv
			previewScene = currentScene;
			button.setText(pre);
		} else {
			//reset all buttons
			previewButtons.forEach((b) -> b.setText("Preview"));
			pprev.setText("<< Preview Previous");
			pnext.setText("Preview Next >>");
			previewScene = scene;
			if(scene != currentScene) button.setText(normal);
		}

	}
	
	private static void handleActivatePress(JButton button, Scene scene) {
		if(scene != currentScene) setActiveScene(scene.getName());
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
		if(name == currentScene.getName()) {
			displayInformation("Skipped setting active scene");
			return false;
		}
		if(!scenes.containsKey(name)) return false;
		Scene requested = scenes.get(name);
		if(requested == null) return false;
		currentScene.setActive(false);
		scenePanels.get(currentScene.getName()).setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		currentScene = requested;
		previewScene = currentScene;
		scenePanels.get(currentScene.getName()).setBorder(BorderFactory.createLineBorder(Color.BLUE));
		currentScene.setActive(true);
		displayInformation("Set new active scene");
		return true;
	}
	
	@AnyState
	public static long getCurrentTick() {
		return tick;
	}
	
	private static void onMainPanelDraw(Graphics2D g, int width, int height) {
		//g.drawImage(canvas, 0, 0, null);
		if(currentScene != null) currentScene.draw(g, width, height);
	}
	
	private static void onSmallPanelDraw(Graphics2D g, int width, int height) {
		//g.drawImage(canvas, smallTransform, null);
		//g.drawImage(canvas.getScaledInstance(width, height, BufferedImage.SCALE_FAST), 0, 0, null);
		//g.drawImage(canvas, 0, 0, smallDcp.getWidth(), smallDcp.getHeight(), 0, 0, mainDcp.getWidth(), mainDcp.getHeight(), null);
		if(previewScene != null) previewScene.draw(g, width, height);
	}
	
	private static void onUpdateTask() {
		if(currentScene != null) {
//			recalculateSizes();
//			Graphics2D draw = canvas.createGraphics();
//			draw.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			currentScene.draw(draw, canvas.getWidth(), canvas.getHeight());
//			draw.dispose();
			//Draw
			mainDcp.repaint();
			smallDcp.repaint();
			currentScene.update(tick);
		}
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
