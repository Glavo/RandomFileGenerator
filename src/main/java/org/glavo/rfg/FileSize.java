package org.glavo.rfg;

import java.util.Locale;

public record FileSize(long size, Unit unit) {

    public static FileSize parse(String s) {
        s = s.toLowerCase();

        try {
            for (Unit unit : Unit.values) {
                for (String suffix : unit.suffixes) {
                    if (s.endsWith(suffix)) {
                        return new FileSize(Long.parseLong(s.substring(0, s.length() - suffix.length())), unit);
                    }
                }
            }

            if (s.endsWith("b")) {
                return new FileSize(Long.parseLong(s.substring(0, s.length() - 1)), Unit.B);
            } else {
                return new FileSize(Long.parseLong(s), Unit.B);
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException("Invalid file size: " + s);
        }
    }

    public long toBytes() {
        return size * unit.factor;
    }

    @Override
    public String toString() {
        return String.valueOf(size) + unit;
    }

    public enum Unit {
        B(1L),
        KiB(1L << 10),
        MiB(1L << 20),
        GiB(1L << 30),
        TiB(1L << 40),
        PiB(1L << 50),
        EiB(1L << 60);

        static final Unit[] values = values();

        public final long factor;
        final String[] suffixes;

        Unit(long factor) {
            this.factor = factor;
            if (factor == 1L) { // Byte
                this.suffixes = new String[]{"byte", "bytes"};
            } else {
                String name = this.name().toLowerCase(Locale.ROOT).intern();
                String name1 = name.substring(0, 1).intern();

                this.suffixes = new String[]{name, name1, name1 + "b", name1 + "byte", name1 + "bytes"};
            }
        }
    }
}
