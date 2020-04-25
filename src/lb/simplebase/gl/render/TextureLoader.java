package lb.simplebase.gl.render;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import de.matthiasmann.twl.utils.PNGDecoder;
import lb.simplebase.util.TextFileLoader;

/**
 * <b>Only loads PNG files</b>
 */
public class TextureLoader {

	public static Texture safeImportFromResource(String name, TextureFormat format) {
		Objects.requireNonNull(name, "Resource name must not be null");
		try (InputStream resource = TextFileLoader.class.getClassLoader().getResourceAsStream(name)) {
			if(resource == null) throw new UncheckedIOException(new FileNotFoundException("Error loading resource: " + name));
			return importFromStream(resource, format);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static Texture importFromResource(String name, TextureFormat format) throws IOException, FileNotFoundException {
		Objects.requireNonNull(name, "Resource name must not be null");
		try (InputStream resource = TextFileLoader.class.getClassLoader().getResourceAsStream(name)) {
			if(resource == null) throw new FileNotFoundException("Error loading resource: " + name);
			return importFromStream(resource, format);
		}
	}

	public static Texture importFromFile(File file, TextureFormat format) throws IOException, FileNotFoundException {
		Objects.requireNonNull(file, "File name must not be null");
		try (InputStream resource = new FileInputStream(file)) {
			return importFromStream(resource, format);
		}
	}

	public static Texture readFromPath(Path path, TextureFormat format) throws IOException, FileNotFoundException {
		Objects.requireNonNull(path, "File path must not be null");
		return importFromStream(Files.newInputStream(path, StandardOpenOption.READ), format);
	}
	
	public static Texture importFromStream(InputStream stream, TextureFormat format) throws IOException {
		final PNGDecoder decoder = new PNGDecoder(stream);
		final int imageByteSize = 4 * decoder.getWidth() * decoder.getHeight();
		final int imageRowStride = 4 * decoder.getWidth();
		ByteBuffer imageData = null;
		final int textureHandle;
		try{
			//Manually allocate because faster, but don't put it on the stack because stack size is limited and images can be large
			imageData = MemoryUtil.memAlloc(imageByteSize);
			if(format.getFlip()) {
				decoder.decodeFlipped(imageData, imageRowStride, PNGDecoder.Format.RGBA);
			} else {
				decoder.decode(imageData, imageRowStride, PNGDecoder.Format.RGBA);
			}
			imageData.flip(); //ready for reading
			
			textureHandle = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
			//set Texture format
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1); //Pixel row data is dense/unpadded
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, format.getMagFilter());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, format.getMinFilter());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, format.getSWrap());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, format.getTWrap());
			if(format.useClamp()) { //set border color
				GL11.glTexParameterfv(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, format.getBorderColor());
			}
			
			//Now store texture data
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0,
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
			//We don't need the buffer anymore
		} finally {
			if(imageData != null) MemoryUtil.memFree(imageData);
		}
		//texture is initialized and still bound
		if(format.getMipmaps()) {
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		}
		
		//unbind
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return new Texture(textureHandle, decoder.getWidth(), decoder.getHeight());
	}
	
}

