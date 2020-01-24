package lb.simplebase.glcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL11.GL_FALSE;

/**
 * This class provides utility methods to load shaders from files and add them to your OpenGL program
 */
public final class GLShaderLoader {

	public static final String DEFAULT_VERTEX_SHADER = "lb/simplebase/glcore/shaders/DefaultVertexShader.glsl";
	public static final String DEFAULT_FRAGMENT_SHADER = "lb/simplebase/glcore/shaders/DefaultFragmentShader.glsl";

	private GLShaderLoader() {}

	/**
	 * Reads a file into a single String. The file is opened using the <code>name</code> parameter with
	 * {@link ClassLoader#getResourceAsStream(String)}.
	 * @param name The name of the file or resource
	 * @return The content as one single string
	 */
	public static String slReadFromResource(String name) throws IOException {
		Objects.requireNonNull(name, "Resource name must not be null");
		try (InputStream resource = GLShaderLoader.class.getClassLoader().getResourceAsStream(name)) {
			if(resource == null) throw new FileNotFoundException("Error loading resource: " + name);
			return slReadFromStream(resource);
		}
	}

	public static String slReadFromFile(File file) throws IOException {
		Objects.requireNonNull(file, "File name must not be null");
		try (InputStream resource = new FileInputStream(file)) {
			return slReadFromStream(resource);
		}
	}

	public static String slReadFromPath(Path path) throws IOException {
		Objects.requireNonNull(path, "File path must not be null");
		return slReadFromStream(Files.newInputStream(path, StandardOpenOption.READ));
	}

	public static String slReadFromStream(InputStream stream) throws IOException {
		Objects.requireNonNull(stream, "Stream must not be null");
		try (InputStreamReader reader = new InputStreamReader(stream))	{
			StringBuilder code = new StringBuilder();
			int current;
			while((current = reader.read()) != -1) {
				code.append((char) current);
			}
			return code.toString();
		}
	}

	public static String slDefaultVertexSource() {
		try {
			return slReadFromResource(DEFAULT_VERTEX_SHADER);
		} catch (IOException e) {
			throw new RuntimeException("Could not load default Vertex Shader at " + DEFAULT_VERTEX_SHADER, e);
		}
	}

	public static String slDefaultFragmentSource() {
		try {
			return slReadFromResource(DEFAULT_FRAGMENT_SHADER);
		} catch (IOException e) {
			throw new RuntimeException("Could not load default Fragment Shader at " + DEFAULT_VERTEX_SHADER, e);
		}
	}

	/**
	 * Links a program and adds a task to delete the program when the application is terminated.
	 * @param program The program to link
	 */
	@Deprecated
	public static void slLinkProgram(int program) {
		GL20.glLinkProgram(program);
		GLFramework.gfAddTerminateTask(() -> GL20.glDeleteProgram(program));
	}

	/**
	 * Loads a shader from a file and attaches it to a program. The shaders will be automatically destroyed and detached when the
	 * program is terminated
	 * @param name The name of the shader, as a path that can be used in {@link #slReadFromResource(String)}.
	 * @param programHandle The handle of the program that this shader should be attached to
	 * @param shaderType The type of shader. This parameter can be one of the possible values for {@link GL20#glCreateShader(int)}
	 * @throws IOException 
	 * @throws RuntimeException If the shader could not be compiled
	 */
	@Deprecated
	public static void slAttachShader(String name, int programHandle, int shaderType) throws IOException {
		final String shaderSource = slReadFromResource(name); //Read the shader source code
		final int shaderHandle = glCreateShader(shaderType); //Create the shader of a type
		glShaderSource(shaderHandle, shaderSource); //Set the source code for this handle
		glCompileShader(shaderHandle); //Compile the shader code

		if(glGetShaderi(shaderHandle, GL_COMPILE_STATUS) == GL_FALSE) { //Compile error
			throw new RuntimeException("Error creating shader\n" + glGetShaderInfoLog(shaderHandle, glGetShaderi(shaderHandle, GL_INFO_LOG_LENGTH)));
		}
		//Attach the shader to the program
		glAttachShader(programHandle, shaderHandle);

		GLFramework.gfAddTerminateTask(() -> GL20.glDetachShader(programHandle, shaderHandle));
		GLFramework.gfAddTerminateTask(() -> GL20.glDeleteShader(shaderHandle)); 
	}	

	/**
	 * Creates a default shader program that uses {@link #DEFAULT_VERTEX_SHADER} and {@link #DEFAULT_FRAGMENT_SHADER} as shaders.<br>
	 * Every call generates a new program.<br>
	 * Program and shaders are automatically cleaned up.
	 * @return A default shader program
	 * @throws IOException 
	 */
	@Deprecated
	public static int slDefaultProgram() throws IOException {
		final int program = GL20.glCreateProgram();
		slAttachShader(DEFAULT_VERTEX_SHADER, program, GL20.GL_VERTEX_SHADER);
		slAttachShader(DEFAULT_FRAGMENT_SHADER, program, GL20.GL_FRAGMENT_SHADER);
		slLinkProgram(program);
		return program;
	}

	/**
	 * Creates a shader program that uses the vertex and fragment shaders in the parameters.<br>
	 * Every call generates a new program.<br>
	 * Program and shaders are automatically cleaned up.
	 * @return A default shader program
	 * @throws IOException 
	 */
	@Deprecated
	public static int slBasicProgram(String vertexShader, String fragmentShader) throws IOException {
		final int program = GL20.glCreateProgram();
		slAttachShader(vertexShader, program, GL20.GL_VERTEX_SHADER);
		slAttachShader(fragmentShader, program, GL20.GL_FRAGMENT_SHADER);
		slLinkProgram(program);
		return program;
	}

}
