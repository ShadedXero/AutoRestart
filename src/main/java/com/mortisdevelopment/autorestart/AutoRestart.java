package com.mortisdevelopment.autorestart;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;

public final class AutoRestart extends JavaPlugin implements Listener {

    public static long WAIT_SECONDS = 30;
    public static long GRACE_SECONDS = 15;
    private LocalDateTime waitTime;
    private LocalDateTime gracePeriod;
    private boolean graceStartLogged;


    @Override
    public void onEnable() {
        // Plugin startup logic
        waitTime = LocalDateTime.now().plusSeconds(WAIT_SECONDS);

        new BukkitRunnable() {
            @Override
            public void run() {
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
                    gracePeriod = LocalDateTime.now().plusSeconds(GRACE_SECONDS);
                    return;
                }
                if (isGracePeriodOver()) {
                    cancel();
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    return;
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private boolean isWaitTimeOver() {
        return waitTime.isBefore(LocalDateTime.now());
    }

    private boolean isGracePeriodOver() {
        return gracePeriod.isBefore(LocalDateTime.now());
    }
}
