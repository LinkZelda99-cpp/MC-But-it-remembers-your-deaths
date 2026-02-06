package com.austin.mcbutitremembersyourdeaths;

import java.util.Random;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;



@Mod.EventBusSubscriber(
        modid = "mcbutitremembersyourdeaths",
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class DeathListener {

    private static final Random random = new Random();

    private static final String[] fallMessages = {
            "You fell from a high place… again.",
            "Gravity called. You didn’t pick up.",
            "Careful with those ledges, huh?"
    };

    private static final String[] fireMessages = {
            "Hot enough for you?",
            "Someone call the fire department!",
            "Do not try this at home… or anywhere."
    };

    private static final String[] lavaMessages = {
            "You’re really heating things up… literally.",
            "Molten death is always stylish.",
            "Lava: 1, You: 0."
    };

    private static final String[] genericMessages = {
            "Ouch! That looked painful.",
            "Seriously? That again?",
            "Try not to die in the same way twice!"
    };
        @SubscribeEvent
public static void onPlayerDeath(LivingDeathEvent event) {
    if (!(event.getEntity() instanceof Player player)) return;
    if (player.level().isClientSide()) return;

    String type = event.getSource().getMsgId();
    String message = getFunnyMessage(type);

    player.displayClientMessage(
        Component.literal(message),
        false
    );
}
    private static String getFunnyMessage(String type) {
        if (type == null) return randomFrom(genericMessages);

        return switch (type) {
            case "fall" -> randomFrom(fallMessages);
            case "fire", "onFire" -> randomFrom(fireMessages);
            case "lava" -> randomFrom(lavaMessages);
            case "explosion", "explosion.player", "explosion.mob" ->
                    "Boom! That didn’t go well.";
            case "magic" -> "Spell fail! Try again.";
            case "arrow", "thrown" ->
                    "Ouch! That projectile had your name on it.";
            default -> randomFrom(genericMessages);
        };
    }

    private static String randomFrom(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }
}
