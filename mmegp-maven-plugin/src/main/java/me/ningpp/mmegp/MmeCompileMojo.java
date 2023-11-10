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

import me.ningpp.mmegp.codegen.DoNotGenerateDsqlModelIntrospectedTableImpl;
import me.ningpp.mmegp.codegen.DoNotGenerateModelIntrospectedTableImpl;
import me.ningpp.mmegp.codegen.DoNotGenerateSimpleModelIntrospectedTableImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.mybatis.generator.api.CompositePlugin;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.ObjectFactory;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class MmeCompileMojo extends AbstractMojo {

    @Parameter(required = false, property = "nThreads", defaultValue = "1")
    private int nThreads;

    /**
     * This is the directory into which the {@code .java} will be created.
     */
    @Parameter(
            required = false,
            property = "outputDirectory",
            defaultValue = "${project.build.directory}/generated-sources/mme/java"
    )
    private File outputDirectory;

    /**
     * This is the directory into which the {@code .xml} will be created.
     */
    @Parameter(required = false, property = "xmlOutputDirectory")
    private String xmlOutputDirectory;

    /**
     * This is the generator config xml file path (like mbg config file).
     */
    @Parameter(required = false, property = "generatorConfigFilePath")
    private String generatorConfigFilePath;

    /**
     * This is the MetaInfoHandler class name config.
     */
    @Parameter(required = false, property = "metaInfoHandlerClassName")
    private String metaInfoHandlerClassName;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    @Component
    protected BuildContext buildContext;

    @Component
    protected MavenProjectHelper projectHelper;

    private static final String XML_TARGET_PROJECT = "targetProject";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // mbg use line.separator render file lines,
        // for stable, don't use system-dependent value.
        System.setProperty("line.separator", "\n");

        MetaInfoHandler metaInfoHandler = createMetaInfoHandler(metaInfoHandlerClassName);

        try ( FileReader cfgFileReader = new FileReader(new File(generatorConfigFilePath)) ) {
            List<Context> contexts = MmegpConfigurationParser.parseContexts(new InputSource(cfgFileReader));
            for (Context context : contexts) {
                resetContextTargetRuntime(context);

                var sqlMapGeneratorCfg = context.getSqlMapGeneratorConfiguration();
                if (sqlMapGeneratorCfg != null) {
                    sqlMapGeneratorCfg.setTargetProject(outputDirectory.getAbsolutePath());
                }

                var javaClientGeneratorCfg = context.getJavaClientGeneratorConfiguration();
                if (javaClientGeneratorCfg != null) {
                    javaClientGeneratorCfg.setTargetProject(outputDirectory.getAbsolutePath());
                }

                var javaModelGeneratorCfg = context.getJavaModelGeneratorConfiguration();
                if (javaModelGeneratorCfg != null) {
                    javaModelGeneratorCfg.setTargetProject(outputDirectory.getAbsolutePath());
                }

                context.generateFiles(new NullProgressCallback(), Collections.emptyList(), 
                        Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList());

                List<Plugin> plugins = resetTargetProjectValue(context);
                
                MmeCompileUtil.generate(context, project.getCompileSourceRoots(),
                        metaInfoHandler, plugins, nThreads);
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Generate MyBatis Model Example File Error!", e);
        }

        projectHelper.addResource(project, outputDirectory.getAbsolutePath(), 
                List.of("**/*.xml"), Collections.emptyList());
        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
        buildContext.refresh(outputDirectory);
    }

    private void resetContextTargetRuntime(Context context) {
        String type = context.getTargetRuntime();
        if (!stringHasValue(type)) {
            type = DoNotGenerateDsqlModelIntrospectedTableImpl.class.getName();
        } else if ("MyBatis3".equalsIgnoreCase(type)) {
            type = DoNotGenerateModelIntrospectedTableImpl.class.getName();
        } else if ("MyBatis3Simple".equalsIgnoreCase(type)) {
            type = DoNotGenerateSimpleModelIntrospectedTableImpl.class.getName();
        } else if ("MyBatis3DynamicSql".equalsIgnoreCase(type)) {
            type = DoNotGenerateDsqlModelIntrospectedTableImpl.class.getName();
        }
        context.setTargetRuntime(type);
    }

    @SuppressWarnings("unchecked")
    private List<Plugin> resetTargetProjectValue(Context context) throws ReflectiveOperationException, SecurityException {
        //hack
        Field pluginsField = CompositePlugin.class.getDeclaredField("plugins");
        pluginsField.setAccessible(true);
        List<Plugin> plugins = (List<Plugin>) pluginsField.get(context.getPlugins());
        for (Plugin plugin : plugins) {
            if (plugin instanceof PluginAdapter) {
                Field propertiesField = PluginAdapter.class.getDeclaredField("properties");
                propertiesField.setAccessible(true);
                Properties properties = (Properties) propertiesField.get(plugin);
                String origalValue = properties.getProperty(XML_TARGET_PROJECT);
                String prefix = "";
                if (StringUtils.isNotEmpty(origalValue)) {
                    prefix = project.getBasedir().getAbsolutePath();
                }

                properties.put(XML_TARGET_PROJECT, prefix + File.separator + origalValue);
            }
        }
        return plugins;
    }

    private MetaInfoHandler createMetaInfoHandler(String metaInfoHandlerClassName) {
        if (StringUtils.isBlank(metaInfoHandlerClassName)) {
            return null;
        }
        return (MetaInfoHandler) ObjectFactory.createInternalObject(metaInfoHandlerClassName.trim().strip());
    }

}
