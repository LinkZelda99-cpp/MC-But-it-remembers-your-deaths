package com.austin.mcbutitremembersyourdeaths;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "mcbutitremembersyourdeaths",
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class PlayerCloneHandler {

    private static final String MOD_DATA_TAG = "mcbutitremembersyourdeaths";

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        CompoundTag originalData = event.getOriginal()
                .getPersistentData()
                .getCompoundOrEmpty(MOD_DATA_TAG);

        event.getEntity()
                .getPersistentData()
                .put(MOD_DATA_TAG, originalData.copy());
    }
}
