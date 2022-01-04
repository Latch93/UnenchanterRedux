package Latch.UnenchanterRedux.ConfigManagers;

import Latch.UnenchanterRedux.Models.UnenchantModel;
import Latch.UnenchanterRedux.UnenchanterRedux;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
public class UnenchantConfigManager {
    private UnenchanterRedux plugin = UnenchanterRedux.getPlugin(UnenchanterRedux.class);
    public static FileConfiguration unenchantCfg;
    public File unenchantFile;

    // Set up unenchanterRedux.yml configuration file
    public void setup(){
        // if the UncrafterRedux folder does not exist, create the UncrafterRedux folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }

        unenchantFile = new File(plugin.getDataFolder(), "unenchanterRedux.yml");
        //if the unenchanterRedux.yml does not exist, create it
        if(!unenchantFile.exists()){
            try {
                unenchantFile.createNewFile();
            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the unenchanterRedux.yml file");
            }
        }
        unenchantCfg = YamlConfiguration.loadConfiguration(unenchantFile);
    }

    public void createUnenchantConfig(List<UnenchantModel> um){
        unenchantFile = new File(plugin.getDataFolder(), "unenchanterRedux.yml");
        //if the unenchanterRedux.yml does not exist, create it
        String existCheck = unenchantCfg.getString("enchants.aqua_affinity---1");
        if(existCheck == null) {
            try {
                int count = 1;
                um.sort(Comparator.comparing(UnenchantModel::getEnchantment));
                for (UnenchantModel enchantment : um) {
                    unenchantCfg.set("enchants." + enchantment.getEnchantment() + ".levelCost", enchantment.getLevelCost());
                    unenchantCfg.set("enchants." + enchantment.getEnchantment() + ".doesCostLevel", enchantment.getDoesCostLevel());
                    unenchantCfg.set("enchants." + enchantment.getEnchantment() + ".moneyCost", enchantment.getMoneyCost());
                    unenchantCfg.set("enchants." + enchantment.getEnchantment() + ".doesCostMoney", enchantment.getDoesCostMoney());
                    unenchantCfg.set("enchants." + enchantment.getEnchantment() + ".isUnenchantAllowed", enchantment.getIsUnenchantAllow());
                    count++;
                }
                unenchantCfg.save(unenchantFile);
            } catch (IOException e) {
                System.out.println(ChatColor.RED + "Could not create the unenchanterRedux.yml file");
            }
        }
    }
}

