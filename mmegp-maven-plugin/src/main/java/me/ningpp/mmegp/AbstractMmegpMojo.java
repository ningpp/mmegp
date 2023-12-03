/*
 *    Copyright 2021-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package me.ningpp.mmegp;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.util.List;

public abstract class AbstractMmegpMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    @Component
    protected BuildContext buildContext;

    @Component
    protected MavenProjectHelper projectHelper;

    private static final List<String> INCLUDE_XML = List.of("**/*.xml");

    @Override
    public final void execute() {
        beforeExecute();
        doExecute();
        afterExecute();
    }

    protected void beforeExecute() {
    }

    protected void doExecute() {
    }

    protected void afterExecute() {
    }

    protected void addComileSource(File outputDirectory) {
        projectHelper.addResource(project, outputDirectory.getAbsolutePath(),
                INCLUDE_XML, List.of());
        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
        buildContext.refresh(outputDirectory);
    }

    protected void addTestComileSource(File testOutputDirectory) {
        projectHelper.addTestResource(project, testOutputDirectory.getAbsolutePath(),
                INCLUDE_XML, List.of());
        project.addTestCompileSourceRoot(testOutputDirectory.getAbsolutePath());
        buildContext.refresh(testOutputDirectory);
    }

}
