package ru.krosovok.krshubswap.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KrshubswapClient implements ClientModInitializer {
    private static final String CATEGORY = "key.categories.krshubswap";
    private AliasConfig aliasConfig;
    private TeleportManager teleportManager;
    private KeyBinding configKeyBinding;
    private static KrshubswapClient instance;

    public static void onGameMessage(String text) {
        if (instance != null && instance.teleportManager != null) {
            instance.teleportManager.onMessage(text);
        }
    }

    @Override
    public void onInitializeClient() {
        instance = this;

        java.nio.file.Path configDir = java.nio.file.Paths.get(
            System.getProperty("user.home"), ".labymod", "configs"
        );

        this.aliasConfig = new AliasConfig(configDir);
        this.teleportManager = new TeleportManager();
        new SwapCommand(teleportManager, aliasConfig).register();

        this.configKeyBinding = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.krshubswap.open_config",
                InputUtil.Type.KEYSYM,
                295,
                CATEGORY
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
    }

    private void onEndTick(MinecraftClient client) {
        teleportManager.tick();

        while (configKeyBinding.wasPressed()) {
            if (client.currentScreen == null) {
                // Config screen would open here
            }
        }
    }
}
