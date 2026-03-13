package com.austin.mcbutitremembersyourdeaths;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "mcbutitremembersyourdeaths",
        value = Dist.CLIENT
)
public class DeathMessageCapture {

    @SubscribeEvent
    public static void onClientRespawn(ClientPlayerNetworkEvent.Clone event) {
        // Clear stored message when player respawns
        DeathMessageStore.LAST_DEATH_MESSAGE = Component.literal("");
    }
}