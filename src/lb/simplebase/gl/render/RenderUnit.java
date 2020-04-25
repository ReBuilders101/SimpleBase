package lb.simplebase.gl.render;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import lb.simplebase.gl.shader.ShaderProgram;

public class RenderUnit {

	private final RenderContext context;
	private ShaderProgram shader;
	
	private final Map<String, AssignedTexture> textureMap;
	private final Token16 textureSlots;
	
	private RenderUnit(RenderContext context, ShaderProgram shader) {
		this.context = context;
		this.shader = shader;
		this.textureSlots = new Token16();
		this.textureMap = new HashMap<>();
	}

	public void begin() {
		shader.useProgram();
		for(Entry<String, AssignedTexture> texture : textureMap.entrySet()) {
			AssignedTexture ass = texture.getValue();
			ass.texture.bindTexture(ass.slot);
			shader.setUniformValue_int(ass.uniform, ass.slot);
		}
	}
	
	public void render() {
		context.bind();
		context.draw();
	}
	
	public void renderEnd() {
		render();
		end();
	}
	
	public void end() {
		Texture.disableActiveTexture();
		shader.disableProgram();
	}
	
	public void renderComplete() {
		begin();
		render();
		end();
	}
	
	//Needs: texture -> id
	//Uniform -> id
	
	public void assignTexture(String shaderUniformName, Texture instance) {
		Objects.requireNonNull(shaderUniformName, "Uniform name can't be null");
		Objects.requireNonNull(instance, "Texture can't be null");
		int uniformLocation = shader.findUniformLocation(shaderUniformName);
		int slot = textureSlots.get();
		if(slot == -1) throw new RuntimeException("Out of texture slots (16 max)");
		AssignedTexture ass = new AssignedTexture(instance, slot, uniformLocation);
		System.out.println("Setting texture: " + ass);
		textureMap.put(shaderUniformName, ass);
	}
	
	public Texture getTexture(String shaderUniformName) {
		Objects.requireNonNull(shaderUniformName, "Uniform name can't be null");
		AssignedTexture ass = textureMap.get(shaderUniformName);
		if(ass == null) return null;
		return ass.texture;
	}
	
	public Texture removeTexture(String shaderUniformName) {
		Objects.requireNonNull(shaderUniformName, "Uniform name can't be null");
		AssignedTexture ass = textureMap.remove(shaderUniformName);
		if(ass == null) return null;
		textureSlots.put(ass.slot);
		return ass.texture;
	}
	
	public void clearTextures() {
		textureMap.clear();
		textureSlots.reset();
	}
	
	public RenderContext getContext() {
		return context;
	}
	
	public ShaderProgram getShader() {
		return shader;
	}
	
	public void setShader(ShaderProgram shader) {
		Objects.requireNonNull(shader, "Shader can't be null");
		this.shader = shader;
	}
	
	public static RenderUnit of(RenderContext context, ShaderProgram shader) {
		Objects.requireNonNull(shader, "Shader can't be null");
		return new RenderUnit(context, shader);
	}
	
	private static final class AssignedTexture {
		private Texture texture;
		private int slot;
		private int uniform;
		
		@Override
		public String toString() {
			return "AssignedTexture [texture=" + texture + ", slot=" + slot + ", uniform=" + uniform + "]";
		}

		private AssignedTexture(Texture texture, int slot, int uniform) {
			this.texture = texture;
			this.slot = slot;
			this.uniform = uniform;
		}
	}
	
	private static final class Token16 {
		private final boolean[] data = new boolean[16];
		
		private Token16() {
			reset();
		}
		
		public int get() {
			for(int i = 0; i < data.length; i++) {
				if(data[i]) {
					data[i] = false;
					return i;
				}
			}
			return -1;
		}
		
		public void put(int value) {
			data[value] = true;
		}
		
		public void reset() {
			Arrays.fill(data, true);
		}
	}
	
}
