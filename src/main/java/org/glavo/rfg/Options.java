package org.glavo.rfg;

import java.nio.file.Path;
import java.util.random.RandomGeneratorFactory;

public final class Options {
    public boolean gui = false;
    public Mode mode = Mode.Random;
    public Path dir;
    public Integer numFiles;
    public FileSize fileSize;
    public RandomGeneratorFactory<?> algo;
    public Long seed;
    public Integer numThreads;
    public String output;

    public String dump() {
        return "mode=" + mode +
                ", dir=" + dir +
                ", numFiles=" + numFiles +
                ", fileSize=" + fileSize +
                (algo == null ? "" : ", algo=" + algo.name()) +
                ", seed=" + seed +
                ", numThreads=" + numThreads +
                ", output=" + output;
    }
}
