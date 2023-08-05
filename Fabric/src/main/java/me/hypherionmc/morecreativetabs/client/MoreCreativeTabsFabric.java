package me.hypherionmc.morecreativetabs.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.data.MoreCreativeTabsData;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * @author HypherionSA
 */
public class MoreCreativeTabsFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /* Register Client Commands */
        ClientCommandRegistrationCallback.EVENT.register((phase, listener) -> {
            ModConstants.logger.info("Registering Commands");
            ClientCommandManager.getActiveDispatcher().register(literal("mct").then(literal("showTabNames")
                            .then(argument("enabled", bool()).executes(context -> {
                                boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                CustomCreativeTabRegistry.showNames = enabled;
                                context.getSource().sendFeedback(enabled ? Component.literal("Showing tab registry names") : Component.literal("Showing tab names"));
                                return 1;
                            }))).then(literal("reloadTabs").executes(ctx -> {
                        FabricResourceLoader.reloadTabs();
                        ctx.getSource().sendFeedback(Component.literal("Reloaded Custom Tabs"));
                        return 1;
                    }))
            );
        });

        /* Load initial entries and cache old tabs */
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricResourceLoader());

        /* register channel */
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("morecreativetabs", "tabs"), (client, handler, buf, responseSender) -> {
            try {
                MoreCreativeTabsData data = new MoreCreativeTabsData(buf);
                Minecraft.getInstance().execute(() -> FabricResourceLoader.loadServerData(data));
            } catch (Exception e) {
                ModConstants.logger.error("Failed to parse morecreativetabs:tabs plugin message", e);
            }
        });
    }
}
