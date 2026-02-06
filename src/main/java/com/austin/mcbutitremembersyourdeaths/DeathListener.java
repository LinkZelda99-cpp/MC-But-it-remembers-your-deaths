package com.austin.mcbutitremembersyourdeaths;

import java.util.HashMap;
import java.util.Map;
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

    private static final Map<String, Integer> deathCounts = new HashMap<>();

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
        if (player.level().isClientSide()) return; // Only run on server

        String type = event.getSource().getMsgId();
        String key = player.getUUID().toString() + ":" + type;

        // Increment death count
        int count = deathCounts.getOrDefault(key, 0) + 1;
        deathCounts.put(key, count);

        // Build message with “remembers” count
        String baseMessage = getFunnyMessage(type);
        String finalMessage = baseMessage + " (That's " + count + (count == 1 ? " time" : " times") + " now)";

        // Display message in chat
        player.displayClientMessage(
                Component.literal(finalMessage),
                false
        );
    }

    // Picks a random funny message based on death type
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

    // Picks a random element from an array
    private static String randomFrom(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }
}

