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

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class MmeCompileMojo extends AbstractMmeMojo {

    /**
     * This is the generator config xml file path (like mbg config file).
     */
    @Parameter(required = true, property = "generatorConfigFilePath")
    private String generatorConfigFilePath;

    @Parameter(property = "customCompileSourceRoots")
    private String[] customCompileSourceRoots;

    /**
     * This is the directory into which the {@code .java} will be created.
     */
    @Parameter(
            required = false,
            property = "outputDirectory",
            defaultValue = "${project.build.directory}/generated-sources/mme/java"
    )
    private File outputDirectory;

    @Override
    protected String getConfigFile() {
        return generatorConfigFilePath;
    }

    @Override
    protected List<String> getSourceRoots() {
        List<String> sourceRoots = new ArrayList<>(project.getCompileSourceRoots());
        if (customCompileSourceRoots != null){
            Collections.addAll(sourceRoots, customCompileSourceRoots);
        }
        return sourceRoots;
    }

    @Override
    protected File getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    protected void afterExecute() {
        projectHelper.addResource(project, getOutputDirectory().getAbsolutePath(),
                List.of("**/*.xml"), Collections.emptyList());
        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
        buildContext.refresh(outputDirectory);
    }

}
