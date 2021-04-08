package ru.fiw.blockhighlight;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
    public HashMap<String, Animation> animations = new HashMap<>();
    public boolean alwaysHideBehindBlocks = false;
    private ArrayList<BukkitTask> repeats = new ArrayList<>();
    private Main main;

    public Config(Main main) {
        this.main = main;
    }

    //TODO: Check whether the values is entered correctly
    public void loadConfig() {
        main.saveDefaultConfig();
        this.stopAllRepeats();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "config.yml"));
        alwaysHideBehindBlocks = config.getBoolean("always-hide-behind-blocks");

        for (String animationName : config.getConfigurationSection("animations").getKeys(false)) {
            Animation animation = new Animation();
            int highestTick = 0;

            String worldStr = config.getString("animations." + animationName + ".world");
            World world = Bukkit.getWorld(worldStr);
            if (world == null) {
                main.getLogger().warning("No world with name '" + worldStr + "' was found, animation '" + animationName + "' was not loaded!");
                continue;
            }
            animation.viewDistanceSquared = config.getInt("animations." + animationName + ".view-distance-squared", 4096);

            for (String tickStr : config.getConfigurationSection("animations." + animationName + ".frames").getKeys(false)) {
                int tick = Integer.parseInt(tickStr);
                if (tick > highestTick) highestTick = tick;

                List<String> framesListStr = config.getStringList("animations." + animationName + ".frames." + tickStr);
                ArrayList<BlockHighlight> frames = new ArrayList<>();

                for (String animationFullStr : framesListStr) {
                    if (animationFullStr.startsWith("hide-behind-blocks")) {
                        frames.add(BlockHighlight.getHideBehindBlocks(Integer.parseInt(animationFullStr.split(" ")[1])));
                        continue;
                    }
                    String[] animationRaw = animationFullStr.split(", ?");
                    String[] coords = animationRaw[0].split(" ");
                    Color color = hex2Rgb(animationRaw[1], Integer.parseInt(animationRaw[2]));
                    String text = animationRaw[3];
                    int time = Integer.parseInt(animationRaw[4]);

                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    int z = Integer.parseInt(coords[2]);
                    frames.add(new BlockHighlight(x, y, z, color.getRGB(), text, time));

                    //This is necessary to determine whether it will be rendered for the player
                    //Checking whether the player sees each hightlighted block is probably too expensive so it is only determined by the first block
                    //TODO: Get animation location not by the first element or come up with a new way to determine if the player sees highlighted block
                    if (animation.location == null) {
                        animation.location = new Location(world, x, y, z);
                    }
                }
                animation.frames.put(tick, frames);
            }
            animation.lastTick = highestTick;

            animations.put(animationName, animation);
        }

        for (String key : config.getConfigurationSection("repeat-animations").getKeys(false)) {
            String animationName = config.getString("repeat-animations." + key + ".animation");
            int repeatEvery = config.getInt("repeat-animations." + key + ".repeat-every");

            startRepeat(animationName, repeatEvery);
        }
    }

    private void stopAllRepeats() {
        for (BukkitTask repeat : repeats) {
            repeat.cancel();
        }
    }

    private void startRepeat(String animationName, int repeatEvery) {
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bh run " + animationName + " all");
            }
        }.runTaskTimer(main, repeatEvery, repeatEvery);
        repeats.add(bukkitTask);
    }

    //https://stackoverflow.com/questions/4129666/how-to-convert-hex-to-rgb-using-java/4129692
    private static Color hex2Rgb(String colorStr, int transparency) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16), transparency);
    }
}
