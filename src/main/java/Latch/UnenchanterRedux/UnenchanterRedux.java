package Latch.UnenchanterRedux;

import Latch.UnenchanterRedux.ConfigManagers.UnenchantConfigManager;
import Latch.UnenchanterRedux.Controllers.UnenchantController;
import Latch.UnenchanterRedux.Models.UnenchantModel;
import Latch.UnenchanterRedux.TabComplete.UnenchantTabComplete;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class UnenchanterRedux extends JavaPlugin {

    public static List<UnenchantModel> um = new ArrayList<>();
    private UnenchantConfigManager UnenchantCfgm;
    private static Economy econ = null;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static UnenchanterRedux instance;
    private final File nameFile = new File(getDataFolder(),"enchantmentNames.yml");
    private YamlConfiguration nameYml;

    public YamlConfiguration getNameYml() {
        return nameYml;
    }

    {
        instance = this;
    }

    public static UnenchanterRedux getInstance() {
        return instance;
    }

    public void reload() {
        um = new ArrayList<>();
        createEnchantmentConfigModel();
        loadUnenchantConfigManager();
        UnenchantCfgm.createUnenchantConfig(um);
        nameYml = YamlConfiguration.loadConfiguration(nameFile);
    }


    @Override
    public void onEnable() {
        if(!nameFile.exists()) {
            saveResource("enchantmentNames.yml",false);
        }
        reload();
        setupEconomy();
        Objects.requireNonNull(this.getCommand("unenchant")).setExecutor(new UnenchantController());
        Objects.requireNonNull(this.getCommand("unenchant")).setTabCompleter(new UnenchantTabComplete());
        setupPlaceholders();
    }

    private void setupPlaceholders() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders().register();
        }
    }

    public String getEnchantmentName(NamespacedKey key) {
        String keyName = key.getNamespace()+":"+key.getKey();
        return nameYml.getString(keyName, keyName);
    }

    @Override
    public void onDisable(){
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    public static void createEnchantmentConfigModel(){
        for(Enchantment enchantment : Enchantment.values()) {
            int maxLevel = enchantment.getMaxLevel();
            for (int i = 0; i < maxLevel; i++){
                um.add(new UnenchantModel(enchantment.getKey().getKey() + "---" + (i + 1), 0, 0, false, false, true ));
            }
        }
    }

    public void loadUnenchantConfigManager(){
        UnenchantCfgm = new UnenchantConfigManager();
        UnenchantCfgm.setup();
    }

    public void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
        setEconomy(econ);
    }

    public void setEconomy(Economy value) {
        econ = value;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
