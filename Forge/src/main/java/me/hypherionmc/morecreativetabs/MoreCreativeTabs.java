package me.hypherionmc.morecreativetabs;

import com.google.common.collect.ImmutableList;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.mixin.accessor.ForgeCreativeModeTabRegistryAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.CreativeModeTabs.*;

/**
 * @author HypherionSA
 */
@Mod(ModConstants.MOD_ID)
public class MoreCreativeTabs {

    private static boolean hasRun = false;

    public MoreCreativeTabs() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));
    }

    public static void reloadResources() {
        if (!hasRun) {
            List<ResourceKey<CreativeModeTab>> VANILLA_TABS = ImmutableList.of(BUILDING_BLOCKS, COLORED_BLOCKS, NATURAL_BLOCKS, FUNCTIONAL_BLOCKS, REDSTONE_BLOCKS, HOTBAR, SEARCH, TOOLS_AND_UTILITIES, COMBAT, FOOD_AND_DRINKS, INGREDIENTS, SPAWN_EGGS, OP_BLOCKS, INVENTORY);
            List<CreativeModeTab> beforeTabs = new ArrayList<>();

            VANILLA_TABS.forEach(t -> beforeTabs.add(BuiltInRegistries.CREATIVE_MODE_TAB.get(t)));

            ForgeCreativeModeTabRegistryAccessor.getInternalTabs().forEach(t -> {
                if (!beforeTabs.contains(t)) {
                    beforeTabs.add(t);
                }
            });

            CustomCreativeTabRegistry.tabs_before = beforeTabs;
            reloadTabs();
            hasRun = true;
        } else {
            reloadTabs();
        }
    }

    /**
     * Called to reload all creative tabs
     */
    private static void reloadTabs() {
        ModConstants.logger.info("Checking for custom creative tabs");
        CustomCreativeTabRegistry.clearTabs();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            Map<ResourceLocation, Resource> customTabs = manager.listResources("morecreativetabs",
                    path -> path.getPath().endsWith(".json") && !path.getPath().contains("disabled_tabs")
                            && !path.getPath().contains("ordered_tabs"));

            Map<ResourceLocation, Resource> disabledTabs = manager.listResources("morecreativetabs", path -> path.getPath().contains("disabled_tabs.json"));
            Map<ResourceLocation, Resource> orderedTabs = manager.listResources("morecreativetabs", path -> path.getPath().contains("ordered_tabs.json"));

            if (!disabledTabs.isEmpty()) {
                CustomCreativeTabRegistry.loadDisabledTabs(disabledTabs);
            }

            if (!orderedTabs.isEmpty()) {
                CustomCreativeTabRegistry.loadOrderedTabs(orderedTabs);
            }

            CustomCreativeTabRegistry.processEntries(customTabs);
        });
    }
}
