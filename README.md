Artifact Spy Plugin
===================
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.itemis.maven.plugins/artifact-spy-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.itemis.maven.plugins/artifact-spy-plugin)

The purpose of this plugin is to spy out the artifacts that are produced by a project build.
It serializes the project artifact and all attached artifacts into a properties file with the following format:

*   **key:** the artifact's coordinates (_groupId_:_artifactId_:_type_:_version_:_classifier_) 
*   **value:** the filepath of the artifact relative to the project's base directory

The path of the artifact properties file can be adapted using the parameter `outputFile` and is by default set to `${project.build.directory}/artifact-spy/artifacts.properties`.


Sample usage
------------
    <project>
      ...
      <build>
        ...
        <plugins>
          ...
          <plugin>
            <groupId>com.itemis.maven.plugins</groupId>
            <artifactId>artifact-spy-plugin</artifactId>
            <version>${version.artifact-spy-plugin}</version>
            <executions>
              <execution>
                <id>spy-artifacts</id>
                <phase>verify</phase>
                <goals>
                  <goal>spy</goal>
                </goals>
                <configuration>
                  <outputFile>${project.build.directory}/spy/artifacts.properties</outputFile>
                </configuration>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    </project>


Sample output
-------------
### Project with packaging 'pom'
    #
    #Wed Apr 27 09:26:52 CEST 2016
    com.itemis.maven.plugins\:org-parent\:pom\:1=pom.xml

### Project with packaging 'jar'
    #
    #Wed Apr 27 08:51:13 CEST 2016
    com.itemis.maven.plugins\:cdi-plugin-utils\:jar\:1.0.0=target/cdi-plugin-utils-1.0.0.jar
    
### Project attaching an additional artifact
    #
    #Wed Apr 27 09:08:03 CEST 2016
    com.itemis.maven.plugins\:ump-it-core\:zip\:pack\:0.0.1=target/ump-it-core-resources-0.0.1.zip
    com.itemis.maven.plugins\:ump-it-core\:jar\:0.0.1=target/ump-it-core-0.0.1.jar
    com.itemis.maven.plugins\:ump-it-core\:pom\:0.0.1=pom.xml
