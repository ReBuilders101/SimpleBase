package lb.simplebase.scene;

import java.awt.Graphics2D;

public abstract class SceneContainer extends Scene{
	
	private Scene[] subscenes;
	
	public Scene[] getSubscenes(){
		return subscenes;
	}
	
	public abstract void drawContainer(Graphics2D g);
	public abstract void tickContainer();
	
	@Override
	public void draw(Graphics2D g){
		drawContainer(g);
		for(Scene s : subscenes){
			s.draw(g);
		}
	}
	
	@Override
	public void tick(){
		tickContainer();
		for(Scene s : subscenes){
			s.tick();
		}
	}
}
