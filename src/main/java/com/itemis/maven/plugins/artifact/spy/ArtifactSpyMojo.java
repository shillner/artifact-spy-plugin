package com.itemis.maven.plugins.artifact.spy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 * A small Mojo that only detects the artifacts produced by the build of the current project and writes them out to a
 * properties file.<br>
 * The Mojo should be bound to a late build phase in order to detect all artifacts produced by the build. By default it
 * is bound to the verify phase.<br>
 * <br>
 *
 * The properties will have the following format:
 * <ul>
 * <li><b>key: </b>artifact coordinates</li>
 * <li><b>value: </b>filepath of the artifact (relative to the project's base directory)</li>
 * </ul>
 *
 * @author <a href="mailto:stanley.hillner@itemis.de">Stanley Hillner</a>
 * @since 1.0.0
 */
@Mojo(name = "spy", defaultPhase = LifecyclePhase.VERIFY)
public class ArtifactSpyMojo extends AbstractMojo {
  @Component
  private MavenProject project;

  /**
   * The path of the properties file which will then contain all artifacts that have been produced by the build of the
   * current project.
   */
  @Parameter(required = true, defaultValue = "${project.build.directory}/artifact-spy/artifacts.properties")
  private File outputFile;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Properties props = new Properties();
    addPomArtifact(props);
    addProjectArtifact(props);
    addAttachedArtifacts(props);

    FileOutputStream fos = null;
    try {
      this.outputFile.getParentFile().mkdirs();
      fos = new FileOutputStream(this.outputFile);
      props.store(fos, "");
    } catch (IOException e) {
      throw new MojoFailureException(
          "Error serializing the project artifacts to file: " + this.outputFile.getAbsolutePath(), e);
    } finally {
      try {
        fos.close();
      } catch (IOException e) {
        throw new MojoExecutionException("Could not close output stream after writing project artifacts to file: "
            + this.outputFile.getAbsolutePath(), e);
      }
    }
  }

  private void addPomArtifact(Properties props) {
    props.setProperty(
        new DefaultArtifact(this.project.getGroupId(), this.project.getArtifactId(), "pom", this.project.getVersion())
            .toString(),
        getProjectRelativePath(this.project.getFile()));
  }

  private void addProjectArtifact(Properties props) {
    Artifact projectArtifact = this.project.getArtifact();
    if (projectArtifact.getFile() != null && projectArtifact.getFile().isFile()) {
      props.setProperty(RepositoryUtils.toArtifact(projectArtifact).toString(),
          getProjectRelativePath(projectArtifact.getFile()));
    }
  }

  private void addAttachedArtifacts(Properties props) {
    for (Artifact a : this.project.getAttachedArtifacts()) {
      props.setProperty(RepositoryUtils.toArtifact(a).toString(), getProjectRelativePath(a.getFile()));
    }
  }

  private String getProjectRelativePath(File f) {
    return this.project.getBasedir().toURI().relativize(f.toURI()).getPath();
  }
}
