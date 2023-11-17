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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
        name = "generate-test",
        defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true, 
        threadSafe = true
)
public class MmeTestCompileMojo extends AbstractMmeMojo {

    /**
     * This is the generator config xml file path (like mbg config file).
     */
    @Parameter(required = true, property = "testGeneratorConfigFilePath")
    private String testGeneratorConfigFilePath;

    @Parameter(property = "customTestCompileSourceRoots")
    private String[] customTestCompileSourceRoots;

    /**
     * This is the directory into which the {@code .java} will be created.
     */
    @Parameter(
            required = false,
            property = "javaTestOutputDirectory",
            defaultValue = "${project.build.directory}/generated-test-sources/mme/java"
    )
    private File testOutputDirectory;

    @Override
    protected String getConfigFile() {
        return testGeneratorConfigFilePath;
    }

    @Override
    protected List<String> getSourceRoots() {
        List<String> sourceRoots = new ArrayList<>(project.getTestCompileSourceRoots());
        if (customTestCompileSourceRoots != null){
            Collections.addAll(sourceRoots, customTestCompileSourceRoots);
        }
        return sourceRoots;
    }

    @Override
    protected File getOutputDirectory() {
        return testOutputDirectory;
    }

    @Override
    protected void afterExecute() {
        if (!testOutputDirectory.exists()) {
            testOutputDirectory.mkdirs();
        }
        projectHelper.addTestResource(project, getOutputDirectory().getAbsolutePath(),
                List.of("**/*.xml"), Collections.emptyList());
        project.addTestCompileSourceRoot(testOutputDirectory.getAbsolutePath());
        buildContext.refresh(testOutputDirectory);
    }

}
