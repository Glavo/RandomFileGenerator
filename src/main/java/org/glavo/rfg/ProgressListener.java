package org.glavo.rfg;

@FunctionalInterface
public interface ProgressListener {
    ProgressListener empty = progress -> {
    };

    void updateProcess(long progress);
}
