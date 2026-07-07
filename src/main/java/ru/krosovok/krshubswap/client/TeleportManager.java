package ru.krosovok.krshubswap.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class TeleportManager {
    private static final String TYPE_KEY = "advancedserverselecter:server-type";
    private static final String SERVER_KEY = "advancedserverselecter:server";
    private static final int TIMEOUT = 200;
    private State state = State.IDLE;
    private int num;
    private String cat;
    private String key;
    private Object prevWorld;
    private int ticks;

    public TeleportManager() {
    }

    public boolean isBusy() {
        return state != State.IDLE;
    }

    public String start(int number) {
        if (isBusy()) {
            return "Already executing teleport, wait for completion";
        } else if (!AnarchyRegistry.isValid(number)) {
            return "Anarchy #" + number + " doesn't exist (available 1-" + AnarchyRegistry.getMaxNumber() + ")";
        } else {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getNetworkHandler() != null && client.player != null) {
                this.num = number;
                this.cat = AnarchyRegistry.categoryOf(number);
                this.key = AnarchyRegistry.keyOf(number);
                this.prevWorld = client.world;
                this.ticks = 0;
                this.state = State.WAITING_HUB_WORLD;
                client.player.networkHandler.sendChatCommand("hub");
                return null;
            } else {
                return "Not connected to server";
            }
        }
    }

    public void onMessage(String text) {
        if (state == State.WAITING_HUB_WORLD && text.contains("already connected")) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.networkHandler.sendChatCommand("lite");
                state = State.WAITING_MENU1;
                ticks = 0;
            }
        }
    }

    public void tick() {
        if (state != State.IDLE) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getNetworkHandler() != null && client.player != null) {
                ticks++;
                if (ticks > TIMEOUT) {
                    ticks = 0;
                    state = State.IDLE;
                } else {
                    switch (state) {
                        case WAITING_HUB_WORLD:
                            waitHub(client);
                            break;
                        case WAITING_MENU1:
                            scanMenu(client, TYPE_KEY, cat, true);
                            break;
                        case WAITING_MENU2:
                            scanMenu(client, SERVER_KEY, key, false);
                            break;
                    }
                }
            } else {
                if (state != State.WAITING_HUB_WORLD) {
                    reset();
                }
            }
        }
    }

    private void waitHub(MinecraftClient client) {
        if (client.world != null && client.world != prevWorld) {
            client.player.networkHandler.sendChatCommand("lite");
            state = State.WAITING_MENU1;
            ticks = 0;
        }
    }

    private void scanMenu(MinecraftClient client, String nbtKey, String expectedValue, boolean firstMenu) {
        if (client.currentScreen instanceof net.minecraft.screen.GenericContainerScreen) {
            // Container scanning logic would go here
            // For now, just complete the sequence
            if (firstMenu) {
                state = State.WAITING_MENU2;
            } else {
                state = State.IDLE;
            }
            ticks = 0;
        }
    }

    private void reset() {
        state = State.IDLE;
        ticks = 0;
        prevWorld = null;
    }

    private enum State {
        IDLE,
        WAITING_HUB_WORLD,
        WAITING_MENU1,
        WAITING_MENU2
    }
}
