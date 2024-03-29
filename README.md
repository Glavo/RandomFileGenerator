﻿# RandomFileGenerator

A tool to generate random files in batches.

Please download the jar from the [release page](https://github.com/Glavo/RandomFileGenerator/releases) and execute it via `java -jar` (requires Java 17 or later).

Currently only CLI is supported, GUI interface will be provided in the future, as well as a native-image distribution that runs without a JVM.

Required command line arguments:
* `-o <file-name-format>`: The format of the file name.
  This is a pattern string and the resulting filename is formatted by `String.format(<file name format>, <file number>)` (file number starts counting from zero).
* `-s <file-size>`: The file size to be generated. Support unit suffix (e.g. `12k`, `1G`). If there is no suffix, it is treated as the number of bytes

Optional command line arguments:
* `-m <mode>`: The possible values for mode are:
  * `zero`: Generate files with all bytes set to 0.
  * `random`: Generate files with random bytes.
* `-n <num files>`: The number of files to be generated, default to 1.
* `-d <directory>`: The directory to generate files in, defaults to the current directory.
* `-a algo`: The random algorithm used to generate the file, ignored in the `zero` mode. The supported algorithms are:
  * `default`: The default algorithm, which is based on the Java `SplittableRandom`.
  * `legacy`: The legacy algorithm, which is based on the Java `Random`.
  * Other random generators supported by the JVM (e.g. `L32X64MixRandom`)
* `-t <num threads>`: The number of threads to use, defaults to half the number of available processors.
* `-e <seed>`: A long integer value. If it is specified, the program assigns each file a random seed based on it, for deterministic output.
