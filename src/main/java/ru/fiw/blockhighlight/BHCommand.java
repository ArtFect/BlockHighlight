package ru.fiw.blockhighlight;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class BHCommand implements CommandExecutor, TabCompleter {
    private Config config;
    private Main main;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length < 1){
            return false;
        }
        if (args[0].equals("reload") && commandSender.hasPermission("blockhighlight.reload")) {
            config.loadConfig();
            Util.sendStopToAll();
            if (config.alwaysHideBehindBlocks) {
                Util.sendHideBehindBlocksAlwaysToAll();
            }
            commandSender.sendMessage(ChatColor.GREEN + "Config reloaded!");
        } else if (args[0].equals("run") && commandSender.hasPermission("blockhighlight.run")) {
            if (args.length < 2) {
                commandSender.sendMessage(ChatColor.RED + "Specify the name of the animation. Example usage /bh run test");
                return true;
            }

            String animName = args[1];
            Animation animation = config.animations.get(animName);
            if (animation == null) {
                commandSender.sendMessage(ChatColor.RED + "No animation with name " + animName + " was found!");
                return true;
            }

            if (args.length == 2) {
                main.startAnimation((Player) commandSender, animation);
            } else {
                if (args[2].equals("all")) {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        main.startAnimation(pl, animation);
                    }
                    return true;
                }

                Player player = Bukkit.getPlayerExact(args[2]);
                if (player != null) {
                    main.startAnimation(player, animation);
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Player " + args[2] + " was not found!");
                    return true;
                }
            }
        } else if (args[0].equals("stop") && commandSender.hasPermission("blockhighlight.stop")) {
            Player pl;
            if (args.length < 2) {
                if(commandSender instanceof Player){
                    pl = (Player) commandSender;
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Can't stop animations for console. Specify the player to stop the animation for or 'all'");
                    return true;
                }
            } else {
                if (args[1].equals("all")) {
                    Util.sendStopToAll();
                    if (config.alwaysHideBehindBlocks) {
                        Util.sendHideBehindBlocksAlwaysToAll();
                    }
                    return true;
                }

                pl = Bukkit.getPlayerExact(args[1]);
                if(pl == null){
                    commandSender.sendMessage(ChatColor.RED + "Player " + args[1] + " was not found!");
                    return true;
                }
            }

            Util.sendStop(pl);
            if (config.alwaysHideBehindBlocks) {
                Util.sendHideBehindBlocksAlways(pl);
            }
        } else if(args[0].equals("list") && commandSender.hasPermission("blockhighlight.list")){
            commandSender.sendMessage("BlockHighlight animations (" + ChatColor.GREEN + config.animations.size() + ChatColor.WHITE + "):");
            for(String name : config.animations.keySet()){
                Animation an = config.animations.get(name);
                Location loc = an.location;
                commandSender.sendMessage(ChatColor.WHITE + " - " + ChatColor.YELLOW + name + ChatColor.WHITE +" (x:" + loc.getBlockX() + ", y:" + loc.getBlockY() + ", z:" + loc.getBlockZ() + ", world: \"" + loc.getWorld().getName() + "\")");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("run", "stop", "reload", "list");
        } else if (args.length == 2) {
            if (args[0].equals("run")) {
                return Lists.newArrayList(config.animations.keySet());
            } else if (args[0].equals("stop")) {
                //Shows online player list
                return null;
            }
        } else if (args.length == 3) {
            if (args[0].equals("run")) {
                //Shows online player list
                return null;
            }
        }
        return Collections.emptyList();
    }
}