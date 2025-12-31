package net.outofhearts.core.util.item;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import net.outofhearts.core.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
 * The ItemBuilder system can be used to easily make custom items
 */
public class ItemBuilder {

    private static final String UNABLE_TO_MOVE = "unableToMove";
    private static final String UNABLE_TO_DROP = "unableToDrop";
    private static final String ACTION_ID = "actionId";

    private static final Map<String, Consumer<PlayerInteractEvent>> ACTION_REGISTRY = new HashMap<>();

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private boolean unableToMove = false;
    private boolean unableToDrop = false;
    private Consumer<PlayerInteractEvent> action = null;

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    private void updateItemMeta() {
        this.itemStack.setItemMeta(this.itemMeta);
    }

    public ItemBuilder setDisplayName(String name) {
        this.itemMeta.setDisplayName(TextUtil.mm(name));
        return this;
    }

    public ItemBuilder setLore(String... lines) {
        this.itemMeta.setLore(List.of(TextUtil.mm(lines)));
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        this.itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setUnableToMove(boolean unableToMove) {
        this.unableToMove = unableToMove;
        return this;
    }

    public ItemBuilder setUnableToDrop(boolean unableToDrop) {
        this.unableToDrop = unableToDrop;
        return this;
    }

    public ItemBuilder addAction(Consumer<PlayerInteractEvent> action) {
        this.action = action;
        return this;
    }

    public ItemStack build() {
        this.updateItemMeta();
        ItemStack result = this.itemStack;

        if (unableToMove) {
            result = setNBTTag(result, UNABLE_TO_MOVE);
        }
        if (unableToDrop) {
            result = setNBTTag(result, UNABLE_TO_DROP);
        }
        if (action != null) {
            String actionId = UUID.randomUUID().toString();
            ACTION_REGISTRY.put(actionId, action);
            result = setNBTString(result, ACTION_ID, actionId);
        }

        return result;
    }

    private static ItemStack setNBTTag(ItemStack stack, String tag) {
        ItemStack craftStack = stack;
        if (!MinecraftReflection.isCraftItemStack(stack)) {
            craftStack = MinecraftReflection.getBukkitItemStack(stack);
        }
        NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(craftStack));
        nbt.put(tag, 1);
        NbtFactory.setItemTag(craftStack, nbt);
        return craftStack;
    }

    private static ItemStack setNBTString(ItemStack stack, String key, String value) {
        ItemStack craftStack = stack;
        if (!MinecraftReflection.isCraftItemStack(stack)) {
            craftStack = MinecraftReflection.getBukkitItemStack(stack);
        }
        NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(craftStack));
        nbt.put(key, value);
        NbtFactory.setItemTag(craftStack, nbt);
        return craftStack;
    }

    public static boolean hasNBTTag(ItemStack stack, String tag) {
        if (stack == null || stack.getType().equals(Material.AIR)) {
            return false;
        }

        ItemStack craftStack = stack;
        if (!MinecraftReflection.isCraftItemStack(stack)) {
            craftStack = MinecraftReflection.getBukkitItemStack(stack);
        }

        if (craftStack.getType().equals(Material.AIR)) {
            return false;
        }

        try {
            NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(craftStack));
            return nbt.containsKey(tag) && nbt.getInteger(tag) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getNBTString(ItemStack stack, String key) {
        if (stack == null || stack.getType().equals(Material.AIR)) {
            return null;
        }

        ItemStack craftStack = stack;
        if (!MinecraftReflection.isCraftItemStack(stack)) {
            craftStack = MinecraftReflection.getBukkitItemStack(stack);
        }

        if (craftStack.getType().equals(Material.AIR)) {
            return null;
        }

        try {
            NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(craftStack));
            return nbt.containsKey(key) ? nbt.getString(key) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isUnableToMove(ItemStack stack) {
        return hasNBTTag(stack, UNABLE_TO_MOVE);
    }

    public static boolean isUnableToDrop(ItemStack stack) {
        return hasNBTTag(stack, UNABLE_TO_DROP);
    }

    public static boolean executeAction(ItemStack stack, PlayerInteractEvent event) {
        String actionId = getNBTString(stack, ACTION_ID);
        if (actionId == null) {
            return false;
        }

        Consumer<PlayerInteractEvent> action = ACTION_REGISTRY.get(actionId);
        if (action != null) {
            action.accept(event);
            return true;
        }
        return false;
    }

    public static boolean hasAction(ItemStack stack) {
        String actionId = getNBTString(stack, ACTION_ID);
        return actionId != null && ACTION_REGISTRY.containsKey(actionId);
    }
}