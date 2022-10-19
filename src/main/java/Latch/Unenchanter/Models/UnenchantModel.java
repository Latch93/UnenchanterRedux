package Latch.Unenchanter.Models;

public class UnenchantModel {

    protected String enchantment;
    protected int levelCost;
    protected int moneyCost;
    protected boolean doesCostLevel;
    protected boolean doesCostMoney;
    protected boolean isUnenchantAllowed;

    public UnenchantModel(String enchantment, int levelCost, int moneyCost, boolean doesCostLevel, boolean doesCostMoney, boolean isUnenchantAllowed) {
        this.enchantment = enchantment;
        this.levelCost = levelCost;
        this.moneyCost = moneyCost;
        this.doesCostLevel = doesCostLevel;
        this.doesCostMoney = doesCostMoney;
        this.isUnenchantAllowed = isUnenchantAllowed;
    }

    public String getEnchantment(){
        return enchantment;
    }

    public void setEnchantment(String enchantment) {
        this.enchantment = enchantment;
    }

    public int getLevelCost(){
        return levelCost;
    }

    public void setLevelCost(int levelCost) {
        this.levelCost = levelCost;
    }

    public int getMoneyCost(){
        return moneyCost;
    }

    public void setMoneyCost(int moneyCost) {
        this.moneyCost = moneyCost;
    }

    public boolean getDoesCostLevel(){
        return doesCostLevel;
    }

    public void setDoesCostLevel(boolean doesCostLevel) {
        this.doesCostLevel = doesCostLevel;
    }

    public boolean getDoesCostMoney(){
        return doesCostMoney;
    }

    public void setDoesCostMoney(boolean doesCostMoney) {
        this.doesCostMoney = doesCostMoney;
    }

    public boolean getIsUnenchantAllow(){
        return isUnenchantAllowed;
    }

    public void setIsUnenchantAllowed(boolean isUnenchantAllowed) {
        this.isUnenchantAllowed = isUnenchantAllowed;
    }

}
