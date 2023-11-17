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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateCodeTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCodeTest.class);

    private static final String CFG_FILE = "/mmegpGeneratorConfig.xml";

    @Test
    void generateTest() {
        File dir = new File("target/mmegp-test-59951356a4f523a9ddc5d9388168c72262913b59/");
        boolean mkdirFlag = dir.mkdirs();
        LOGGER.info("mkdirs: {}, generate in dir : {}", mkdirFlag, dir.getAbsolutePath());

        MyBatisGeneratorUtil.generate(
                GenerateCodeTest.class.getResourceAsStream(CFG_FILE),
                List.of(), dir, null, 1
        );

        MyBatisGeneratorUtil.generate(
                GenerateCodeTest.class.getResourceAsStream(CFG_FILE),
                List.of(), dir, EmptyMetaInfoHandler.class.getName(), 1
        );

        File testEntityDir = new File("src/test/java/");
        MyBatisGeneratorUtil.generate(
                GenerateCodeTest.class.getResourceAsStream(CFG_FILE),
                List.of(testEntityDir.getAbsolutePath()),
                dir, EmptyMetaInfoHandler.class.getName(), 1
        );

        try (Stream<Path> walk = Files.walk(dir.toPath())) {
            List<File> files = walk.sorted(Comparator.reverseOrder()).map(Path::toFile).toList();
            assertTrue(files.stream().filter(f -> f.getName().endsWith(".java"))
                    .anyMatch(f -> f.length() > 1));
            for (File file : files) {
                Files.deleteIfExists(file.toPath());
            }
        } catch (Exception e) {
            // ignore
        }
    }

}
