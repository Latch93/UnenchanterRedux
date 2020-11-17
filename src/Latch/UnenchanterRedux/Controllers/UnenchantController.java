package Latch.UnenchanterRedux.Controllers;

import Latch.UnenchanterRedux.ConfigManagers.UnenchantConfigManager;
import Latch.UnenchanterRedux.UnenchanterRedux;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

public class UnenchantController implements CommandExecutor {

    public static FileConfiguration UnenchantCfg = UnenchantConfigManager.unenchantCfg;
    private static int configExpCost;
    private static int configMoneyCost;
    private static boolean configDoesCostExp;
    private static boolean configDoesCostMoney;
    private static boolean isUnenchantAllowed;
    private static boolean doesHaveBook;
    private static boolean doesContainEnchant;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static boolean doesPlayerHaveEnoughLevels;
    private static boolean doesPlayerHaveEnoughMoney;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player pa = (Player) commandSender;
        NamespacedKey enchantment;
        int level;
        int count = 0;
        if (args.length == 0){
            pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "You need to select the enchantment on an item to unenchant.");
        }
        else if (args.length == 1) {
            for (ItemStack stack : pa.getInventory().getContents()) {
                if (count == 0){
                    try {
                        if(stack.getType().equals(Material.BOOK)){
                            count = 1;
                            doesHaveBook = true;
                            for (Map.Entry<Enchantment, Integer> entry : pa.getInventory().getItemInMainHand().getEnchantments().entrySet()) {
                                enchantment = entry.getKey().getKey();
                                String enc = enchantment.getKey();
                                level = entry.getValue();
                                String selection = args[0];
                                String[] selectionArr = selection.split(":");
                                if (selectionArr[0].equalsIgnoreCase(enc.toString())){
                                    int configCounter = 1;
                                    doesContainEnchant = true;
                                    for(String enchantFromConfig : UnenchantCfg.getConfigurationSection("enchants").getKeys(false)) {
                                        String[] placeholder = enchantFromConfig.split("---");
                                        String configEnchantName = placeholder[0];
                                        int configEnchantLevel = Integer.parseInt(placeholder[1]);
                                        if (configEnchantName.equalsIgnoreCase(enc) && configEnchantLevel == level){
                                            configExpCost = UnenchantCfg.getInt("enchants." + enchantFromConfig + ".levelCost");
                                            configMoneyCost = UnenchantCfg.getInt("enchants." + enchantFromConfig + ".moneyCost");
                                            configDoesCostExp = UnenchantCfg.getBoolean("enchants." + enchantFromConfig + ".doesCostLevel");
                                            configDoesCostMoney = UnenchantCfg.getBoolean("enchants." + enchantFromConfig + ".doesCostMoney");
                                            isUnenchantAllowed = UnenchantCfg.getBoolean("enchants." + enchantFromConfig + ".isUnenchantAllowed");
                                        }
                                        configCounter++;
                                    }
                                    doesHaveEnoughMoney(pa, configMoneyCost);
                                    doesHaveEnoughLevels(pa, configExpCost);
                                    if (Boolean.TRUE.equals(isUnenchantAllowed)){
                                        if (Boolean.TRUE.equals(configDoesCostExp) && Boolean.FALSE.equals(configDoesCostMoney)) {
                                            if (Boolean.TRUE.equals(doesPlayerHaveEnoughLevels)) {
                                                requireLevelCheck(stack, pa, enchantment, level);
                                                removeEnchant(stack, pa, enchantment, level);
                                            }
                                            else {
                                                pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "You do not have enough levels to unenchant. Levels required: " + ChatColor.GOLD + configExpCost);
                                            }
                                        }
                                        if (Boolean.TRUE.equals(configDoesCostMoney) && Boolean.FALSE.equals(configDoesCostExp)){
                                            if (Boolean.TRUE.equals(doesPlayerHaveEnoughMoney)){
                                                requireMoneyCheck(stack, pa, enchantment, level);
                                                removeEnchant(stack, pa, enchantment, level);
                                            }
                                            else {
                                                pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "You do not have enough money to unenchant. Cost required: " + ChatColor.GOLD + "$" + configMoneyCost);
                                            }
                                        }
                                        if (Boolean.TRUE.equals(configDoesCostMoney) && Boolean.TRUE.equals(configDoesCostExp)){
                                            if (Boolean.FALSE.equals(doesPlayerHaveEnoughMoney)){
                                                pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "You do not have enough money to unenchant. Cost required: " + ChatColor.GOLD + "$" + configMoneyCost);
                                            }
                                            if (Boolean.FALSE.equals(doesPlayerHaveEnoughLevels)){
                                                pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "You have enough levels to unenchant this item. Levels required: " + ChatColor.GOLD + configExpCost);
                                            }
                                            if (Boolean.TRUE.equals(doesPlayerHaveEnoughMoney) && Boolean.TRUE.equals(doesPlayerHaveEnoughLevels)) {
                                                requireMoneyCheck(stack, pa, enchantment, level);
                                                requireLevelCheck(stack, pa, enchantment, level);
                                                removeEnchant(stack, pa, enchantment, level);
                                            }
                                        }
                                    }
                                    else {
                                        pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "This enchantment is not allowed to be unenchanted.");
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException ignore) {
                    }
                }
            }
        }
        else {
            pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Too many arguments entered.");
            pa.sendMessage(ChatColor.GRAY + "Enter command like this -> /unenchant [enchantment]");
        }
        if (!Boolean.TRUE.equals(doesHaveBook)) {
            pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "You need at least one book to unenchant.");
        }
        if (Boolean.TRUE.equals(doesHaveBook) && !Boolean.TRUE.equals(doesContainEnchant)){
            pa.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Item does not have that enchantment.");
        }
        return true;
    }

    public static void removeEnchant(ItemStack stack, Player pa, NamespacedKey enchantment, int level) {
        stack.setAmount(stack.getAmount() - 1);
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        pa.getInventory().getItemInMainHand().removeEnchantment(Objects.requireNonNull(Enchantment.getByKey(enchantment)));
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        assert meta != null;
        meta.addStoredEnchant(Objects.requireNonNull(Enchantment.getByKey(enchantment)), level, true);
        book.setItemMeta(meta);
        pa.getWorld().dropItem(pa.getLocation(), new ItemStack(book));
    }

    public void requireMoneyCheck(ItemStack stack, Player pa, NamespacedKey enchantment, int level){
        Economy econ = UnenchanterRedux.getEconomy();
        EconomyResponse r = econ.withdrawPlayer(pa, configMoneyCost);
        Double balance = r.balance;
        df.format(balance);
        df.setRoundingMode(RoundingMode.UP);
        pa.sendMessage(ChatColor.GOLD + "$" + Math.round(r.amount) + ChatColor.WHITE + " was taken from your balance, you now have " +
                ChatColor.GOLD + "$" + df.format(balance) + ChatColor.GREEN + ".");
    }

    public void requireLevelCheck(ItemStack stack, Player pa, NamespacedKey enchantment, int level){
        int playerLevel = pa.getLevel();
        pa.setLevel(playerLevel - configExpCost);
        pa.sendMessage(ChatColor.GREEN + "Took " + ChatColor.GOLD + configExpCost + ChatColor.GREEN + " levels from you.");
    }

    public void doesHaveEnoughLevels(Player pa, int levels){
        int playerLevel = pa.getLevel();
        doesPlayerHaveEnoughLevels = playerLevel >= levels;
    }

    public void doesHaveEnoughMoney(Player pa, int cost){
        Economy econ = UnenchanterRedux.getEconomy();
        double playerBalance = econ.getBalance(pa);
        doesPlayerHaveEnoughMoney = playerBalance >= cost;
    }
}

