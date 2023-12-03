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

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

public abstract class AbstractMybatisGenerateMojo extends AbstractMmegpMojo {

    @Parameter(required = false, property = "nThreads", defaultValue = "1")
    private int nThreads;

    /**
     * This is the MetaInfoHandler class name config.
     */
    @Parameter(required = false, property = "metaInfoHandlerClassName")
    private String metaInfoHandlerClassName;

    protected abstract String getConfigFile();

    protected abstract List<String> getSourceRoots();

    protected abstract File getOutputDirectory();

    protected abstract void afterExecute();

    @Override
    public final void doExecute() {
        // mbg use line.separator render file lines,
        // for stable, don't use system-dependent value.
        System.setProperty("line.separator", "\n");

        getLog().info("generate code from config file : " + getConfigFile());
        getLog().info("source root directories : " + String.join(", ", getSourceRoots()));
        getLog().info("output directory : " + getOutputDirectory().getAbsolutePath());

        MyBatisGeneratorUtil.generate(
                getConfigFile(),
                getSourceRoots(),
                getOutputDirectory(),
                metaInfoHandlerClassName,
                nThreads
        );
    }

}
