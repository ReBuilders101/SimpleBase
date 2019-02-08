package lb.simplebase.core;

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
	 * Loads a shader from a file and attaches it to a program.
	 * @param name The name of the shader, as a path that can be used in {@link #slReadFromFile(String)}.
	 * @param programHandle The handle of the program that this shader should be attached to
	 * @param shaderType The type of shader. This parameter can be one of the possible values for {@link GL20#glCreateShader(int)}
	 * @throws RuntimeException If the shader could not be compiled
	 * @return The handle of the created shader object
	 */
	public static int slAattachShader(String name, int programHandle, int shaderType) {
		final String shaderSource = slReadFromFile(name); //Read the shader source code
		final int shaderHandle = glCreateShader(shaderType); //Create the shader of a type
		glShaderSource(shaderHandle, shaderSource); //Set the source code for this handle
		glCompileShader(shaderHandle); //Compile the shader code
		
		if(glGetShaderi(shaderHandle, GL_COMPILE_STATUS) == GL_FALSE) { //Compile error
			throw new RuntimeException("Error creating shader\n" + glGetShaderInfoLog(shaderHandle, glGetShaderi(shaderHandle, GL_INFO_LOG_LENGTH)));
		}
		//Attach the shader to the program
		glAttachShader(programHandle, shaderHandle);
		//return the handle, in case it is needed
		return shaderHandle;
	}	
	
}
