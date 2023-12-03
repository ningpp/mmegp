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

import me.ningpp.mmegp.annotationmodel.AnnotationModelGenerator;

import java.io.File;
import java.util.List;

public abstract class AbstractAnnotationModelMojo extends AbstractMmegpMojo {

    protected abstract String[] getScanPackages();

    protected abstract String getModelPackage();

    protected abstract File getOutputDirectory();

    @Override
    protected final void doExecute() {
        File outputDirectory = getOutputDirectory();
        getLog().info("output directory : " + outputDirectory.getAbsolutePath());

        AnnotationModelGenerator generator = new AnnotationModelGenerator(
                getScanPackages(), getModelPackage(), getOutputDirectory());
        generator.generate();
    }

    //@Override
    public final void execute1() {
        File outputDirectory = getOutputDirectory();
        getLog().info("output directory : " + outputDirectory.getAbsolutePath());

        AnnotationModelGenerator generator = new AnnotationModelGenerator(
                getScanPackages(), getModelPackage(), getOutputDirectory());
        generator.generate();

        projectHelper.addResource(project, outputDirectory.getAbsolutePath(),
                List.of(), List.of());
        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
        buildContext.refresh(outputDirectory);
    }
}
