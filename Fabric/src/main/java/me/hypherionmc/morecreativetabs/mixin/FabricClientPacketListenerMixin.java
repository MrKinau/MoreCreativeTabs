package me.hypherionmc.morecreativetabs.mixin;

import io.netty.buffer.Unpooled;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class FabricClientPacketListenerMixin {

    private final ResourceLocation channelIdentifier = new ResourceLocation("morecreativetabs", "tabs");

    @Inject(method = "handleLogin", at = @At("TAIL"), cancellable = true)
    public void injectHandleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        final ClientPacketListener self = (ClientPacketListener) (Object) this;

        CustomCreativeTabRegistry.server_data = null;
        self.getConnection().send(new ServerboundCustomPayloadPacket(channelIdentifier, new FriendlyByteBuf(Unpooled.buffer())));
    }
}
