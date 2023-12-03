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
package me.ningpp.mmegp.annotationmodel;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class AnnotationModelGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationModelGenerator.class);

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final ResourcePatternResolver RESOURCE_RESOLVER = new PathMatchingResourcePatternResolver();

    public static final String SUFFIX = "Model";

    private final String[] scanPackages;
    private final String modelPackage;
    private final File outputDirectory;

    public AnnotationModelGenerator(String[] scanPackages, String modelPackage, File outputDirectory) {
        this.scanPackages = scanPackages;
        this.modelPackage = modelPackage;
        this.outputDirectory = outputDirectory;
    }

    public void generate() {
        Map<Class<?>, CompilationUnit> parsed = buildModels();
        parsed.forEach((key, value) -> {
            try {
                String targetDir = outputDirectory.getAbsolutePath()
                        + File.separator
                        + modelPackage.replace(".", File.separator);
                File targetFile = new File(targetDir,
                        value.getTypes().get(0).getNameAsString() + ".java");
                boolean mkdirsFlag = targetFile.getParentFile().mkdirs();
                boolean deleteFlag = targetFile.delete();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("mkdirs {}, delete file {}", mkdirsFlag, deleteFlag);
                }

                Files.writeString(targetFile.toPath(),
                        value.toString(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW);

            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private Map<Class<?>, CompilationUnit> buildModels() {
        Set<Class<?>> classes = scanAnnotations();
        Map<Class<?>, CompilationUnit> parsed = new HashMap<>();
        for (Class<?> clazz : classes) {
            parseAnnotaion(clazz, parsed);
        }
        return parsed;
    }

    private void parseAnnotaion(Class<?> annotationClass,
                                Map<Class<?>, CompilationUnit> parsed) {
        if (parsed.containsKey(annotationClass)) {
            return;
        }

        List<Method> methods = Stream.of(annotationClass.getDeclaredMethods())
                .sorted(Comparator.comparing(Method::getName)).toList();

        CompilationUnit compilationUnit = new CompilationUnit();
        parsed.put(annotationClass, compilationUnit);

        compilationUnit.setPackageDeclaration(modelPackage);
        RecordDeclaration recordDeclaration = new RecordDeclaration(
                new NodeList<>(Modifier.publicModifier()),
                annotationClass.getSimpleName() + SUFFIX);
        compilationUnit.addType(recordDeclaration);

        for (Method method : methods) {
            Class<?> componentOrReturnType;
            if (method.getReturnType().isArray()) {
                Class<?> componentType = method.getReturnType().componentType();
                if (componentType.isAnnotation()) {
                    parseAnnotaion(componentType, parsed);
                }
                componentOrReturnType = componentType;
            } else {
                componentOrReturnType = method.getReturnType();
            }

            Type paramType;
            if (componentOrReturnType.isAnnotation()) {
                String simpleName = componentOrReturnType.getSimpleName() + SUFFIX;
                String name = modelPackage + "." + simpleName;
                paramType = StaticJavaParser.parseType(
                        simpleName
                                + (method.getReturnType().isArray() ? "[]" : ""));
                compilationUnit.addImport(name);
            } else {
                compilationUnit.addImport(method.getReturnType());
                paramType = StaticJavaParser.parseType(method.getGenericReturnType().getTypeName());
            }
            recordDeclaration.getParameters()
                    .add(new Parameter(paramType, method.getName()));
        }
    }

    private Set<Class<?>> scanAnnotations() {
        Set<Class<?>> classes = new HashSet<>();
        try {
            for (String basePackage : scanPackages) {
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(
                                new StandardEnvironment().resolveRequiredPlaceholders(basePackage))
                        + '/' + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = RESOURCE_RESOLVER.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    MetadataReader metadataReader = new SimpleMetadataReaderFactory().getMetadataReader(resource);
                    if (metadataReader.getClassMetadata().isAnnotation()) {
                        classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return classes;
    }

}
