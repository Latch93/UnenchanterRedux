package Latch.Unenchanter.Controllers;

import Latch.Unenchanter.Constants;
import Latch.Unenchanter.Unenchanter;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static Latch.Unenchanter.Unenchanter.log;

public class UnenchantController implements CommandExecutor {

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
    private final Unenchanter plugin = Unenchanter.getPlugin(Unenchanter.class);

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(args.length == 1 && args[0].equalsIgnoreCase("reload") && commandSender.hasPermission("ur.command.reload")){
            Unenchanter.getInstance().reload();
            commandSender.sendMessage(ChatColor.GREEN + Constants.PLUGIN_NAME + " has been reloaded.");
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("debug") && commandSender.isOp()) {
            Arrays.stream(Enchantment.values()).forEach(ench -> {
                log.info(ChatColor.YELLOW + "Enchantment: " + ench.getKey());
            });
            return true;
        }

        Player pa = (Player) commandSender;
        int level;
        int count = 0;
        File configFile = new File(plugin.getDataFolder(), Constants.CONFIG_FILE_NAME + ".yml");
        FileConfiguration unenchantCfg = YamlConfiguration.loadConfiguration(configFile);
        if (args.length == 0){
            pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "You need to select the enchantment on an item to unenchant.");
        }
        else if (args.length == 1) {
            for (ItemStack stack : pa.getInventory().getContents()) {
                if (count == 0){
                    try {
                        if(stack.getType().equals(Material.BOOK)){
                            count = 1;
                            doesHaveBook = true;
                            for (Map.Entry<Enchantment, Integer> entry : pa.getInventory().getItemInMainHand().getEnchantments().entrySet()) {
                                String[] temp1 = entry.toString().split(":");
                                String[] temp2 = temp1[1].split(",");
                                String enc = temp2[0];
                                level = entry.getValue();
                                String selection = args[0];
                                String[] selectionArr = selection.split(":");
                                if (selectionArr[0].equalsIgnoreCase(enc)){
                                    doesContainEnchant = true;
                                    for(String enchantFromConfig : unenchantCfg.getConfigurationSection("enchants").getKeys(false)) {
                                        String[] placeholder = enchantFromConfig.split("---");
                                        String configEnchantName = placeholder[0];
                                        int configEnchantLevel = Integer.parseInt(placeholder[1]);
                                        if (configEnchantName.equalsIgnoreCase(enc) && configEnchantLevel == level){
                                            configExpCost = unenchantCfg.getInt(Constants.YML_ENCHANTS_KEY + enchantFromConfig + ".levelCost");
                                            configMoneyCost = unenchantCfg.getInt(Constants.YML_ENCHANTS_KEY + enchantFromConfig + ".moneyCost");
                                            configDoesCostExp = unenchantCfg.getBoolean(Constants.YML_ENCHANTS_KEY + enchantFromConfig + ".doesCostLevel");
                                            configDoesCostMoney = unenchantCfg.getBoolean(Constants.YML_ENCHANTS_KEY + enchantFromConfig + ".doesCostMoney");
                                            isUnenchantAllowed = unenchantCfg.getBoolean(Constants.YML_ENCHANTS_KEY + enchantFromConfig + ".isUnenchantAllowed");
                                        }
                                    }
                                    doesHaveEnoughMoney(pa, configMoneyCost);
                                    doesHaveEnoughLevels(pa, configExpCost);
                                    if (Boolean.TRUE.equals(isUnenchantAllowed)){
                                        if (!Boolean.TRUE.equals(configDoesCostExp) && !Boolean.TRUE.equals(configDoesCostMoney)) {
                                            removeEnchant(stack, pa, entry.getKey(), level);
                                            pa.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.GOLD + enc + " " + level + ChatColor.GREEN + ".");
                                        }
                                        if (Boolean.TRUE.equals(configDoesCostExp) && Boolean.FALSE.equals(configDoesCostMoney)) {
                                            if (Boolean.TRUE.equals(doesPlayerHaveEnoughLevels)) {
                                                requireLevelCheck(pa);
                                                removeEnchant(stack, pa, entry.getKey(), level);
                                            }
                                            else {
                                                pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "You do not have enough levels to unenchant. Levels required: " + ChatColor.GOLD + configExpCost);
                                            }
                                        }
                                        if (Boolean.TRUE.equals(configDoesCostMoney) && Boolean.FALSE.equals(configDoesCostExp)){
                                            if (Boolean.TRUE.equals(doesPlayerHaveEnoughMoney)){
                                                requireMoneyCheck(pa);
                                                removeEnchant(stack, pa, entry.getKey(), level);
                                            }
                                            else {
                                                pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "You do not have enough money to unenchant. Cost required: " + ChatColor.GOLD + "$" + configMoneyCost);
                                            }
                                        }
                                        if (Boolean.TRUE.equals(configDoesCostMoney) && Boolean.TRUE.equals(configDoesCostExp)){
                                            if (Boolean.FALSE.equals(doesPlayerHaveEnoughMoney)){
                                                pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "You do not have enough money to unenchant. Cost required: " + ChatColor.GOLD + "$" + configMoneyCost);
                                            }
                                            if (Boolean.FALSE.equals(doesPlayerHaveEnoughLevels)){
                                                pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "You have enough levels to unenchant this item. Levels required: " + ChatColor.GOLD + configExpCost);
                                            }
                                            if (Boolean.TRUE.equals(doesPlayerHaveEnoughMoney) && Boolean.TRUE.equals(doesPlayerHaveEnoughLevels)) {
                                                requireMoneyCheck(pa);
                                                requireLevelCheck(pa);
                                                removeEnchant(stack, pa, entry.getKey(), level);
                                            }
                                        }
                                    }
                                    else {
                                        pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "This enchantment is not allowed to be unenchanted.");
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        log.info(ChatColor.RED +e.getMessage());
                    }
                }
            }
        }
        else {
            pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "Too many arguments entered.");
            pa.sendMessage(ChatColor.GRAY + "Enter command like this -> /unenchant [enchantment]");
        }
        if (!Boolean.TRUE.equals(doesHaveBook)) {
            pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "You need at least one book to unenchant.");
        }
        if (Boolean.TRUE.equals(doesHaveBook) && !Boolean.TRUE.equals(doesContainEnchant)){
            pa.sendMessage(ChatColor.RED + Constants.YML_ENCHANT_ERROR_MESSAGE_STRING + ChatColor.GRAY + "Item does not have that enchantment.");
        }
        return true;
    }

    public static void removeEnchant(ItemStack stack, Player pa, Enchantment enchantment, int level) {
        stack.setAmount(stack.getAmount() - 1);
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        pa.getInventory().getItemInMainHand().removeEnchantment(enchantment);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        assert meta != null;
        meta.addStoredEnchant(Objects.requireNonNull(enchantment), level, true);
        book.setItemMeta(meta);
        pa.getInventory().addItem(book);
        //pa.getWorld().dropItem(pa.getLocation(), new ItemStack(book));
    }

    public void requireMoneyCheck(Player pa){
        Economy econ = Unenchanter.getEconomy();
        EconomyResponse r = econ.withdrawPlayer(pa, configMoneyCost);
        Double balance = r.balance;
        df.format(balance);
        df.setRoundingMode(RoundingMode.UP);
        pa.sendMessage(ChatColor.GOLD + "$" + Math.round(r.amount) + ChatColor.WHITE + " was taken from your balance, you now have " +
                ChatColor.GOLD + "$" + df.format(balance) + ChatColor.GREEN + ".");
    }

    public void requireLevelCheck(Player pa){
        int playerLevel = pa.getLevel();
        pa.setLevel(playerLevel - configExpCost);
        pa.sendMessage(ChatColor.GREEN + "Took " + ChatColor.GOLD + configExpCost + ChatColor.GREEN + " levels from you.");
    }

    public void doesHaveEnoughLevels(Player pa, int levels){
        int playerLevel = pa.getLevel();
        doesPlayerHaveEnoughLevels = playerLevel >= levels;
    }

    public void doesHaveEnoughMoney(Player pa, int cost){
        Economy econ = Unenchanter.getEconomy();
        double playerBalance = econ.getBalance(pa);
        doesPlayerHaveEnoughMoney = playerBalance >= cost;
    }
}

