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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.ningpp.mmegp.codegen.DoNotGenerateDsqlModelIntrospectedTableImpl;
import me.ningpp.mmegp.codegen.DoNotGenerateModelIntrospectedTableImpl;
import me.ningpp.mmegp.codegen.DoNotGenerateSimpleModelIntrospectedTableImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mybatis.generator.api.CompositePlugin;
import org.mybatis.generator.api.GeneratedFile;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public final class MyBatisGeneratorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisGeneratorUtil.class);

    private static final String XML_TARGET_PROJECT = "targetProject";

    private MyBatisGeneratorUtil() {
    }

    public static void generate(String configFile,
                                List<String> sourceRoots,
                                File outputDirectory,
                                String metaInfoHandlerClassName,
                                int nThreads) {
        try (InputStream inputStream = new FileInputStream(configFile)) {
            generate(inputStream, sourceRoots, outputDirectory, metaInfoHandlerClassName, nThreads);
        } catch (IOException e) {
            throw new GenerateMyBatisExampleException(e.getMessage(), e);
        }
    }

    public static void generate(InputStream configFile,
                                List<String> sourceRoots,
                                File outputDirectory,
                                String metaInfoHandlerClassName,
                                int nThreads) {
        MetaInfoHandler metaInfoHandler = createMetaInfoHandler(metaInfoHandlerClassName);

        try {
            List<Context> contexts = MmegpConfigurationParser.parseContexts(new InputSource(configFile));
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

                List<Plugin> plugins = resetTargetProjectValue(context, outputDirectory);

                generate(context, sourceRoots, metaInfoHandler, plugins, nThreads);
            }
        } catch (InterruptedException e) {
            LOGGER.warn(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (ReflectiveOperationException
                 | ExecutionException
                 | XMLParserException
                 | SAXException
                 | ParserConfigurationException
                 | IOException e) {
            throw new GenerateMyBatisExampleException(e.getMessage(), e);
        }
    }

    private static void resetContextTargetRuntime(Context context) {
        String type = context.getTargetRuntime();
        if (!StringUtility.stringHasValue(type)) {
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
    private static List<Plugin> resetTargetProjectValue(Context context, File outputDirectory) throws ReflectiveOperationException {
        //hack
        Field pluginsField = CompositePlugin.class.getDeclaredField("plugins");
        pluginsField.trySetAccessible();
        List<Plugin> plugins = (List<Plugin>) pluginsField.get(context.getPlugins());
        for (Plugin plugin : plugins) {
            if (plugin instanceof PluginAdapter) {
                Field propertiesField = PluginAdapter.class.getDeclaredField("properties");
                propertiesField.trySetAccessible();
                Properties properties = (Properties) propertiesField.get(plugin);
                properties.put(XML_TARGET_PROJECT, outputDirectory.getAbsolutePath());
            }
        }
        return plugins;
    }

    private static MetaInfoHandler createMetaInfoHandler(String metaInfoHandlerClassName) {
        if (StringUtils.isBlank(metaInfoHandlerClassName)) {
            return null;
        }
        return (MetaInfoHandler) ObjectFactory.createInternalObject(metaInfoHandlerClassName.trim().strip());
    }

    private static void generate(Context context,
                                 List<String> compileSourceRoots,
                                 MetaInfoHandler metaInfoHandler,
                                 List<Plugin> plugins,
                                 int nThreads) throws InterruptedException, ExecutionException {

        if (compileSourceRoots == null) {
            return;
        }
        List<Pair<IntrospectedTable, File>> pairs = buildIntrospectedTables(
                        compileSourceRoots, context, metaInfoHandler, nThreads);

        initIntrospectedTables(pairs);

        List<GeneratedFile> generatedFiles = generateFiles(pairs, plugins);

        writeGeneratedFiles(generatedFiles, nThreads);
    }

    private static List<GeneratedFile> generateFiles(List<Pair<IntrospectedTable, File>> pairs, List<Plugin> plugins) {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        for (Pair<IntrospectedTable, File> pair : pairs) {
            generatedFiles.addAll(pair.getLeft().getGeneratedJavaFiles());

            generatedFiles.addAll(pair.getLeft().getGeneratedXmlFiles());

            if (plugins != null) {
                for (Plugin plugin : plugins) {
                    plugin.initialized(pair.getLeft());
                    List<GeneratedJavaFile> files = plugin.contextGenerateAdditionalJavaFiles(pair.getLeft());
                    if (LOGGER.isDebugEnabled()) {
                        files.forEach(f -> LOGGER.debug("Successfully generate file :   " + f.getFileName()));
                    }

                    generatedFiles.addAll(files);
                    generatedFiles.addAll(plugin.contextGenerateAdditionalXmlFiles(pair.getLeft()));
                }
            }
        }
        return generatedFiles;
    }

    private static void initIntrospectedTables(List<Pair<IntrospectedTable, File>> pairs) {
        for (Pair<IntrospectedTable, File> pair : pairs) {
            var introspectedTable = pair.getLeft();
            introspectedTable.initialize();
            //必须在initialize方法之后，否则rules不起作用
            introspectedTable.setRules(new MmegpFlatModelRules(introspectedTable));
            introspectedTable.calculateGenerators(Collections.emptyList(), new NullProgressCallback());
        }
    }

    private static List<File> getSourceFiles(List<String> compileSourceRoots, Context context) {
        List<File> sourceFiles = new ArrayList<>();
        for (String compileSourceRoot : compileSourceRoots) {
            String modelPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
            String modelFileDir = compileSourceRoot + File.separator +
                    modelPackage.replace('.', File.separatorChar) + File.separator;
            File[] javaFiles = new File(modelFileDir).listFiles();
            if (javaFiles != null) {
                Collections.addAll(sourceFiles, javaFiles);
            }
        }
        return sourceFiles;
    }

    private static List<Pair<IntrospectedTable, File>> buildIntrospectedTables(List<String> compileSourceRoots,
                                                                               Context context,
                                                                               MetaInfoHandler metaInfoHandler,
                                                                               int nThreads) throws InterruptedException, ExecutionException {
        List<File> sourceFiles = getSourceFiles(compileSourceRoots, context);
        List<Pair<IntrospectedTable, File>> pairs = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        IntrospectedTableBuilder builder = createIntrospectedTableBuilder(context);
        try {
            List<BuildIntrospectedTableCallable> allTasks = new ArrayList<>(sourceFiles.size());
            for (File file : sourceFiles) {
                if (file.getName().endsWith(".java")
                        && file.exists()
                        && file.length() > 0) {
                    allTasks.add(new BuildIntrospectedTableCallable(
                            builder, context, file, metaInfoHandler
                    ));
                }
            }

            List<List<BuildIntrospectedTableCallable>> listList = toListList(allTasks, nThreads);
            for (List<BuildIntrospectedTableCallable> tasks : listList) {
                List<Future<Pair<IntrospectedTable, File>>> results = pool.invokeAll(tasks);
                for (Future<Pair<IntrospectedTable, File>> future : results) {
                    Pair<IntrospectedTable, File> pair = future.get();
                    if (pair.getLeft() != null) {
                        pairs.add(pair);
                        context.getIntrospectedTables().add(pair.getLeft());
                    }
                }
            }
        } finally {
            pool.shutdown();
        }
        return pairs;
    }

    private static IntrospectedTableBuilder createIntrospectedTableBuilder(Context context) {
        String type = context.getProperty("introspectedTableBuilder");
        if (!StringUtility.stringHasValue(type)) {
            type = DefaultIntrospectedTableBuilder.class.getName();
        }
        return (IntrospectedTableBuilder) ObjectFactory.createInternalObject(type);
    }

    private static void writeGeneratedFiles(List<GeneratedFile> generatedFiles,
                                            int nThreads) throws InterruptedException, ExecutionException {
        DefaultShellCallback shellCallback = new DefaultShellCallback(true);
        List<WriteGeneratedFileCallable> allTasks = new ArrayList<>();
        for (GeneratedFile gf : generatedFiles) {
            allTasks.add(new WriteGeneratedFileCallable(shellCallback, gf));
        }

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        try {
            List<List<WriteGeneratedFileCallable>> listList = toListList(allTasks, nThreads);
            for (List<WriteGeneratedFileCallable> tasks : listList) {
                List<Future<Void>> results = pool.invokeAll(tasks);
                for (Future<Void> future : results) {
                    future.get();
                }
            }
        } finally {
            pool.shutdown();
        }
    }

    private static <E> List<List<E>> toListList(List<E> list, int n) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>(0);
        }

        List<List<E>> listList = new ArrayList<>();
        if (list.size() < n) {
            listList.add(new ArrayList<>(list));
        } else {
            for (int i = 0; i < list.size(); i += n) {
                listList.add(new ArrayList<>(list.subList(i, Math.min(i + n, list.size()))));
            }
        }
        return listList;
    }

}
