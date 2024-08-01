package com.wildermods.workspace;

import org.gradle.api.artifacts.Dependency;
import static com.wildermods.workspace.ProjectDependencyType.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A WWProject dependency represents a dependency common to all projects made
 * using WilderWorkspace.
 * <p>
 * Each constant in this enum corresponds to a specific dependency required for projects
 * using WilderWorkspace. These dependencies are essential for consistent compilation
 * and execution of projects that share the same version of the WilderWorkspace plugin.
 * <p>
 * Dependencies are categorized into two types:
 * <ul>
 *     <li>{@code fabricDep}: Dependencies that Fabric requires to be in the "fabricDependencyPath" at runtime. 
 *         {@code ./bin/fabric/} is the default location where these dependencies are located.</li>
 *     <li>{@code fabricImpl}: Dependencies that are required to be in the "fabricPath" at runtime. 
 *         {@code ./bin/} is the default location where these dependencies are located.</li>
 * </ul>
 * <p>
 * The {@code gitRepo} field, if specified, indicates a Git repository URL from which
 * the dependency can be obtained. This is used to set up source control repositories
 * for project dependencies during plugin application. See the {@link WilderWorkspacePlugin#apply(Settings)}
 * method for details on how these repositories are configured.
 * <p>
 * For additional information about the dependencies needed by the WilderWorkspace
 * gradle plugin itself, see {@link PluginDependency}.
 * </p>
 */
public enum WWProjectDependency implements Dependency {
		
		commonsText(fabricDep, "org.apache.commons", "commons-text", "@commonsTextVersion@"),
		accessWidener(fabricDep, "net.fabricmc", "access-widener", "@accessWidenerVersion@"),
		tinyMappingsParser(fabricDep, "net.fabricmc", "tiny-mappings-parser", "@tinyMappingsVersion@"),
		mixin(fabricDep, "org.spongepowered", "mixin", "@mixinVersion@"),
		guava(fabricDep, "com.google.guava", "guava", "@guavaVersion@"),
		gson(fabricDep, "com.google.code.gson", "gson", "@gsonVersion@"),
		gameProvider(fabricDep, "com.wildermods", "provider", "@providerVersion@", "https://wildermods.com/WildermythGameProvider.git"),
		
		asm(fabricDep, "org.ow2.asm", "asm", "@asmVersion@"),
		asmAnalysis(fabricDep, "org.ow2.asm", "asm-analysis", "@asmVersion@"),
		asmCommons(fabricDep, "org.ow2.asm", "asm-commons", "@asmVersion@"),
		asmTree(fabricDep, "org.ow2.asm", "asm-tree", "@asmVersion@"),
		asmUtil(fabricDep, "org.ow2.asm", "asm-util", "@asmVersion@"),
		
		
		fabricLoader(fabricImpl, "net.fabricmc", "fabric-loader", "@fabricLoaderVersion@"),
		wilderLoader(fabricImpl, "com.wildermods", "wilderloader", "@wilderLoaderVersion@", "https://wildermods.com/WilderLoader.git")
		
	;

	private final ProjectDependencyType type;
	private final String groupID;
	private final String artifact;
	private final String version;
	private final String dependencyString;
	private final URI gitRepo;
	private String reason;

	/**
	 * Constructs a new {@code WWProjectDependency} with the specified parameters.
	 * 
	 * @param type the type of the project dependency
	 * @param groupID the group ID of the dependency
	 * @param artifact the artifact ID of the dependency
	 * @param version the version of the dependency
	 */
	private WWProjectDependency(ProjectDependencyType type, String groupID, String artifact, String version) {
		this(type, groupID, artifact, version, null);
	}
	
	/**
	 * Constructs a new {@code WWProjectDependency} with the specified parameters and optional Git repository.
	 * 
	 * @param type the type of the project dependency
	 * @param groupID the group ID of the dependency
	 * @param artifact the artifact ID of the dependency
	 * @param version the version of the dependency
	 * @param gitRepo the URL of the Git repository associated with the dependency, or {@code null} if not applicable
	 */
	private WWProjectDependency(ProjectDependencyType type, String groupID, String artifact, String version, String gitRepo) {
		this.type = type;
		this.groupID = groupID;
		this.artifact = artifact;
		this.version = version;
		this.dependencyString = String.join(":", groupID, artifact, version);
		try {
			if(gitRepo != null) {
				this.gitRepo = new URI(gitRepo);
			}
			else {
				this.gitRepo = null;
			}
		} catch (URISyntaxException e) {
			throw new AssertionError(e);
		}
	}

	public String toString() {
		return dependencyString;
	}

	@Override
	public void because(String reason) {
		this.reason = reason;
	}

	@Override
	public boolean contentEquals(Dependency dependency) {
		return dependency == this;
	}

	@Override
	public Dependency copy() {
		throw new AssertionError();
	}
	
	/**
	 * Returns the type of the project dependency.
	 *
	 * The type indicates the category of the dependency is used to determine where it should be placed
	 * within the workspace.
	 * 
	 * @see ProjectDependencyType
	 * 
	 * @return the type of the dependency
	 */
	public ProjectDependencyType getType() {
		return type;
	}

	@Override
	public String getGroup() {
		return groupID;
	}

	@Override
	public String getName() {
		return artifact;
	}

	@Override
	public String getReason() {
		return reason;
	}
	
	/**
	 * Returns the module string representation of the dependency in the format
	 * {@code group:artifact}.
	 * 
	 * @return the module string representation
	 */
	public String getModule() {
		return String.join(":", getGroup(), getName());
	}
	
	public URI getRepo() {
		return gitRepo;
	}

	@Override
	public String getVersion() {
		return version;
	}

}
