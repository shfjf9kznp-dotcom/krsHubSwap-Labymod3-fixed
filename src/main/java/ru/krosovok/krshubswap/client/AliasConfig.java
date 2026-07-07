package ru.krosovok.krshubswap.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AliasConfig {
    private static final List<String> DEFAULT_ALIASES = List.of("lt", "an");
    private final Path configFile;
    private final List<String> aliases = new ArrayList<>();

    public AliasConfig(Path configDir) {
        this.configFile = configDir.resolve("krshubswap-aliases.txt");
        load();
    }

    public List<String> getAliases() {
        return new ArrayList<>(aliases);
    }

    public void setAliases(List<String> newAliases) {
        aliases.clear();
        Set<String> seen = new LinkedHashSet<>();

        for (String raw : newAliases) {
            String a = sanitize(raw);
            if (!a.isEmpty()) {
                seen.add(a);
            }
        }

        aliases.addAll(seen);
        if (aliases.isEmpty()) {
            aliases.addAll(DEFAULT_ALIASES);
        }

        save();
    }

    private static String sanitize(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        while (s.startsWith("/")) {
            s = s.substring(1);
        }
        s = s.replace(" ", "");
        return s;
    }

    private void load() {
        aliases.clear();

        try {
            if (Files.exists(configFile)) {
                List<String> lines = Files.readAllLines(configFile, StandardCharsets.UTF_8);
                Set<String> seen = new LinkedHashSet<>();

                for (String line : lines) {
                    String a = sanitize(line);
                    if (!a.isEmpty()) {
                        seen.add(a);
                    }
                }

                aliases.addAll(seen);
            }
        } catch (IOException ignored) {
        }

        if (aliases.isEmpty()) {
            aliases.addAll(DEFAULT_ALIASES);
        }
    }

    public void save() {
        try {
            Files.createDirectories(configFile.getParent());
            Files.write(configFile, aliases, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }
}
