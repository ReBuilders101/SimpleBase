package lb.simplebase.gl.shader;

import java.io.IOException;
import lb.simplebase.util.TextFileLoader;

/**
 * This class provides utility methods to load shaders from files and add them to your OpenGL program.
 * Use {@link TextFileLoader} to load any text file as shader source code
 */
public final class ShaderLoader {

	public static final String DEFAULT_VERTEX_SHADER = "lb/simplebase/gl/shader/DefaultVertexShader.glsl";
	public static final String DEFAULT_FRAGMENT_SHADER = "lb/simplebase/gl/shader/DefaultFragmentShader.glsl";

	private ShaderLoader() {}

	public static String getDefaultVertexSource() {
		try {
			return TextFileLoader.slReadFromResource(DEFAULT_VERTEX_SHADER);
		} catch (IOException e) {
			throw new RuntimeException("Could not load default Vertex Shader at " + DEFAULT_VERTEX_SHADER, e);
		}
	}

	public static String getDefaultFragmentSource() {
		try {
			return TextFileLoader.slReadFromResource(DEFAULT_FRAGMENT_SHADER);
		} catch (IOException e) {
			throw new RuntimeException("Could not load default Fragment Shader at " + DEFAULT_VERTEX_SHADER, e);
		}
	}
}
