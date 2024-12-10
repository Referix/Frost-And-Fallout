package org.referix.lotusOffSeasonV2.command;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractCommand implements CommandExecutor, TabCompleter {
    public AbstractCommand(String command){
        PluginCommand pluginCommand = LotusOffSeasonV2.getInstance().getCommand(command);
        if (pluginCommand != null){
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    public abstract boolean execute(CommandSender sender, String label, String[] args);

    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        execute(sender, label, args);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return filter(complete(commandSender, strings), strings);
    }

    private List<String> filter(List<String> list, String[] args) {
        if(list == null) return null;
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for (String arg : list) {
            if(arg.toLowerCase().startsWith(last.toLowerCase())) result.add(arg);
        }
        return result;
    }
}

