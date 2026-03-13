package com.austin.mcbutitremembersyourdeaths;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "mcbutitremembersyourdeaths",
        value = Dist.CLIENT
)
public class DeathScreenHandler {

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {

        if (!(event.getScreen() instanceof DeathScreen)) return;

        if (DeathMessageStore.LAST_DEATH_MESSAGE == null) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics graphics = event.getGuiGraphics();

        int centerX = event.getScreen().width / 2;

        // Draw YOUR message instead of the vanilla one
        graphics.drawCenteredString(
                mc.font,
                DeathMessageStore.LAST_DEATH_MESSAGE.getString(),
                centerX,
                85,
                0xFFFFFF
        );
    }
}