package me.hypherionmc.morecreativetabs.client.data;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.Map;

public class MoreCreativeTabsData {

    public Map<String, CustomCreativeTab> tabs;
    public List<String> disabled_tabs;
    public List<String> ordered_tabs;

    public MoreCreativeTabsData(FriendlyByteBuf buf) {
        this.tabs = buf.readMap(FriendlyByteBuf::readUtf, CustomCreativeTab::new);
        this.disabled_tabs = buf.readNullable(friendlyByteBuf -> friendlyByteBuf.readList(FriendlyByteBuf::readUtf));
        this.ordered_tabs = buf.readNullable(friendlyByteBuf -> friendlyByteBuf.readList(FriendlyByteBuf::readUtf));
    }

    public void save(FriendlyByteBuf buf) {
        buf.writeMap(tabs, FriendlyByteBuf::writeUtf, (friendlyByteBuf, customCreativeTab) -> customCreativeTab.save(friendlyByteBuf));
        buf.writeNullable(disabled_tabs, (friendlyByteBuf, strings) -> friendlyByteBuf.writeCollection(strings, FriendlyByteBuf::writeUtf));
        buf.writeNullable(ordered_tabs, (friendlyByteBuf, strings) -> friendlyByteBuf.writeCollection(strings, FriendlyByteBuf::writeUtf));
    }
}
