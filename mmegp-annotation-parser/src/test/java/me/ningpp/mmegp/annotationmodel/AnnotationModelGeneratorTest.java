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

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationModelGeneratorTest {

    @Test
    void generateSprignWebMvcTest() throws IOException {
        File[] files = generate(
                new String[] { "org.springframework" },
                "mmegptest_springframework" + System.currentTimeMillis()
        );
        assertTrue(files.length > 1);
    }

    @Test
    void generateJpaTest() throws IOException {
        File[] files = generate(
                new String[] { "jakarta.persistence" },
                "mmegptest_jpa" + System.currentTimeMillis()
        );
        assertTrue(files.length > 1);
    }

    private File[] generate(String[] packages, String modelPackage) throws IOException {
        String destDir = System.getProperty("java.io.tmpdir");
        AnnotationModelGenerator generator = new AnnotationModelGenerator(
                packages, modelPackage, new File(destDir)
        );
        generator.generate();
        File dir = new File(destDir + File.separator + modelPackage);
        File[] files = dir.listFiles();
        for (File file : files) {
            Files.delete(file.toPath());
        }
        Files.delete(dir.toPath());
        return files;
    }

}
