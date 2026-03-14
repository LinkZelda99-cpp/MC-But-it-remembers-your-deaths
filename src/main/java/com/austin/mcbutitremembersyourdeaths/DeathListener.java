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
                {"You fell from a high place… again.", "Oof! Watch your step."}, // tier 0
                {"Gravity called. You didn’t answer.", "Another fall… seriously?"}, // tier 1
                {"This is getting repetitive… you fell… again!", "Falling seems to be your specialty."}, // tier 2
                {"Are you trying to touch the ground again?", "The ground misses you."}, // tier 3
                {"Gravity is relentless… and so are you.", "You’re on a first-name basis with the ground."} // tier 4
        });

        deathMessages.put("fire", new String[][] {
                {"Hot enough for you?", "You're on fire!"},
                {"Someone call the fire department!", "Are you playing with fire?"},
                {"Stop playing with fire… literally.", "You might be immune to heat by now."},
                {"The flames recognize you now.", "Fire thinks it owns you."},
                {"Fire and you: eternal rivals.", "You have a burning reputation now."}
        });

        deathMessages.put("lava", new String[][] {
                {"You’re heating things up… literally.", "A little lava never hurt anyone… right?"},
                {"Molten death is stylish.", "That lava is looking familiar."},
                {"Lava: 1, You: 0.", "At this point, lava hates you personally."},
                {"Lava has marked you as its nemesis.", "The lava watches… and waits."},
                {"Lava knows your name.", "You are the chosen of molten death."}
        });

        deathMessages.put("explosion", new String[][] {
                {"Boom! That didn’t go well.", "Kaboom!"},
                {"Are you trying to make a new crater?", "Explosives are tricky, huh?"},
                {"Explosions love you more than life does…", "You are one with TNT now."},
                {"Every blast greets you personally.", "You are a walking boom signal."},
                {"Explosions bow to your power… sort of.", "You and TNT: a legendary story."}
        });

        deathMessages.put("magic", new String[][] {
                {"Spell fail! Try again.", "Magic is hard!"},
                {"Magic got you again.", "The wizard is not impressed."},
                {"You’ve angered the wizard… again.", "Your magic needs work."},
                {"Magic whispers your name… sadly.", "Even magic laughs at you now."},
                {"The arcane remembers you.", "You are infamous in magical circles."}
        });

        deathMessages.put("arrow", new String[][] {
                {"Ouch! That projectile has your name on it.", "Target practice gone wrong."},
                {"You’re like a target dummy.", "Arrows everywhere!"},
                {"Seriously, maybe stay inside.", "Stop being arrow bait."},
                {"You’ve become one with arrow physics.", "Every arrow has a memo for you."},
                {"Arrows salute your dedication.", "You are the chosen of the bow."}
        });

        deathMessages.put("inWall", new String[][] {
                {"Stuck again?", "You shouldn’t hug walls so tightly."},
                {"I think the wall is out to get you.", "Walls 1, You 0."},
                {"Do you even have air?", "The wall wins… again."},
                {"Walls remember your stubbornness.", "Your friendship with walls is legendary."},
                {"Walls bow to your persistence.", "You have a history with walls now."}
        });

        deathMessages.put("drown", new String[][] {
                {"Water is not your friend.", "Splash! Not in a good way."},
                {"You know you can swim, right?", "Maybe learn to breathe underwater."},
                {"Water hates you personally…", "Seriously, avoid water."},
                {"Even oceans sigh at your arrival.", "The deep sea remembers you."},
                {"The sea marks you as its mortal enemy.", "Water bows reluctantly."}
        });

        deathMessages.put("starve", new String[][] {
                {"You should have eaten.", "Hunger is no joke."},
                {"Hunger is real… and fatal.", "Starving again?"},
                {"Starvation notices you more each time.", "Maybe eat something…"},
                {"The pantry laughs at your attempts.", "Hunger is patient… unlike you."},
                {"You are legendary in famine circles.", "Your stomach has a fan club."}
        });

        deathMessages.put("mob", new String[][] {
                {"The mob wins… this time.", "You got mobbed!"},
                {"Maybe fight back?", "Mobs are relentless."},
                {"The mobs are laughing at you.", "You’re their favorite target."},
                {"You are famous among mobs now.", "They know your weaknesses."},
                {"Legendary target: you.", "Mobs speak your name in fear… or laughter."}
        });
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        String type = normalizeDeathType(event.getSource().getMsgId());

        CompoundTag data = player.getPersistentData();
        CompoundTag deathData = data.getCompoundOrEmpty("FunnyDeathCounts");

        // // Increment death count safely
        // int count = deathData.getIntOr(type, 0) + 1;
        // deathData.putInt(type, count);
        // data.put("FunnyDeathCounts", deathData);

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
        else tier = 4;

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