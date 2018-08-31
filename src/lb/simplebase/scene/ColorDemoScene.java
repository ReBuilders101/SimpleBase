package lb.simplebase.scene;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;

public class ColorDemoScene extends Scene{

	private Color currentColor;
	private String name;
	private JButton button;
	
	public ColorDemoScene(String name, Color initialColor) {
		currentColor = initialColor;
		this.name = name;
		button = new JButton("Change Color");
		button.addActionListener(this::changeColor);
	}
	
	@Override
	public void tick() {}

	@Override
	public void draw(Graphics2D g, int width, int height) {
		g.setColor(currentColor);
		g.fillRect(0, 0, width, height);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public JComponent getOptions() {
		return button;
	}

	@Override
	public void enable() {}

	@Override
	public void disable() {}
	
	private void changeColor(ActionEvent e){
		currentColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	}

}
