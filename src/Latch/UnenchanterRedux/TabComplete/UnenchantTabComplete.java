package Latch.UnenchanterRedux.TabComplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnenchantTabComplete implements TabCompleter {

    private static final List<String> strings = new ArrayList<>();

    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        strings.clear();
        Player pa = (Player) commandSender;
        Map<Enchantment, Integer> itemInHand = pa.getInventory().getItemInMainHand().getEnchantments();
        String enchantment;
        int level;
        for (Map.Entry<Enchantment, Integer> entry : itemInHand.entrySet()) {
            enchantment = entry.getKey().getKey().getKey();
            enchantment = enchantment.substring(0, 1).toUpperCase() + enchantment.substring(1);
            level = entry.getValue();
            strings.add(enchantment + ":" + level);
        }
        try {
            return (args.length > 0) ? StringUtil.copyPartialMatches(args[0], strings, new ArrayList<>()) : null;
        } catch (IllegalArgumentException ignored){

        }
        return StringUtil.copyPartialMatches(args[0], strings, new ArrayList<>());
    }


}