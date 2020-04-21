package lb.simplebase.util;

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

/**
 * This class provides utility methods to load shaders from files and add them to your OpenGL program
 */
public final class TextFileLoader {
	private TextFileLoader() {}

	/**
	 * Reads a file into a single String. The file is opened using the <code>name</code> parameter with
	 * {@link ClassLoader#getResourceAsStream(String)}.
	 * @param name The name of the file or resource
	 * @return The content as one single string
	 */
	public static String readFromResource(String name) throws IOException, FileNotFoundException {
		Objects.requireNonNull(name, "Resource name must not be null");
		try (InputStream resource = TextFileLoader.class.getClassLoader().getResourceAsStream(name)) {
			if(resource == null) throw new FileNotFoundException("Error loading resource: " + name);
			return readFromStream(resource);
		}
	}

	public static String readFromFile(File file) throws IOException, FileNotFoundException {
		Objects.requireNonNull(file, "File name must not be null");
		try (InputStream resource = new FileInputStream(file)) {
			return readFromStream(resource);
		}
	}

	public static String readFromPath(Path path) throws IOException, FileNotFoundException {
		Objects.requireNonNull(path, "File path must not be null");
		return readFromStream(Files.newInputStream(path, StandardOpenOption.READ));
	}

	public static String readFromStream(InputStream stream) throws IOException {
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
}
