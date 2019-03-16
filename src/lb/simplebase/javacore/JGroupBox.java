package lb.simplebase.javacore;

import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class JGroupBox extends JPanel{
	private static final long serialVersionUID = 5369906220444795022L;

	private String borderTitle;
	
	public JGroupBox(String title) {
		super();
		setBorder(BorderFactory.createTitledBorder(title));
		borderTitle = title;
	}

	public JGroupBox(String title, boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		setBorder(BorderFactory.createTitledBorder(title));
		borderTitle = title;
	}

	public JGroupBox(String title, LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		setBorder(BorderFactory.createTitledBorder(title));
		borderTitle = title;
	}

	public JGroupBox(String title, LayoutManager layout) {
		super(layout);
		setBorder(BorderFactory.createTitledBorder(title));
		borderTitle = title;
	}

	public void setBorderKeepTitle(Border border) {
		super.setBorder(BorderFactory.createTitledBorder(border, borderTitle));
	}
	
	public String getTitle() {
		return borderTitle;
	}
}
