package org.glavo.rfg;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import java.util.random.RandomGenerator;

public enum Generator {
    Zero {
        @Override
        public void generate(Path filePath, Options options, ProgressListener listener) throws IOException {
            try (RandomAccessFile f = new RandomAccessFile(filePath.toFile(), "rw")) {
                f.setLength(options.fileSize.toBytes());
            }
            listener.updateProcess(options.fileSize.toBytes());
        }
    },
    Random {
        private static final Set<OpenOption> openOptions =
                Set.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        private static final FileAttribute<?>[] attributes = new FileAttribute[0];

        private static final int bufferSize = 4096;
        private static final ThreadLocal<ByteBuffer> buffer = ThreadLocal.withInitial(() -> ByteBuffer.allocate(bufferSize));

        private static void nextBytes(RandomGenerator random, byte[] arr, int limit) {
            int i = 0;
            for (int words = limit >> 3; words-- > 0; ) {
                long rnd = random.nextLong();
                for (int n = 8; n-- > 0; rnd >>>= Byte.SIZE) {
                    arr[i++] = (byte) rnd;
                }
            }
            if (i < limit)
                for (long rnd = random.nextLong(); i < limit; rnd >>>= Byte.SIZE) {
                    arr[i++] = (byte) rnd;
                }
        }

        @Override
        public void generate(Path filePath, Options options, ProgressListener listener) throws IOException {
            RandomGenerator generator = options.algo.create(options.seed);

            ByteBuffer b = buffer.get().clear();
            byte[] arr = b.array();
            long fileSize = options.fileSize.toBytes();

            try (WritableByteChannel channel = Files.newByteChannel(filePath, openOptions, attributes)) {
                long bytes = 0;

                while (bytes < fileSize) {
                    long remaining = fileSize - bytes;
                    int n;
                    if (remaining >= bufferSize) {
                        n = bufferSize;
                        generator.nextBytes(arr);
                    } else {
                        n = (int) remaining;
                        nextBytes(generator, arr, n);
                    }

                    b.limit(n);
                    channel.write(b);

                    b.position(0);
                    bytes += n;
                    listener.updateProcess(bytes);
                }
            }

        }
    };

    public abstract void generate(Path filePath, Options options, ProgressListener listener) throws IOException;

    public static Generator fromString(String s) {
        return switch (s) {
            case "z", "zero" -> Zero;
            case "r", "random" -> Random;
            default -> throw new IllegalArgumentException("Unknown generator: " + s);
        };
    }
}
