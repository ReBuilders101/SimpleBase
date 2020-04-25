package lb.simplebase.gl.shader;

import java.io.FileNotFoundException;
import java.io.IOException;
import lb.simplebase.util.TextFileLoader;

/**
 * This class provides utility methods to load shaders from files and add them to your OpenGL program.
 * Use {@link TextFileLoader} to load any text file as shader source code
 */
public final class ShaderLoader {

	public static final String DEFAULT_VERTEX_SHADER = "lb/simplebase/gl/shader/DefaultVertexShader.glsl";
	public static final String DEFAULT_FRAGMENT_SHADER = "lb/simplebase/gl/shader/DefaultFragmentShader.glsl";
	public static final String DEFAULT_FXAA_SHADER = "lb/simplebase/gl/shader/FXAAFragmentShader.glsl";

	private ShaderLoader() {}

	public static String safeReadFromJarResource(String name) {
		return TextFileLoader.safeReadFromResource(name);
	}
	
	public static String readFromJarResource(String name) throws FileNotFoundException, IOException {
		return TextFileLoader.readFromResource(name);
	}
	
	public static String getDefaultVertexSource() {
		try {
			return TextFileLoader.readFromResource(DEFAULT_VERTEX_SHADER);
		} catch (IOException e) {
			throw new RuntimeException("Could not load default Vertex Shader at " + DEFAULT_VERTEX_SHADER, e);
		}
	}

	public static String getDefaultFragmentSource() {
		try {
			return TextFileLoader.readFromResource(DEFAULT_FRAGMENT_SHADER);
		} catch (IOException e) {
			throw new RuntimeException("Could not load default Fragment Shader at " + DEFAULT_FRAGMENT_SHADER, e);
		}
	}
	
	/**
	 * Loads a fragment shader that does FXAA antialiasing.<br>
	 * <b>Source: <a href="https://github.com/McNopper/OpenGL/blob/master/Example42/shader/fxaa.frag.glsl">
	 * https://github.com/McNopper/OpenGL/blob/master/Example42/shader/fxaa.frag.glsl</a></b>
	 * <p>
	 * Provides these uniforms
	 * <ul>
	 * <li>u_colorTexture 	[sampler2D]: The texture to use for sampling</li>
	 * <li>u_texelStep 		[vec2]: IDK</li>
	 * <li>u_showEdges 		[int]: Debug option. Highlights antialiased edges red when enabled</li>
	 * <li>u_fxaaOn 		[int]: Toggles FXAA. If disabled, texel will be used directly without any antialiasing</li>
	 * <li>u_lumaThreshold 	[float]: Threshold used to decide whether an edge needs antialiasing</li>
	 * <li>u_mulReduce 		[float]: IDK</li>
	 * <li>u_minReduce 		[float]: IDK</li>
	 * <li>u_maxSpan 		[float]: IDK</li>
	 * </ul>
	 * </p>
	 * @param textureCoordsUniformName_vec2 The name of the texture coordinate variable (from the previous shader stage)
	 * @return The source code for a FXAA Fragment shader, GLSL version 330 core
	 */
	public static String getFXAAFragmentSource(String textureCoordsUniformName_vec2) {
		try {
			String template = TextFileLoader.readFromResource(DEFAULT_FXAA_SHADER);
			return template.replaceFirst("TEXCOORD", textureCoordsUniformName_vec2);
		} catch (IOException e) {
			throw new RuntimeException("Could not load default Fragment Shader at " + DEFAULT_FXAA_SHADER, e);
		}
	}
}
