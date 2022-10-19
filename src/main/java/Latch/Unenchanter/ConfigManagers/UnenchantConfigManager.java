package Latch.Unenchanter.ConfigManagers;

import Latch.Unenchanter.Models.UnenchantModel;
import Latch.Unenchanter.Unenchanter;
import Latch.Unenchanter.Constants;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static Latch.Unenchanter.Unenchanter.log;

public class UnenchantConfigManager {
    private final Unenchanter plugin = Unenchanter.getPlugin(Unenchanter.class);
    private static FileConfiguration unenchantCfg;
    private File unenchantFile;

    // Set up config.yml configuration file
    public void setup(){
        // if the Latch's Unenchant folder does not exist, create the Latch's Unenchant folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }

        unenchantFile = new File(plugin.getDataFolder(), Constants.CONFIG_FILE_NAME + ".yml");
        //if the config.yml does not exist, create it
        if(!unenchantFile.exists()){
            try {
                unenchantFile.createNewFile();
            }
            catch(IOException e){
                log.info(ChatColor.RED + "Could not create the " + Constants.CONFIG_FILE_NAME + ".yml file");
            }
        }
        unenchantCfg = YamlConfiguration.loadConfiguration(unenchantFile);
    }

    public void createUnenchantConfig(List<UnenchantModel> um){
        unenchantFile = new File(plugin.getDataFolder(), Constants.CONFIG_FILE_NAME + ".yml");
        //if the config.yml does not exist, create it
        try {
            Unenchanter.getUM().sort(Comparator.comparing(UnenchantModel::getEnchantment));
            for (UnenchantModel enchantment : um) {
                if (!unenchantCfg.isSet(Constants.YML_ENCHANTS_KEY + enchantment.getEnchantment() + ".levelCost")) {
                    unenchantCfg.set(Constants.YML_ENCHANTS_KEY + enchantment.getEnchantment() + ".levelCost", enchantment.getLevelCost());
                    unenchantCfg.set(Constants.YML_ENCHANTS_KEY + enchantment.getEnchantment() + ".doesCostLevel", enchantment.getDoesCostLevel());
                    unenchantCfg.set(Constants.YML_ENCHANTS_KEY + enchantment.getEnchantment() + ".moneyCost", enchantment.getMoneyCost());
                    unenchantCfg.set(Constants.YML_ENCHANTS_KEY + enchantment.getEnchantment() + ".doesCostMoney", enchantment.getDoesCostMoney());
                    unenchantCfg.set(Constants.YML_ENCHANTS_KEY + enchantment.getEnchantment() + ".isUnenchantAllowed", enchantment.getIsUnenchantAllow());
                }
            }
            unenchantCfg.save(unenchantFile);
        } catch (IOException e) {
            log.info(ChatColor.RED + "Could not create the " + Constants.CONFIG_FILE_NAME + ".yml file");
        }
    }
}

