package com.wildermods.workspace.gradle.plugin.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;

import org.apache.commons.io.file.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.org.apache.commons.lang.NotImplementedException;

import com.wildermods.workspace.util.OS;

public class CopyLocalDependenciesToWorkspaceTask extends DefaultTask {
	
	private static final Logger LOGGER = Logging.getLogger(CopyLocalDependenciesToWorkspaceTask.class);
	
	@Input
	private String platform = Platform.steam.name();
	
	@Input
	private String patchline = getProject().getName() + " " + getProject().getVersion();
	
	@Input
	private String destDir = getProject().relativePath("bin");
	
	@Input
	private String decompDir = Path.of(destDir).resolve("decomp").toString();
	
	@Input
	private boolean overwrite = false;
	
	public static enum Platform {
		
		steam(() -> {return OS.getSteamDefaultDirectory().resolve("common").resolve("Wildermyth");}),
		epic(unknownPlatformLocation("epic")),
		itch(unknownPlatformLocation("itch")),
		gog(unknownPlatformLocation("gog")),
		filesystem(() -> {return null;});
		
		private Callable<Path> dir;
		
		private Platform (Callable<Path> dir) {
			this.dir = dir;
		}
		
		private Path getDefaultInstallDirectory() throws Exception {
			return dir.call();
		}
		
		public static Platform fromString(String input) {
			for(Platform platform : Platform.values()) {
				if(platform.name().equals(input.toLowerCase())) {
					return platform;
				}
			}
			return filesystem;
		}
	}
	
	public static Callable<Path> unknownPlatformLocation(String platform) {
		return (() -> {throw new NotImplementedException("I don't know where the default install directory for Wildermyth is for the " + platform + " platform. Submit a pull request or input a raw path to the installation location.");});
	}
	
	@TaskAction
	public void copyDependencies() throws IOException {
		final Path destDir = Path.of(this.destDir).toAbsolutePath().normalize();
		try {
			Platform selectedPlatform = Platform.fromString(platform);
			LOGGER.info("Platform: " + platform);
			Path installDir;
			if(selectedPlatform != Platform.filesystem) {
				installDir = selectedPlatform.getDefaultInstallDirectory();
				LOGGER.info("Using default " + selectedPlatform + " install for " + OS.getOS() + ", located at " + installDir);
			}
			else {
				installDir = Path.of(platform);
				LOGGER.info("Using custom Wildermyth install located at " + installDir);
			}
			
			if(!Files.exists(installDir)) {
				throw new FileNotFoundException(installDir.toAbsolutePath().normalize().toString());
			}
			if(!Files.isDirectory(installDir)) {
				throw new NotDirectoryException(installDir.toAbsolutePath().normalize().toString());
			}
			
			if(!Files.exists(destDir)) {
				Files.createDirectories(destDir);
			}
			
			Files.walkFileTree(installDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Path target = destDir.resolve(installDir.relativize(file));
					if(!overwrite && Files.exists(target)) {
						return FileVisitResult.CONTINUE;
					}
					Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Path target = destDir.resolve(installDir.relativize(dir));
					if(attrs.isSymbolicLink() || dir.getFileName().endsWith("backup") || dir.getFileName().endsWith("feedback") || dir.getFileName().endsWith("logs") || dir.getFileName().endsWith("out") || dir.getFileName().endsWith("players") || dir.getFileName().endsWith("screenshots")) {
						return FileVisitResult.SKIP_SUBTREE;
					}
					Files.createDirectories(target);
					return FileVisitResult.CONTINUE;
				}
			});
			
			Path patchFile = destDir.resolve("patchline.txt");
			PathUtils.writeString(patchFile, patchline + " - [WilderWorkspace {$workspaceVersion}]", Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING);
			
		}
		catch(Exception e) {
			RuntimeException e2 = new RuntimeException("Failed to copy dependencies.", e);
			LOGGER.error("Failed to copy dependencies.", e2);
		}
	}
	
	private static void copyDirs(Path source, Path dest, String... sub) throws IOException {
		for(String s : sub) {
			Path from = source.resolve(s);
			Path to = dest;
			if(Files.exists(from)) {
				if(Files.isDirectory(from)) {
					to = dest.resolve(s);
					PathUtils.copyDirectory(from, to);
				}
				else if(Files.isRegularFile(from)) {
					PathUtils.copyFileToDirectory(from, to);
				}
			}
			else {
				throw new FileNotFoundException(from.toAbsolutePath().normalize().toString());
			}
		}
	}
	
	public String getPlatform() {
		return platform;
	}
	
	public String getPatchline() {
		return patchline;
	}
	
	public String getDestDir() {
		return destDir;
	}
	
	public String getDecompDir() {
		return decompDir;
	}
	
	public boolean getOverwrite() {
		return overwrite;
	}
	
}
