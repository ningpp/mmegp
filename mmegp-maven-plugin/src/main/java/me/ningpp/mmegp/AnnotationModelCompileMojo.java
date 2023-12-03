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

@Mojo(
        name = "generate-annotation-model",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class AnnotationModelCompileMojo extends AbstractAnnotationModelMojo {

    @Parameter(required = true, property = "annotationScanPackages")
    private String[] annotationScanPackages;

    @Parameter(required = true, property = "annotationModelPackage")
    private String annotationModelPackage;

    @Parameter(
            property = "annotationModelOutputDirectory",
            defaultValue = "${project.build.directory}/generated-sources/mme/java"
    )
    private File annotationModelOutputDirectory;

    @Override
    protected String[] getScanPackages() {
        return annotationScanPackages;
    }

    @Override
    protected String getModelPackage() {
        return annotationModelPackage;
    }

    @Override
    protected File getOutputDirectory() {
        return annotationModelOutputDirectory;
    }

    @Override
    protected void afterExecute() {
        super.addComileSource(annotationModelOutputDirectory);
    }
}
