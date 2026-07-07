package ru.krosovok.krshubswap.client;

import java.util.ArrayList;

public class AnarchyRegistry {
    private static final RangeConfig rangesConfig = new RangeConfig(
        java.nio.file.Paths.get(System.getProperty("user.home"), ".labymod", "configs")
    );

    private AnarchyRegistry() {
    }

    public static void applyRanges(RangeConfig config) {
        rangesConfig.setRange(new ArrayList<>(config.getRanges()), config.getTotal());
    }

    public static RangeConfig getRangeConfig() {
        return rangesConfig;
    }

    public static int getMinNumber() {
        return rangesConfig.getMin();
    }

    public static int getMaxNumber() {
        return rangesConfig.getMax();
    }

    public static boolean isValid(int number) {
        return number >= rangesConfig.getMin() && number <= rangesConfig.getMax();
    }

    public static String categoryOf(int number) {
        RangeConfig.RangeEntry entry = rangesConfig.find(number);
        if (entry != null) {
            return entry.key();
        } else {
            throw new IllegalArgumentException("Invalid anarchy number: " + number);
        }
    }

    public static String keyOf(int number) {
        return number == 1 ? "lanarchy" : "lanarchy" + number;
    }

    public static String name(int number) {
        RangeConfig.RangeEntry entry = rangesConfig.find(number);
        String prefix;
        if (entry != null) {
            String k = entry.key();
            prefix = switch (k) {
                case "solo" -> "СолоЛайт";
                case "duo" -> "ДуоЛайт";
                case "trio" -> "ТриоЛайт";
                default -> "КланЛайт";
            };
        } else {
            prefix = "Anarchy";
        }
        return prefix + " #" + number;
    }
}
