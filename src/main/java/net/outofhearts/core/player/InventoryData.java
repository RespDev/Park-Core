package net.outofhearts.core.player;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryData {

    public List<ItemStack> inventory;
    public List<ItemStack> armor;

    public InventoryData(List<ItemStack> inventory, List<ItemStack> armor) {
        this.inventory = inventory;
        this.armor = armor;
    }
}