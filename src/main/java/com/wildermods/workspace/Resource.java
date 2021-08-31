package com.wildermods.workspace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Resource {

	private final InputStream inputstream;
	private final String resourceLocation;
	private final String destPath;
	
	public Resource(String path) {
		this(path, path);
	}
	
	public Resource(String resourceLocation, String destPath) {
		this.resourceLocation = resourceLocation;
		this.destPath = destPath;
		InputStream stream = Resource.class.getResourceAsStream(resourceLocation);
		if(stream == null) { //If we're being executed from a dir not a jar
			stream = Resource.class.getResourceAsStream("/" + resourceLocation);
		}
		this.inputstream = stream;
		if(inputstream == null) {
			throw new MissingResourceException("Could not find resource " + resourceLocation, Resource.class.getName(), resourceLocation);
		}
	}
	
	/*
	 * Note that a resource can only be written once.
	 */
	@SuppressWarnings("deprecation")
	public void write(File destDir) throws IOException {
		File dest = new File(destDir.getAbsolutePath() + "/" + destPath);
		if(!dest.exists()) {
			System.out.println("writing " + dest);
			FileUtils.writeByteArrayToFile(dest, IOUtils.toByteArray(inputstream), false);
		}
		else {
			System.out.println("Resource already exists: " + dest);
		}
	}
}
