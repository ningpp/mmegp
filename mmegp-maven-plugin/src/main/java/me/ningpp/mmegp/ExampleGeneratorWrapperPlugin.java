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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.config.PropertyRegistry;

public class ExampleGeneratorWrapperPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private String getTargetProject() {
        String targetProject = (String) properties.get("targetProject");
        return StringUtils.isEmpty(targetProject) ? "target/generated-sources/mme/java" : targetProject;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        ExampleGenerator generator = new ExampleGenerator(getTargetProject());
        generator.setContext(context);
        generator.setIntrospectedTable(introspectedTable);
        generator.setProgressCallback(new NullProgressCallback());
        generator.setWarnings(new ArrayList<>(1));

        List<CompilationUnit> compilationUnits = generator.getCompilationUnits();
        List<GeneratedJavaFile> answer = new ArrayList<>(1);
        if (compilationUnits == null) {
            return answer;
        }
        for (CompilationUnit compilationUnit : compilationUnits) {
            answer.add(new GeneratedJavaFile(compilationUnit,
                    getTargetProject(),
                    context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                    context.getJavaFormatter()));
        }
        return answer;
    }

}
