package Latch.UnenchanterRedux;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return UnenchanterRedux.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            //System.out.println("player = null");
            return null;
        }
        if (!params.startsWith("ench_")) {
            //System.out.println("param ! startsWith ench_");
            return null;
        }
        String[] split = params.split("_");
        if (split.length != 2) {
            //System.out.println("split.length != 2");
            return null;
        }
        int id = 1;
        try {
            id = Integer.parseInt(split[1]) - 1;
        } catch (Exception e) {
            //System.out.println("Could not parse id");
            return null;
        }
        ItemStack item = player.getInventory().getItemInMainHand();

        Map<Enchantment, Integer> enchantments = item.getEnchantments();
        if (id >= enchantments.size()) {
            //System.out.println("Enchantments#size >= id");
            //System.out.println("Enchantments#size: " + enchantments.size());
            //System.out.println("ID: " + id);
            return null;
        }
        if (id < 0) {
            //System.out.println("ID < 0");
            return null;
        }
        List<Enchantment> enchantmentList = enchantments.keySet().stream().collect(Collectors.toList());
        Enchantment enchantment = enchantmentList.get(id);
        int level = enchantments.get(enchantment);
        return UnenchanterRedux.getInstance().getEnchantmentName(enchantment.getKey()) + " " + level;
    }
}
