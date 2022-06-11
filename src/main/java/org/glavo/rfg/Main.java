package org.glavo.rfg;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.random.RandomGeneratorFactory;

public final class Main {
    public static void main(String[] args) {
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
                case "-g", "-generator", "--generator" -> {
                    String g = readArg(option, it);
                    if ((options.generator = Generator.fromString(g)) == null) {
                        System.err.println("Unknown generator: " + g);
                        return;
                    }
                }
                case "-d", "-directory", "--directory" -> options.dir = Path.of(readArg(option, it));
                case "-f", "-format", "--format" -> options.format = readArg(option, it);
                case "-o", "-output", "--output" -> options.output = Path.of(readArg(option, it));
                case "-n", "-num-files", "--num-files" -> options.numFiles = Integer.parseInt(readArg(option, it));
                case "-s", "-size", "--size", "-file-size", "--file-size" -> options.fileSize = FileSize.parse(readArg(option, it));
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
                default -> {
                    System.err.println("Unknown option: " + option);
                    System.exit(1);
                }
            }
        }

        if (options.gui) {
            // TODO
        }


    }

    private static String readArg(String option, Iterator<String> it) {
        if (!it.hasNext()) {
            System.err.println("Option '" + option + "' requires an argument");
            System.exit(1);
        }

        return it.next();
    }
}
