package lb.simplebase.gl.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import lb.simplebase.gl.GLHandle;

public class Texture implements GLHandle{

	private final int textureHandle;
	private final int width;
	private final int height;
	
	protected Texture(int textureHandle, int width, int height) {
		this.textureHandle = textureHandle;
		this.width = width;
		this.height = height;
	}

	public void disposeTexture() {
		GL11.glDeleteTextures(textureHandle);
	}
	
	public void bindTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
	}
	
	public void bindTexture(int activeSlotNum) {
		if(activeSlotNum < 0 || activeSlotNum >= 16) throw new IllegalArgumentException("Slot number must be 0...15");
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + activeSlotNum);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
	}
	
	public static void disposeTexture(Texture...textures) {
		if(textures != null) {
			for(Texture t : textures) {
				if(t != null) t.disposeTexture();
			}
		}
	}
	
	public void disableTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public static void disableActiveTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	@Override
	public int getGLHandle() {
		return textureHandle;
	}
	
}
