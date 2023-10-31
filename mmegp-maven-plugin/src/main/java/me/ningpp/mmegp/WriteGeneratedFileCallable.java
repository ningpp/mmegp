package me.ningpp.mmegp;

import org.mybatis.generator.api.GeneratedFile;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

public class WriteGeneratedFileCallable implements Callable<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteGeneratedFileCallable.class);

    private final DefaultShellCallback shellCallback;
    private final GeneratedFile gf;

    public WriteGeneratedFileCallable(DefaultShellCallback shellCallback, GeneratedFile gf) {
        this.shellCallback = shellCallback;
        this.gf = gf;

        init();
    }

    private void init() {
        try {
            if (!new File(gf.getTargetProject()).exists()) {
                new File(gf.getTargetProject()).mkdirs();
            }
            File directory = shellCallback.getDirectory(gf.getTargetProject(), gf.getTargetPackage());
            File targetFile = new File(directory, gf.getFileName());
            targetFile.getParentFile().mkdirs();
            targetFile.delete();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GenerateMyBatisExampleException(e.getMessage(), e);
        }
    }

    @Override
    public Void call() throws Exception {
        File targetFile = new File(
                shellCallback.getDirectory(gf.getTargetProject(), gf.getTargetPackage()),
                gf.getFileName());
        Files.writeString(targetFile.toPath(), gf.getFormattedContent(),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        LOGGER.info("writed :   " + targetFile.getAbsolutePath());
        return null;
    }
}
