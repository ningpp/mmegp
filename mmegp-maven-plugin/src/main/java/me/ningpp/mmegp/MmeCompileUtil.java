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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MmeCompileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MmeCompileUtil.class);

    private MmeCompileUtil() {
    }

    public static void generate(Context context,
            List<String> compileSourceRoots,
            MetaInfoHandler metaInfoHandler,
            List<Plugin> plugins, int nThreads) throws InterruptedException, ExecutionException {

        if (compileSourceRoots == null) {
            return;
        }
        List<Pair<IntrospectedTable, File>> pairs = new ArrayList<>();
        for (String compileSourceRoot : compileSourceRoots) {
            String modelPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
            String modelFileDir = compileSourceRoot + File.separator +
                    modelPackage.replace('.', File.separatorChar) + File.separator;
            File[] javaFiles = new File(modelFileDir).listFiles();
            if (javaFiles != null) {
                pairs.addAll(buildIntrospectedTables(
                        javaFiles, context, metaInfoHandler, nThreads));
            }
        }

        for (Pair<IntrospectedTable, File> pair : pairs) {
            var introspectedTable = pair.getLeft();
            introspectedTable.initialize();
            //必须在initialize方法之后，否则rules不起作用
            introspectedTable.setRules(new MmegpFlatModelRules(introspectedTable));
            introspectedTable.calculateGenerators(Collections.emptyList(), new NullProgressCallback());
        }

        List<GeneratedJavaFile> additionalJavaFiles = new ArrayList<>();
        List<GeneratedXmlFile> generatedXmlFiles = new ArrayList<>();
        for (Pair<IntrospectedTable, File> pair : pairs) {
            additionalJavaFiles.addAll(pair.getLeft().getGeneratedJavaFiles());

            generatedXmlFiles.addAll(pair.getLeft().getGeneratedXmlFiles());

            if (plugins != null) {
                for (Plugin plugin : plugins) {
                    plugin.initialized(pair.getLeft());
                    List<GeneratedJavaFile> files = plugin.contextGenerateAdditionalJavaFiles(pair.getLeft());
                    files.forEach(f -> LOGGER.info("generated :   " + f.getFileName()));
                    additionalJavaFiles.addAll(files);
                    generatedXmlFiles.addAll(plugin.contextGenerateAdditionalXmlFiles(pair.getLeft()));
                }
            }
        }

        writeGeneratedFiles(additionalJavaFiles, generatedXmlFiles, nThreads);
    }

    private static void writeGeneratedFiles(List<GeneratedJavaFile> additionalJavaFiles,
                                            List<GeneratedXmlFile> generatedXmlFiles,
                                            int nThreads) throws InterruptedException, ExecutionException {
        DefaultShellCallback shellCallback = new DefaultShellCallback(true);
        List<WriteGeneratedFileCallable> allTasks = new ArrayList<>();
        for (GeneratedJavaFile gjf : additionalJavaFiles) {
            allTasks.add(new WriteGeneratedFileCallable(shellCallback, gjf));
        }

        for (GeneratedXmlFile gxf : generatedXmlFiles) {
            allTasks.add(new WriteGeneratedFileCallable(shellCallback, gxf));
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

    private static List<Pair<IntrospectedTable, File>> buildIntrospectedTables(File[] javaFiles,
                       Context context, MetaInfoHandler metaInfoHandler, int nThreads) throws InterruptedException, ExecutionException {
        List<Pair<IntrospectedTable, File>> pairs = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        try {
            List<BuildIntrospectedTableCallable> allTasks = new ArrayList<>(javaFiles.length);
            for (File file : javaFiles) {
                if (file.getName().endsWith(".java")
                        && file.exists()
                        && file.length() > 0) {
                    allTasks.add(new BuildIntrospectedTableCallable(
                            context, file, metaInfoHandler
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
