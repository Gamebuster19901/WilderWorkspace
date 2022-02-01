package com.wildermods.workspace;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


/**
 * If shouldOverwrite is true, then if a file is found at its destination, it will be overwritten
 * otherwise, copying for that file will be skipped.
 */
public class ShouldOverwriteWriteRule extends WriteRule {

	final boolean shouldOverwrite;
	
	public ShouldOverwriteWriteRule(boolean shouldOverwrite, String regex) {
		super(regex);
		this.shouldOverwrite = shouldOverwrite;
	}

	@Override
	public Throwable write(Installation installation, File source, File dest) {
		if(shouldOverwrite || !dest.exists()) {
			try {
				System.out.println("Overwriting " + dest);
				FileUtils.copyFile(source, dest);
			} catch (IOException e) {
				return e;
			}
		}
		else {
			System.out.println("Skipping " + dest + " because it already exists.");
		}
		return null;
	}

}
