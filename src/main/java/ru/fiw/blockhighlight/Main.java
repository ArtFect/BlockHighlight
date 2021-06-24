package ru.fiw.blockhighlight;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends JavaPlugin implements Listener {
    private static ArrayList<RunningAnimation> runningAnimations = new ArrayList<>();
    private Config config;

    public void onEnable() {
        config = new Config(this);
        config.loadConfig();
        if (config.alwaysHideBehindBlocks) {
            Util.sendHideBehindBlocksAlwaysToAll();
        }
        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("blockhightlight").setExecutor(new BHCommand(config, this));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Iterator<RunningAnimation> it = runningAnimations.iterator(); it.hasNext(); ) {
                    RunningAnimation anim = it.next();
                    anim.render();

                    if (anim.isLastTick()) {
                        it.remove();
                    } else {
                        anim.addTick();
                    }
                }
            }
        }.runTaskTimer(this, 1, 1);
    }

    public void startAnimation(Player pl, Animation animation) {
        runningAnimations.add(new RunningAnimation(animation, pl));
    }

    @EventHandler
    public void sendHideOnJoin(PlayerJoinEvent e) {
        if (config.alwaysHideBehindBlocks) {
            Util.sendHideBehindBlocksAlways(e.getPlayer());
        }
    }
}