package ru.krosovok.krshubswap.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RangeConfig {
    private final Path configFile;
    private final List<RangeEntry> ranges = new ArrayList<>();
    private int total = 66;

    public RangeConfig(Path configDir) {
        this.configFile = configDir.resolve("krshubswap-ranges.txt");
        resetDefaults();
        load();
    }

    private void resetDefaults() {
        ranges.clear();
        ranges.add(new RangeEntry("solo", "Соло", 1, 15));
        ranges.add(new RangeEntry("duo", "Дуо", 16, 31));
        ranges.add(new RangeEntry("trio", "Трио", 32, 47));
        ranges.add(new RangeEntry("clans", "Кланы", 48, 66));
        total = 66;
    }

    public List<RangeEntry> getRanges() {
        return Collections.unmodifiableList(ranges);
    }

    public int getTotal() {
        return total;
    }

    public void setRange(List<RangeEntry> newRanges, int newTotal) {
        if (newTotal < 1) {
            newTotal = 1;
        }

        total = newTotal;
        List<RangeEntry> sorted = new ArrayList<>(newRanges);
        sorted.sort(Comparator.comparingInt(RangeEntry::min));
        ranges.clear();

        for (RangeEntry e : sorted) {
            int min = Math.max(1, Math.min(e.min(), total));
            int max = Math.max(min, Math.min(e.max(), total));
            if (!ranges.isEmpty()) {
                RangeEntry prev = ranges.get(ranges.size() - 1);
                if (min <= prev.max()) {
                    min = prev.max() + 1;
                }
                if (max < min) {
                    max = min;
                }
            }
            ranges.add(new RangeEntry(e.key(), e.name(), min, max));
        }

        if (!ranges.isEmpty()) {
            RangeEntry last = ranges.get(ranges.size() - 1);
            if (last.max() < total) {
                ranges.set(ranges.size() - 1, new RangeEntry(last.key(), last.name(), last.min(), total));
            }
        }

        save();
    }

    public RangeEntry find(int number) {
        for (RangeEntry entry : ranges) {
            if (number >= entry.min() && number <= entry.max()) {
                return entry;
            }
        }
        return null;
    }

    public int getMin() {
        return 1;
    }

    public int getMax() {
        return total;
    }

    private void load() {
        try {
            if (Files.exists(configFile)) {
                List<String> lines = Files.readAllLines(configFile, StandardCharsets.UTF_8);
                int loadedTotal = 66;
                List<RangeEntry> loaded = new ArrayList<>();

                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        if (line.startsWith("total=")) {
                            try {
                                loadedTotal = Integer.parseInt(line.substring(6).trim());
                            } catch (NumberFormatException ignored) {
                            }
                        } else {
                            String[] parts = line.split("=");
                            if (parts.length == 2) {
                                String[] rangeParts = parts[1].split("-");
                                if (rangeParts.length == 2) {
                                    try {
                                        int min = Integer.parseInt(rangeParts[0].trim());
                                        int max = Integer.parseInt(rangeParts[1].trim());
                                        loaded.add(new RangeEntry(parts[0].trim(), getName(parts[0].trim()), min, max));
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            }
                        }
                    }
                }

                if (!loaded.isEmpty()) {
                    total = loadedTotal;
                    ranges.clear();
                    ranges.addAll(loaded);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public void save() {
        try {
            Files.createDirectories(configFile.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("total=" + total);

            for (RangeEntry e : ranges) {
                lines.add(e.key() + "=" + e.min() + "-" + e.max());
            }

            Files.write(configFile, lines, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private static String getName(String key) {
        return switch (key) {
            case "solo" -> "Соло";
            case "duo" -> "Дуо";
            case "trio" -> "Трио";
            case "clans" -> "Кланы";
            default -> key;
        };
    }

    public static class RangeEntry {
        private final String key;
        private final String name;
        private final int min;
        private final int max;

        public RangeEntry(String key, String name, int min, int max) {
            this.key = key;
            this.name = name;
            this.min = min;
            this.max = max;
        }

        public String key() {
            return key;
        }

        public String name() {
            return name;
        }

        public int min() {
            return min;
        }

        public int max() {
            return max;
        }
    }
}
