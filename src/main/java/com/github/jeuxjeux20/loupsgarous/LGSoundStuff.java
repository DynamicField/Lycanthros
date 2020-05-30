package com.github.jeuxjeux20.loupsgarous;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class LGSoundStuff {
    private LGSoundStuff() {
    }

    public static void ding(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }

    public static void enchant(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
    }

    /**
     * It goes "SLURRRP".
     *
     * @param player the player to play the sound for
     */
    public static void remove(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    }

    /**
     * It goes "naaah", or "huh".
     *
     * @param player the player to play the sound for
     */
    public static void nah(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
    }

    /**
     * The noteblock "pling" sound you have heard at least once while playing on Minecraft servers.
     * @param player the player to play the sound for
     */
    public static void pling(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
    }
}
