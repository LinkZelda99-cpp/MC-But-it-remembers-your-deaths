package com.austin.mcbutitremembersyourdeaths;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = "mcbutitremembersyourdeaths",
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT
)
public class DeathScreenListener {

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (!(event.getScreen() instanceof DeathScreen oldScreen)) return;

        if (DeathMessageStore.LAST_DEATH_MESSAGE == null) return;

        Minecraft mc = Minecraft.getInstance();

if (!(event.getScreen() instanceof DeathScreen)) return;
if (mc.player == null || mc.level == null) return;

event.setNewScreen(
    new DeathScreen(
        DeathMessageStore.LAST_DEATH_MESSAGE,
        mc.level.getLevelData().isHardcore(),
        mc.player
    )
);
    }
}