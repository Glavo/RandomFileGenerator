package org.glavo.rfg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGeneratorFactory;

public final class Main {
    public static void main(String[] args) throws InterruptedException {
        Options options = new Options();
        Iterator<String> it = Arrays.asList(args).iterator();

        while (it.hasNext()) {
            String option;
            switch (option = it.next()) {
                case "--gui" -> options.gui = true;
                case "-h", "-help", "--help" -> {
                    System.out.println("Please go to https://github.com/Glavo/RandomFileGenerator for help");
                    return;
                }
                case "-g", "-generator", "--generator" -> options.generator = Generator.fromString(readArg(option, it));
                case "-d", "-directory", "--directory" -> options.dir = Path.of(readArg(option, it));
                case "-o", "-output", "--output", "-output-format", "--output-format" ->
                        options.output = readArg(option, it);
                case "-n", "-num-files", "--num-files" -> options.numFiles = Integer.parseInt(readArg(option, it));
                case "-s", "-size", "--size", "-file-size", "--file-size" ->
                        options.fileSize = FileSize.parse(readArg(option, it));
                case "-a", "-algo", "--algo", "-algorithm", "--algorithm" -> {
                    String algo = readArg(option, it);
                    options.algo = switch (algo.toLowerCase(Locale.ROOT)) {
                        case "default", "splittable" -> RandomGeneratorFactory.of("SplittableRandom");
                        case "legacy", "lcg" -> RandomGeneratorFactory.of("Random");
                        case "secure" -> RandomGeneratorFactory.of("SecureRandom");
                        default -> RandomGeneratorFactory.of(algo);
                    };
                }
                case "-e", "-seed", "--seed" -> options.seed = Long.parseLong(readArg(option, it));
                case "-t", "-num-threads", "--num-threads" ->
                        options.numThreads = Integer.parseInt(readArg(option, it));

                default -> showWarnAndExit("Unknown option: " + option);
            }
        }

        if (options.gui) {
            System.err.println("GUI not implemented yet");
            System.exit(1);
        } else {
            if (options.fileSize == null)
                showWarnAndExit("File size is not specified");
            if (options.numFiles == null || options.numFiles == 1) {
                options.numFiles = 1;

                if (options.output == null)
                    showWarnAndExit("Output file is not specified");
            } else if (options.numFiles > 1) {
                if (options.output == null)
                    showWarnAndExit("Output file name format is not specified");
            } else {
                showWarnAndExit("The number of files generated must be a positive number");
            }

            if (options.generator == Generator.Random && options.algo == null)
                options.algo = RandomGeneratorFactory.of("SplittableRandom");

            if (options.dir == null)
                options.dir = Path.of(System.getProperty("user.dir"));

            if (options.numThreads == null)
                options.numThreads = Integer.max(1, Runtime.getRuntime().availableProcessors() / 2);
            else if (options.numThreads <= 0)
                showWarnAndExit("The number of threads must be a positive number");

            long baseSeed = options.seed == null ? options.seed = System.nanoTime() : options.seed;

            ExecutorService executorService = Executors.newFixedThreadPool(options.numThreads);
            var lock = new Object();
            var latch = new CountDownLatch(options.numFiles);
            var successCounter = new AtomicInteger();
            var failureCounter = new AtomicInteger();

            System.out.println("configuration: " + options.dump());

            var startTime = Instant.now();

            for (int i = 0; i < options.numFiles; i++) {
                Path output = options.dir.resolve(String.format(options.output, i));
                if (Files.exists(output)) {
                    System.err.println("Warning: file '" + output + "' already exists");
                }

                try {
                    Files.createDirectories(output.getParent());
                } catch (IOException e) {
                    System.err.println("Warning: failed to create directories for file '" + output + "'");
                }

                System.out.print("0% (0/" + options.numFiles + ")\r");
                System.out.flush();

                options.seed = baseSeed + i;
                executorService.execute(() -> {
                    boolean success = false;
                    try {
                        options.generator.generate(output, options, ProgressListener.empty);
                        success = true;
                    } catch (Throwable e) {
                        System.err.println("Warning: Failed to generate file '" + output + "'");
                        e.printStackTrace();


                    }

                    synchronized (lock) {
                        latch.countDown();
                        if (success) {
                            successCounter.incrementAndGet();
                        } else {
                            failureCounter.incrementAndGet();
                        }

                        var sc = successCounter.get();
                        var fc = failureCounter.get();

                        System.out.printf("%s%% (%s/%s)\r", (sc + fc) * 100 / options.numFiles, sc + fc, options.numFiles);
                        System.out.flush();
                    }
                });
            }
            executorService.shutdown();
            latch.await();

            var d = Duration.between(startTime, Instant.now());

            System.out.printf("Done in %s. %s in total, %s successes, %s failures%n", formatDuration(d), options.numFiles, successCounter.get(), failureCounter.get());

        }

    }

    private static String readArg(String option, Iterator<String> it) {
        if (!it.hasNext()) {
            System.err.println("Option '" + option + "' requires an argument");
            System.exit(1);
        }

        return it.next();
    }

    private static void showWarnAndExit(String message) {
        System.err.println(message);
        System.exit(1);
    }

    private static String formatDuration(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
}
