package com.wildermods.workspace.util;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileHelper {

	public static class IgnoreSymbolicVisitor<T> extends SimpleFileVisitor<T> {
		@Override
		public FileVisitResult preVisitDirectory(T t, BasicFileAttributes attrs) {
			if(attrs.isSymbolicLink()) {
				return FileVisitResult.SKIP_SUBTREE;
			}
			return FileVisitResult.CONTINUE;
		}
	}
	
	public static Path relativePath(Path parent, Path child) {
		parent = parent.normalize().toAbsolutePath();
		child = child.normalize().toAbsolutePath();
		if(!child.startsWith(parent)) {
			throw new IllegalArgumentException("Child path not a subpath of parent!");
		}
		return parent.relativize(child);
	}
	
}
