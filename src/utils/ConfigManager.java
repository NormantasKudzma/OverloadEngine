package utils;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import org.json.JSONObject;
import org.lwjgl.BufferUtils;

import sun.misc.IOUtils;

public class ConfigManager {
	private static String commentDelim = "#";
	private static String kvDelim = "=";

	public static ArrayList<String> loadFileLines(String path){
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			System.out.println("Tryload " + url.toString());
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			ArrayList<String> lines = new ArrayList<String>();
			String line;
			
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			
			return lines;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Config<String, String> loadConfigAsPairs(String path) {
		return loadConfigAsPairs(path, false);
	}

	public static Config<String, String> loadConfigAsPairs(String path, boolean isHeaderUnique) {
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			System.out.println("Tryload " + url.toString());
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			Config<String, String> cfg = new Config<String, String>();
			String line;
			String[] params;

			while ((line = br.readLine()) != null) {
				if (line.isEmpty() || line.startsWith(commentDelim)) {
					continue;
				}

				params = line.split(kvDelim);
				// Discard mangled lines
				if (params.length != 2) {
					continue;
				}

				if (isHeaderUnique) {
					isHeaderUnique = false;
					cfg.firstLine = new Pair<Object, Object>(params[0], params[1]);
					continue;
				}

				cfg.contents.add(new Pair<String, String>(params[0], params[1]));
			}
			br.close();
			return cfg;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject loadConfigAsJson(String path) {
		try {
			if (!path.endsWith(".json")) {
				path += ".json";
			}
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			System.out.println("Tryload " + url.toString());
			byte buffer[] = IOUtils.readFully(url.openStream(), -1, true);

			String contents = new String(buffer);
			JSONObject obj = new JSONObject(contents);
			return obj;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		File file = new File(resource);
		if (file.isFile()) {
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();

			buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);

			while (fc.read(buffer) != -1)
				;

			fis.close();
			fc.close();
		}
		else {
			buffer = BufferUtils.createByteBuffer(bufferSize);

			InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
			if (source == null){
				return null;
			}

			try {
				ReadableByteChannel rbc = Channels.newChannel(source);
				try {
					while (true) {
						int bytes = rbc.read(buffer);
						if (bytes == -1)
							break;
						if (buffer.remaining() == 0)
							buffer = resizeBuffer(buffer, buffer.capacity() * 2);
					}
				}
				finally {
					rbc.close();
				}
			}
			finally {
				source.close();
			}
		}

		buffer.flip();
		return buffer;
	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	public static Font loadFont(String path, int size) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			Font font = Font.createFont(Font.TRUETYPE_FONT, url.openStream()).deriveFont(size);
			ge.registerFont(font);
			return font;
		}
		catch (Exception e) {
			return null;
		}
	}
}
