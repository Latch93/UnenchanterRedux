package Latch.Unenchanter;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Placeholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "unenchanter";
    }

    @Override
    public @NotNull String getAuthor() {
        return "mfnalex";
    }

    @Override
    public @NotNull String getVersion() {
        return Unenchanter.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return null;
        }
        if (!params.startsWith("ench_")) {
            return null;
        }
        String[] split = params.split("_");
        if (split.length != 2) {
            return null;
        }
        int id = 1;
        try {
            id = Integer.parseInt(split[1]) - 1;
        } catch (Exception e) {
            return null;
        }
        ItemStack item = player.getInventory().getItemInMainHand();

        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        if (id >= enchantments.size()) {
            return null;
        }
        if (id < 0) {
            return null;
        }
        List<Enchantment> enchantmentList = new ArrayList<>(enchantments.keySet());
        Enchantment enchantment = enchantmentList.get(id);
        int level = enchantments.get(enchantment);
        return Unenchanter.getInstance().getEnchantmentName(enchantment.getKey()) + " " + level;
    }
}
