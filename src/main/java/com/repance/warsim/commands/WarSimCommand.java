package com.repance.warsim.commands;

import com.repance.warsim.managers.GameManager;
import com.repance.warsim.utils.LocationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarSimCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Alleen spelers kunnen dit command gebruiken!");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join" -> GameManager.getInstance().joinGame(player);
            case "leave" -> GameManager.getInstance().leaveGame(player);
            case "start" -> GameManager.getInstance().startCountdown(player);

            case "forcestart" -> {
                if (!player.hasPermission("warsim.admin")) {
                    noPerm(player);
                    return true;
                }
                GameManager.getInstance().forceStart(player);
            }

            case "stop" -> {
                if (!player.hasPermission("warsim.admin")) {
                    noPerm(player);
                    return true;
                }
                GameManager.getInstance().forceStop(player);
            }

            case "setpregamelobby" -> {
                if (!player.hasPermission("warsim.admin")) {
                    noPerm(player);
                    return true;
                }
                LocationUtil.saveLocation("locations.pregame", player.getLocation());
                player.sendMessage("§aPregame lobby opgeslagen.");
            }

            case "setlobby" -> {
                if (!player.hasPermission("warsim.admin")) {
                    noPerm(player);
                    return true;
                }
                LocationUtil.saveLocation("locations.return", player.getLocation());
                player.sendMessage("§aLobby (return spawn) opgeslagen.");
            }

            case "setspawn" -> {
                if (!player.hasPermission("warsim.admin")) {
                    noPerm(player);
                    return true;
                }

                if (args.length < 3) {
                    player.sendMessage("§cGebruik: /warsim setspawn team <a|b>");
                    return true;
                }

                String type = args[1].toLowerCase();
                String team = args[2].toLowerCase();

                if (!type.equals("team")) {
                    player.sendMessage("§cGebruik: /warsim setspawn team <a|b>");
                    return true;
                }

                switch (team) {
                    case "a" -> {
                        LocationUtil.saveLocation("arena.spawn-a", player.getLocation());
                        player.sendMessage("§aSpawn voor §lTeam A §aopgeslagen.");
                    }
                    case "b" -> {
                        LocationUtil.saveLocation("arena.spawn-b", player.getLocation());
                        player.sendMessage("§aSpawn voor §lTeam B §aopgeslagen.");
                    }
                    default -> player.sendMessage("§cGebruik: /warsim setspawn team <a|b>");
                }
            }

            default -> sendUsage(player);
        }

        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage("§eWarSim commands:");
        player.sendMessage("§f/warsim join");
        player.sendMessage("§f/warsim leave");
        player.sendMessage("§f/warsim start");
        player.sendMessage("§f/warsim stop");

        if (player.hasPermission("warsim.admin")) {
            player.sendMessage("§7Admin:");
            player.sendMessage("§f/warsim forcestart");
            player.sendMessage("§f/warsim stop");
            player.sendMessage("§f/warsim setpregamelobby");
            player.sendMessage("§f/warsim setlobby");
            player.sendMessage("§f/warsim setspawn team a");
            player.sendMessage("§f/warsim setspawn team b");
        }
    }

    private void noPerm(Player player) {
        player.sendMessage("§cGeen permissie.");
    }
}