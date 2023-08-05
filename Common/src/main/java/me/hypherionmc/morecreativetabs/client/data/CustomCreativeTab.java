package me.hypherionmc.morecreativetabs.client.data;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

/**
 * @author HypherionSA
 * Gson Helper class for loading Custom Tabs
 */
public class CustomCreativeTab {

    public boolean tab_enabled;
    public String tab_name;
    public TabIcon tab_stack;
    public String tab_background;
    public boolean replace = false;
    public boolean keepExisting = false;
    public List<TabItem> tab_items;

    public CustomCreativeTab() {}

    public CustomCreativeTab(FriendlyByteBuf buf) {
        this.tab_enabled = buf.readBoolean();
        this.tab_name = buf.readUtf();
        this.tab_stack = buf.readNullable(TabIcon::new);
        this.tab_background = buf.readNullable(FriendlyByteBuf::readUtf);
        this.tab_items = buf.readNullable(friendlyByteBuf -> friendlyByteBuf.readList(TabItem::new));
    }

    public void save(FriendlyByteBuf buf) {
        buf.writeBoolean(tab_enabled);
        buf.writeUtf(tab_name);
        buf.writeNullable(tab_stack, (friendlyByteBuf1, tabIcon) -> tabIcon.save(friendlyByteBuf1));
        buf.writeNullable(tab_background, FriendlyByteBuf::writeUtf);
        buf.writeNullable(tab_items, (friendlyByteBuf, tabItems) -> friendlyByteBuf.writeCollection(tabItems, (friendlyByteBuf1, tabItem) -> tabItem.save(friendlyByteBuf1)));
    }

    public static class TabItem {
        public String name;
        public boolean hide_old_tab;
        public String nbt;

        public TabItem() {}

        public TabItem(FriendlyByteBuf buf) {
            this.name = buf.readUtf();
            this.hide_old_tab = buf.readBoolean();
            this.nbt = buf.readUtf();
        }

        public void save(FriendlyByteBuf buf) {
            buf.writeUtf(name);
            buf.writeBoolean(hide_old_tab);
            buf.writeUtf(nbt);
        }
    }

    public static class TabIcon {
        public String name;
        public String nbt;

        public TabIcon() {}

        public TabIcon(FriendlyByteBuf buf) {
            this.name = buf.readUtf();
            this.nbt = buf.readUtf();
        }

        public void save(FriendlyByteBuf buf) {
            buf.writeUtf(name);
            buf.writeUtf(nbt);
        }
    }

}
