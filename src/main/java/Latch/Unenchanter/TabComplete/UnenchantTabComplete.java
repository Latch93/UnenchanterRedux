package Latch.Unenchanter.TabComplete;

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
        int level;
        for (Map.Entry<Enchantment, Integer> entry : itemInHand.entrySet()) {
            String[] temp1 = entry.toString().split(":");
            String[] temp2 = temp1[1].split(",");
            level = entry.getValue();
            strings.add(temp2[0] + ":" + level);
        }
        try {
            return (args.length > 0) ? StringUtil.copyPartialMatches(args[0], strings, new ArrayList<>()) : null;
        } catch (IllegalArgumentException ignored){

        }
        return StringUtil.copyPartialMatches(args[0], strings, new ArrayList<>());
    }


}