package com.repance.warsim;

import com.repance.warsim.commands.WarSimCommand;
import com.repance.warsim.game.Game;
import com.repance.warsim.listeners.CombatListener;
import com.repance.warsim.listeners.PlayerCommandListener;
import com.repance.warsim.listeners.PlayerEliminationListener;
import com.repance.warsim.listeners.PlayerJoinResetListener;
import com.repance.warsim.listeners.PlayerMoveListener;
import com.repance.warsim.listeners.PregameItemListener;
import com.repance.warsim.listeners.PregameProtectionListener;
import com.repance.warsim.managers.GameManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.repance.warsim.listeners.PlayerQuitListener;

public class WarSim extends JavaPlugin {

    private static WarSim instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        new GameManager();

        if (getCommand("ws") != null) {
            getCommand("ws").setExecutor(new WarSimCommand());
        }

        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new PregameItemListener(), this);
        getServer().getPluginManager().registerEvents(new PregameProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinResetListener(GameManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new PlayerEliminationListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        Game game = GameManager.getInstance().getCurrentGame();
        if (game != null) {
            game.visuals().resetAllOnlinePlayers();
            game.visuals().reinitializeTeams();
        }

        getLogger().info("WarSim is gestart!");
    }

    @Override
    public void onDisable() {
        Game game = GameManager.getInstance() != null ? GameManager.getInstance().getCurrentGame() : null;
        if (game != null) {
            game.visuals().resetAllOnlinePlayers();
            game.visuals().resetWarSimTeams();
        }

        getLogger().info("WarSim is gestopt!");
    }

    public static WarSim getInstance() {
        return instance;
    }
}