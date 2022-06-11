package org.glavo.rfg;

import java.nio.file.Path;
import java.util.random.RandomGeneratorFactory;

public final class Options {
    public boolean gui = false;
    public Generator generator;
    public Path dir;
    public Integer numFiles;
    public FileSize fileSize;
    public String format;
    public RandomGeneratorFactory<?> algo;
    public Long seed;
    public Integer numThreads;
    public Path output;
}
