package com.mortisdevelopment.autorestart;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;

public final class AutoRestart extends JavaPlugin implements Listener {

    public static long WAIT_HOURS = 12;
    public static long GRACE_MINUTES = 15;
    private LocalDateTime waitTime;
    private LocalDateTime gracePeriod;
    private boolean graceStartLogged;


    @Override
    public void onEnable() {
        // Plugin startup logic
        waitTime = LocalDateTime.now().plusHours(WAIT_HOURS);
        
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!isWaitTimeOver()) {
                return;
            }
            if (!graceStartLogged) {
                getLogger().info("Starting grace period. Will restart if no players are online for configured time.");
                graceStartLogged = true;
                return;
            }
            int playerCount = Bukkit.getOnlinePlayers().size();
            if (playerCount > 0) {
                gracePeriod = null;
                return;
            }
            if (gracePeriod == null) {
                gracePeriod = LocalDateTime.now().plusMinutes(GRACE_MINUTES);
                return;
            }
            if (isGracePeriodOver()) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }, 0L, 20L);
    }

    private boolean isWaitTimeOver() {
        return waitTime.isBefore(LocalDateTime.now());
    }

    private boolean isGracePeriodOver() {
        return gracePeriod.isBefore(LocalDateTime.now());
    }
}
