package com.repance.warsim.service;

import com.repance.warsim.game.Game;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;

public class GameMessageService {

    private final Game game;
    private final LegacyComponentSerializer legacySerializer;

    public GameMessageService(Game game) {
        this.game = game;
        this.legacySerializer = LegacyComponentSerializer.legacySection();
    }

    public void broadcastToQueued(String message) {
        for (Player player : game.players().getQueuedOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public void broadcastToAllParticipants(String message) {
        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public void broadcastSoundToParticipants(Sound sound, float volume, float pitch) {
        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void broadcastTeamComposition() {
        broadcastToQueued("§cTeam A: §f" + game.getTeamA().size() + " §7| §9Team B: §f" + game.getTeamB().size());
    }

    public void showCountdownTitleToQueued(int seconds, String subtitle) {
        String color = seconds <= 3 ? "§c" : seconds <= 5 ? "§e" : "§a";

        Title title = Title.title(
                legacy(color + seconds),
                legacy(subtitle),
                Title.Times.times(
                        Duration.ofMillis(0),
                        Duration.ofMillis(1100),
                        Duration.ofMillis(0)
                )
        );

        for (Player player : game.players().getQueuedOnlinePlayers()) {
            // GEEN clearTitle/resetTitle hier
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, seconds <= 3 ? 1.5f : 1.0f);
        }
    }

    public void showCountdownTitleToParticipants(int seconds, String subtitle) {
        String color = seconds <= 3 ? "§c" : seconds <= 5 ? "§e" : "§a";

        Title title = Title.title(
                legacy(color + seconds),
                legacy(subtitle),
                Title.Times.times(
                        Duration.ofMillis(0),
                        Duration.ofMillis(1100),
                        Duration.ofMillis(0)
                )
        );

        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            // GEEN clearTitle/resetTitle hier
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, seconds <= 3 ? 1.5f : 1.0f);
        }
    }

    public void showGoTitle() {
        Title title = Title.title(
                legacy("§a§lGO!"),
                Component.empty(),
                Title.Times.times(
                        Duration.ofMillis(100),
                        Duration.ofMillis(900),
                        Duration.ofMillis(150)
                )
        );

        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            player.resetTitle();
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1.0f, 1.0f);
        }
    }

    public void showStopTitle() {
        Title title = Title.title(
                legacy("§cGESTOPT"),
                legacy("§7Game is gestopt"),
                Title.Times.times(
                        Duration.ofMillis(100),
                        Duration.ofMillis(1400),
                        Duration.ofMillis(200)
                )
        );

        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            player.clearTitle();
            player.showTitle(title);
        }
    }

    public void showCancelledTitleToQueued() {
        Title title = Title.title(
                legacy("§cGECANCELD"),
                legacy("§7Te weinig spelers"),
                Title.Times.times(
                        Duration.ofMillis(100),
                        Duration.ofMillis(1400),
                        Duration.ofMillis(200)
                )
        );

        for (Player player : game.players().getQueuedOnlinePlayers()) {
            player.clearTitle();
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
        }
    }

    public void showWinTitle(Player player) {
        Title title = Title.title(
                legacy("§a§lGEWONNEN"),
                legacy("§7Jouw team heeft gewonnen!"),
                Title.Times.times(
                        Duration.ofMillis(150),
                        Duration.ofMillis(3500),
                        Duration.ofMillis(250)
                )
        );

        player.clearTitle();
        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    public void showLoseTitle(Player player) {
        Title title = Title.title(
                legacy("§c§lHELAAS"),
                legacy("§7Jouw team heeft verloren."),
                Title.Times.times(
                        Duration.ofMillis(150),
                        Duration.ofMillis(3500),
                        Duration.ofMillis(250)
                )
        );

        player.clearTitle();
        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.8f);
    }

    public void showDrawTitleToParticipants() {
        Title title = Title.title(
                legacy("§6§lGELIJKSPEL"),
                legacy("§7Tijd is om"),
                Title.Times.times(
                        Duration.ofMillis(150),
                        Duration.ofMillis(3500),
                        Duration.ofMillis(250)
                )
        );

        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            player.clearTitle();
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        }
    }

    public void sendMatchTimerActionBar(Player player, int secondsLeft) {
        NamedTextColor color;

        if (secondsLeft <= 10) {
            color = NamedTextColor.RED;
        } else if (secondsLeft <= 30) {
            color = NamedTextColor.GOLD;
        } else {
            color = NamedTextColor.YELLOW;
        }

        player.sendActionBar(Component.text("Nog " + formatTime(secondsLeft), color));
    }

    public void sendMatchTimerActionBarToParticipants(int secondsLeft) {
        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            sendMatchTimerActionBar(player, secondsLeft);
        }
    }

    public void sendReturnCountdownActionBar(Player player, int secondsLeft) {
        player.sendActionBar(
                Component.text("Even geduld, je wordt teruggestuurd naar de lobby...", NamedTextColor.YELLOW)
        );
    }

    public void sendReturnCountdownActionBarToParticipants(int secondsLeft) {
        for (Player player : game.players().getAllParticipantOnlinePlayers()) {
            sendReturnCountdownActionBar(player, secondsLeft);
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private Component legacy(String text) {
        return legacySerializer.deserialize(text);
    }
}