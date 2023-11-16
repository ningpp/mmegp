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

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.config.Context;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.Callable;

public class BuildIntrospectedTableCallable implements Callable<Pair<IntrospectedTable, File>> {

    private final Context context;
    private final File file;
    private final MetaInfoHandler metaInfoHandler;

    public BuildIntrospectedTableCallable(Context context, File file, MetaInfoHandler metaInfoHandler) {
        this.context = context;
        this.file = file;
        this.metaInfoHandler = metaInfoHandler;
    }

    @Override
    public Pair<IntrospectedTable, File> call() {
        try {
            String fileContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            ParseResult<CompilationUnit> parseResult = JavaParserUtil.newParser().parse(fileContent);
            Optional<CompilationUnit> cuOptional = parseResult.getResult();
            if (parseResult.isSuccessful() && cuOptional.isPresent()) {
                return Pair.of(
                            MyBatisGeneratorUtil.buildIntrospectedTable(
                                    context,
                                    cuOptional.get(),
                                    metaInfoHandler
                            ),
                            file);
            }
        } catch (Exception e) {
            throw new GenerateMyBatisExampleException(e.getMessage(), e);
        }
        return Pair.of(null, file);
    }

}