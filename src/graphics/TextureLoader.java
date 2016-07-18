/*
 * Copyright (c) 2002-2010 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package graphics;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

public abstract class TextureLoader {
	/** The table of textures that have been loaded in this loader */
	protected HashMap<String, Texture> table = new HashMap<String, Texture>();

	/**
	 * Create a new texture loader based on the game panel
	 */
	protected TextureLoader() {

	}

	/**
	 * Create a new texture ID
	 * 
	 * @return A new texture ID
	 */
	protected abstract int createTextureID();

	/**
	 * Load a texture
	 * 
	 * @param resourceName
	 *            The location of the resource to load
	 * @return The loaded texture
	 * @throws IOException
	 *             Indicates a failure to access the resource
	 * @throws LWJGLException 
	 */
	public Texture getTexture(String resourceName) throws IOException {
		Texture tex = table.get(resourceName);

		if (tex != null) {
			return tex;
		}

		BufferedImage bufferedImage = loadImage(resourceName);
		tex = getTexture(bufferedImage);

		table.put(resourceName, tex);
		
		return tex;
	}

	public abstract Texture getTexture(BufferedImage bufferedImage);

	/**
	 * Get the closest greater power of 2 to the fold number
	 * 
	 * @param fold
	 *            The target number
	 * @return The power of 2
	 */
	protected int get2Fold(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}

	/**
	 * Load a given resource as a buffered image
	 * 
	 * @param ref The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException
	 *             Indicates a failure to find a resource
	 */
	protected BufferedImage loadImage(String ref) throws IOException {
		URL url = TextureLoader.class.getClassLoader().getResource(ref);
		System.out.println("Tryload " + url.toString());
		Image img = new ImageIcon(url).getImage();
		BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bufferedImage;
	}
}
