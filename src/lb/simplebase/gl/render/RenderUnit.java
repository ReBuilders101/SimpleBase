package lb.simplebase.gl.render;

import lb.simplebase.gl.shader.ShaderProgram;

public class RenderUnit {

	private final RenderContext context;
	private ShaderProgram shader;
	private Texture[] textureMap;
	private boolean[] enabled;
	
	private RenderUnit(RenderContext context, ShaderProgram shader, Texture[] textureMap) {
		this.context = context;
		this.shader = shader;
		this.textureMap = textureMap;
	}

	public void begin() {
		shader.useProgram();
	}
	
	public void render() {
		context.draw();
	}
	
	public void end() {
		shader.disableProgram();
	}
	
	public RenderContext getContext() {
		return context;
	}
	
//	@SuppressWarnings("unchecked")
//	public <T extends RenderContext> T getContext(Class<T> contextType) {
//		return (T) context;
//	}
	
	public static RenderUnit of(RenderContext context, ShaderProgram shader, Texture...textures) {
		return new RenderUnit(context, shader, textures);
	}
	
}
