package lb.simplebase.glcore;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
	
	public static final String DEFAULT_VERTEX_SHADER = "lb/simplebase/core/DefaultVertexShader.glsl";
	public static final String DEFAULT_FRAGMENT_SHADER = "lb/simplebase/core/DefaultFragmentShader.glsl";
	
	private GLShaderLoader() {}
	
	/**
	 * Reads a file into a single String. The file is opened using the <code>name</code> parameter with
	 * {@link ClassLoader#getResourceAsStream(String)}.
	 * @param name The name of the file or resource
	 * @return The content as one single string
	 */
	public static String slReadFromFile(String name) {
		//Copied code from https://goharsha.com/lwjgl-tutorial-series/the-first-triangle/
		
		//Container for code
		StringBuilder source = new StringBuilder();
		try	{
			//Reader for file contentd
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(GLShaderLoader.class.getClassLoader().getResourceAsStream(name)));
			String line;
			while ((line = reader.readLine()) != null) { //Read single lines
				source.append(line).append("\n"); //Append them to the code
			}
			reader.close();
		}
		catch (Exception e)	{
			System.err.println("Error loading source code: " + name);
			e.printStackTrace();
		}
		//Return the code
		return source.toString();
	}
	
	/**
	 * Links a program and adds a task to delete the program when the application is terminated.
	 * @param program The program to link
	 */
	public static void slLinkProgram(int program) {
		GL20.glLinkProgram(program);
		GLFramework.gfAddTerminateTask(() -> GL20.glDeleteProgram(program));
	}
	
	/**
	 * Loads a shader from a file and attaches it to a program. The shaders will be automatically destroyed and detached when the
	 * program is terminated
	 * @param name The name of the shader, as a path that can be used in {@link #slReadFromFile(String)}.
	 * @param programHandle The handle of the program that this shader should be attached to
	 * @param shaderType The type of shader. This parameter can be one of the possible values for {@link GL20#glCreateShader(int)}
	 * @throws RuntimeException If the shader could not be compiled
	 */
	public static void slAttachShader(String name, int programHandle, int shaderType) {
		final String shaderSource = slReadFromFile(name); //Read the shader source code
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
	 */
	public static int slDefaultProgram() {
		final int program = GL20.glCreateProgram();
		slAttachShader(DEFAULT_VERTEX_SHADER, program, GL20.GL_VERTEX_SHADER);
		slAttachShader(DEFAULT_FRAGMENT_SHADER, program, GL20.GL_FRAGMENT_SHADER);
		slLinkProgram(program);
		return program;
	}
	
}
