package com.austin.mcbutitremembersyourdeaths;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.nbt.CompoundTag;
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

    // Messages per death type, 5 tiers each
    private static final Map<String, String[][]> deathMessages = new HashMap<>();
    static {
        deathMessages.put("fall", new String[][] {
                {"You fell from a high place… again.", "Oof! Watch your step."}, 
                {"Gravity called. You didn’t answer.", "Another fall… seriously?"}, 
                {"This is getting repetitive… you fell… again!", "Falling seems to be your specialty."}, 
                {"Are you trying to touch the ground again?", "The ground misses you."}, 
                {"Gravity is relentless… and so are you.", "You’re on a first-name basis with the ground."},
                {"Dying over 50 times from falling? Seems about right.", "You have become one with the ground."},
                {"You have fallen again, and again, and again, and again, and again and...", "You are definitely falling on purpose by now...", "You have died over a 100 times by... falling."}
        });

        deathMessages.put("fire", new String[][] {
                {"Hot enough for you?", "Something's buring...", "Left with 3rd degree burns."},
                {"Someone call the fire department!", "Are you playing with fire?"},
                {"Stop playing with fire… literally.", "You might be immune to heat by now."},
                {"The flames recognize you now.", "Fire thinks it owns you."},
                {"Fire and you: eternal rivals.", "You have a burning reputation now."},
                {"Fire and you have a story. It is blazing good.", "Fire must be everywhere..."},
                {"The story of fire and you is now written in every atom of the universe"}
        });

        deathMessages.put("lava", new String[][] {
                {"You’re heating things up… literally.", "A little lava never hurt anyone… right?"},
                {"Molten death is stylish.", "That lava is looking familiar."},
                {"Lava: 1, You: 0.", "At this point, lava hates you personally."},
                {"Lava has marked you as its nemesis.", "The lava watches… and waits."},
                {"Lava knows your name.", "You are the chosen of molten death."},
                {"The lava now feels bad... just kidding.", "Try to swim next time. Or maybe not. It doesn't really matter."},
                {"You have died from lava over 100 times.", "Lava is very happy at this point"}
        });

        deathMessages.put("explosion", new String[][] {
                {"Boom! That didn’t go well.", "Kaboom!"},
                {"Are you trying to make a new crater?", "Explosives are tricky, huh?"},
                {"Explosions love you more than life does…", "You are one with TNT now."},
                {"Every blast greets you personally.", "You are a walking boom signal."},
                {"Explosions bow to your power… sort of.", "You and TNT: a legendary story."},
                {"The TNT is wondering how this is even possible. You are probably also."},
                {"You have been blasted into a million pieces over 100 times."}
        });

        deathMessages.put("mob", new String[][] {
                {"The mob wins… this time.", "You got mobbed!"},
                {"Maybe fight back?", "Mobs are relentless."},
                {"The mobs are laughing at you.", "You’re their favorite target."},
                {"You are famous among mobs now.", "They know your weaknesses."},
                {"Legendary target: you.", "Mobs speak your name in fear… or laughter."},
                {"The mobs have defeated you over 50 times.", "The mobs talk to each other about killing you. And they have succeeded."},
                {"Over 100 deaths from mobs. Wow.", "You are the mobs punching bag."}
        });
    }

    // Hardcore-specific death messages by death type
    private static final Map<String, String[]> hardcoreMessages = new HashMap<>();
    static {
        hardcoreMessages.put("fall", new String[] {
                "Hardcore Fall! Gravity always wins.",
                "You plummeted to your doom… Hardcore style!",
                "One life, one fall… Hardcore doesn’t forgive."
        });

        hardcoreMessages.put("fire", new String[] {
                "Hardcore Fire! You burn forever.",
                "Flames claimed you in Hardcore.",
                "One life, incinerated. Hardcore style!"
        });

        hardcoreMessages.put("lava", new String[] {
                "Hardcore Lava! Molten fate is cruel.",
                "You are melted in Hardcore fashion.",
                "Lava and Hardcore: an eternal combo."
        });

        hardcoreMessages.put("explosion", new String[] {
                "Hardcore Boom! TNT wins.",
                "Explosive Hardcore demise achieved.",
                "Kaboom! No second chances in Hardcore."
        });

        hardcoreMessages.put("mob", new String[] {
                "Hardcore mobs got you good.",
                "No respawns here, mobs win forever.",
                "One life, one death… mobs rejoice."
        });
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        String type = normalizeDeathType(event.getSource().getMsgId());

        // Hardcore world - select hardcore message by type
        if (player.level().getLevelData().isHardcore()) {
            String[] messages = hardcoreMessages.getOrDefault(type, hardcoreMessages.get("mob"));
            String msg = messages[random.nextInt(messages.length)];
            player.displayClientMessage(Component.literal(msg), false);
            DeathMessageStore.LAST_DEATH_MESSAGE = Component.literal(msg);
            return;
        }

        CompoundTag data = player.getPersistentData();
        CompoundTag deathData = data.getCompoundOrEmpty("FunnyDeathCounts");

        // Increment death count safely
        int count = deathData.getIntOr(type, 0) + 1;
        deathData.putInt(type, count);
        data.put("FunnyDeathCounts", deathData);

        // If this is the first time dying this way, use vanilla message
        if (count == 1) {
            DeathMessageStore.LAST_DEATH_MESSAGE = null;
            return;
        }

        // Compute current tier (0–4)
        int tier;
        if (count <= 2) tier = 0;
        else if (count <= 5) tier = 1;
        else if (count <= 10) tier = 2;
        else if (count <= 20) tier = 3;
        else if (count <= 50) tier = 4;
        else tier = 5;

        // Randomly select a message from any tier up to current tier
        String[][] messages = deathMessages.getOrDefault(type, deathMessages.get("mob"));
        int pickTier = random.nextInt(tier + 1); // random tier from 0..tier
        String[] tierMsgs = messages[Math.min(pickTier, messages.length - 1)];
        String message = tierMsgs[random.nextInt(tierMsgs.length)];

        // Display in chat
        player.displayClientMessage(Component.literal(message), false);

        // Store for DeathScreen
        DeathMessageStore.LAST_DEATH_MESSAGE = Component.literal(message);
    }

    private static String normalizeDeathType(String raw) {
        if (raw == null) return "mob";
        return switch (raw) {
            case "fire", "onFire" -> "fire";
            case "explosion.player", "explosion.mob", "explosion" -> "explosion";
            default -> raw;
        };
    }
}