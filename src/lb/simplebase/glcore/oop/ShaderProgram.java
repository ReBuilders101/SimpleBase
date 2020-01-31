package lb.simplebase.glcore.oop;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.vecmath.Matrix4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GLCapabilities;

import lb.simplebase.glcore.GLFramework;

public final class ShaderProgram implements GLHandle, GLBindable, GLDisposable {

	private final Map<String, Integer> uniforms;
	
	private final int handle;
	private final Runnable task;
	private ShaderProgram(int handle, Runnable task, Map<String, Integer> uniforms) {
		this.handle = handle;
		this.task = task;
		this.uniforms = uniforms;
		GLDisposable.registerTask(this);
	}
	
	public static ShaderProgram.Builder builder() {
		return new Builder();
	}
	
	@Override
	public void enable() {
		GL20.glUseProgram(handle);
	}
	
	public void setUniformValue_int(String name, int value) {
		setUniformValue_int(findUniformLocation(name), value);
	}
	
	public void setUniformValue_int(int location, int value) {
		GL20.glUniform1i(location, value);
	}
	
	public void setUniformValue_float(String name, float value) {
		setUniformValue_float(findUniformLocation(name), value);
	}
	
	public void setUniformValue_float(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	public void setUniformValue_vec4f(String name, float...values) {
		setUniformValue_vec4f(findUniformLocation(name), values);
	}
	
	public void setUniformValue_vec4f(int location, float...values) {
		assert values.length == 4;
		GL20.glUniform4fv(location, values);
	}
	
	public void setUniformValue_mat4f(String name, float...values) {
		setUniformValue_mat4f(findUniformLocation(name), values);
	}
	
	public void setUniformValue_mat4f(int location, float...values) {
		assert values.length == 16;
		GL20.glUniformMatrix4fv(location, false, values);
	}
	
	public void setUniformValue_mat4f(String name, Matrix4f value) {
		setUniformValue_mat4f(findUniformLocation(name), value);
	}
	
	public void setUniformValue_mat4f(int location, Matrix4f value) {
		GL20.glUniformMatrix4fv(location, true, new float[] {//OpenGL uses column-major -> transpose
				value.m00, value.m01, value.m02, value.m03,
				value.m10, value.m11, value.m12, value.m13,
				value.m20, value.m21, value.m22, value.m23,
				value.m30, value.m31, value.m32, value.m33,});
	}
	
	public int findUniformLocation(String name) {
		Integer id = uniforms.get(name);
		if(id == null) throw new IllegalArgumentException("Invalid uniform name: " + name);
		return id;
	}
	
	public OptionalInt tryFindUniformLocation(String name) {
		Integer id = uniforms.get(name);
		if(id == null) return OptionalInt.empty();
		return OptionalInt.of(id);
	}
	
	@Override
	public void disable() {
		GL20.glUseProgram(0);
	}
	
	@Override
	public int getGLHandle() {
		return handle;
	}
	
	@Override
	public Runnable getDisposeAction() {
		return task;
	}
	
	public static ShaderProgram emptyShader() {
		return new ShaderProgram(0, () -> {}, new HashMap<>());
	}
	
	public static final class Builder {
		
		private static final int SHADER_BUFFER_SIZE = 5;
		
		private final int programHandle;
		private IntBuffer shaders;
		private boolean locked;
		private final Set<String> uniformNames;
		
		private Builder() {
			this.programHandle = GL20.glCreateProgram();
			this.shaders = IntBuffer.allocate(SHADER_BUFFER_SIZE); //There are 5 shader types, so 5 will be the default max
			this.uniformNames = new HashSet<>();
			this.locked = false;
//			assert !shaders.isDirect(); //debug only
		}
		
		private void assertWritable() {
			if(shaders.remaining() == 0) {
				final IntBuffer shaders2 = IntBuffer.allocate(shaders.capacity() + SHADER_BUFFER_SIZE);
				shaders2.put(shaders);
				shaders = shaders2;
			}
		}
		
		public Builder attachShader(int handle) {
			if(locked) throw new IllegalStateException("This builder has already created a ShaderProgram");
			
			GL20.glAttachShader(programHandle, handle);
			assertWritable();
			shaders.put(handle);
			return this;
		}
		
		public Builder attachShaderFromSource(ShaderType type, String shaderSource) throws ShaderCompilationException {
			Objects.requireNonNull(shaderSource, "The shader source code must not be null");
			Objects.requireNonNull(type, "The shader type must not be null");
			if(!type.isSupported()) throw new IllegalArgumentException("Shader type must be supported by the current GL context (use ShaderType.isSupported)");
			if(locked) throw new IllegalStateException("This builder has already created a ShaderProgram");
			
			final int shaderHandle = GL20.glCreateShader(type.getGLHandle()); //Create the shader of a type
			GL20.glShaderSource(shaderHandle, shaderSource); //Set the source code for this handle
			GL20.glCompileShader(shaderHandle); //Compile the shader code
			
			if(GL20.glGetShaderi(shaderHandle, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) { //Compile error
				throw new ShaderCompilationException("Error creating shader\n" + 
						GL20.glGetShaderInfoLog(shaderHandle, GL20.glGetShaderi(shaderHandle, GL20.GL_INFO_LOG_LENGTH)),
						shaderSource);
			}
			//Attach the shader to the program
			return attachShader(shaderHandle);
		}
		
		public Builder registerUniform(String name) {
			uniformNames.add(name);
			return this;
		}
		
		public ShaderProgram build() {
			if(locked) throw new IllegalStateException("This builder has already created a ShaderProgram");
			
			GL20.glLinkProgram(programHandle);
			return new ShaderProgram(programHandle, () -> {
				for(int i = 0; i < shaders.position(); i++) {
					GL20.glDetachShader(programHandle, shaders.get(i));
					GL20.glDeleteShader(shaders.get(i));
				}
				GL20.glDeleteProgram(programHandle);
			}, uniformNames.stream().collect(Collectors.toMap(Function.identity(),
					name -> Integer.valueOf(GL20.glGetUniformLocation(programHandle, name)))));
		}
	}
	
	public static enum ShaderType implements GLHandle {
		FRAGMENT(GL20.GL_FRAGMENT_SHADER, gl -> gl.OpenGL20),
		VERTEX(GL20.GL_VERTEX_SHADER, gl -> gl.OpenGL20),
		GEOMETRY(GL32.GL_GEOMETRY_SHADER, gl -> gl.OpenGL32),
		TESSELATION_CONTROL(GL40.GL_TESS_CONTROL_SHADER, gl -> gl.OpenGL40),
		TESSELATION_EVALUATION(GL40.GL_TESS_EVALUATION_SHADER, gl -> gl.OpenGL40);

		private final int handle;
		private final Predicate<GLCapabilities> valid;
		private ShaderType(int handle, Predicate<GLCapabilities> versionCheck) {
			this.handle = handle;
			this.valid = versionCheck;
		}
		
		@Override
		public int getGLHandle() {
			return handle;
		}
		
		public boolean isSupported() {
			return valid.test(GLFramework.gfGetGLCapabilities());
		}
	}
	
	public static final class ShaderCompilationException extends RuntimeException {
		
		private static final long serialVersionUID = 5225890773147361303L;
		private final String source;
		
		public ShaderCompilationException(String message) {
			this(message, null);
		}
		
		public ShaderCompilationException(String message, String shaderSourceCode) {
			super(message);
			this.source = shaderSourceCode;
		}
		
		public String getShaderSourceCode() {
			return source;
		}
	}
}
