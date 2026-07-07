package ru.krosovok.krshubswap.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.Text;

public class SwapCommand {
    private final TeleportManager teleportManager;
    private final AliasConfig aliasConfig;

    public SwapCommand(TeleportManager teleportManager, AliasConfig aliasConfig) {
        this.teleportManager = teleportManager;
        this.aliasConfig = aliasConfig;
    }

    public void register() {
        for (String alias : aliasConfig.getAliases()) {
            if (!alias.trim().isEmpty()) {
                LiteralArgumentBuilder<FabricClientCommandSource> command =
                    ClientCommandManager.literal(alias)
                        .then(ClientCommandManager.argument("number",
                            com.mojang.brigadier.arguments.IntegerArgumentType.integer(
                                AnarchyRegistry.getMinNumber(),
                                AnarchyRegistry.getMaxNumber()))
                        .executes(ctx -> {
                            int number = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "number");
                            String error = teleportManager.start(number);
                            if (error != null) {
                                ctx.getSource().sendFeedback(Text.literal("[HubSwap] " + error));
                                return 0;
                            }
                            return 1;
                        }))
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(Text.literal("[HubSwap] Usage: /" + alias + " <number 1-" + AnarchyRegistry.getMaxNumber() + ">"));
                            return 0;
                        });

                ClientCommandManager.DISPATCHER.register(command);
            }
        }
    }
}
