package com.austin.mcbutitremembersyourdeaths;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.austin.mcbutitremembersyourdeaths.DeathMessageStore;


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
    if (player.level().isClientSide()) return;

    String type = event.getSource().getMsgId();
    String message = getFunnyMessage(type);

    Component component = Component.literal(message);

    // Send to chat
    player.displayClientMessage(component, false);

    // Store for death screen (client will read this)
    DeathMessageStore.LAST_DEATH_MESSAGE = component;
    
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
    @SubscribeEvent
public static void onRespawn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
    if (event.getEntity().level().isClientSide()) {
        DeathMessageStore.LAST_DEATH_MESSAGE = null;
    }
}
}

