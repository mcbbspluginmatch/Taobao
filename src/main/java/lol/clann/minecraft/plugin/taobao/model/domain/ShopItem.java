package lol.clann.minecraft.plugin.taobao.model.domain;

import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.plugin.taobao.message.Messages;
import lol.clann.minecraft.springboot.api.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.api.context.SpringContext;
import lol.clann.minecraft.springboot.api.model.LazyField;
import lol.clann.minecraft.springboot.api.model.Lores;
import lol.clann.minecraft.springboot.api.model.message.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pyz
 * @date 2019/6/4 11:09 PM
 */
@Getter
@Setter
@ToString
public class ShopItem {
    private static final LazyField<ItemStackUtils> itemStackUtils = LazyField.of(() -> SpringContext.getBean(ItemStackUtils.class));
    private static final LazyField<Messages> messages = LazyField.of(() -> SpringContext.getBean(Messages.class));

    private long id;
    private long shopId;
    private ShopTypeEnum type;
    private long price;
    private int count;
    private int maxCount;
    private ItemStack item;
    private int totalDealCount;
    private long totalDealCost;
    private int order;
    //  以下字段不持久化
    private String owner;

    private Map<String, Object> getIconLoreContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("${price}", getPrice());
        context.put("${count}", getCount());
        context.put("${capability}", (getMaxCount() - getCount()));
        context.put("${totalDealCount}", getTotalDealCount());
        context.put("${totalDealCost}", getTotalDealCost());
        context.put("${shopItemId}", getId());
        context.put("${order}", getOrder());
        return context;
    }

    public ItemStack toIcon() {
        ItemStack icon = item.clone();
        ItemStackUtils itemStackUtils = ShopItem.itemStackUtils.get();
        List<String> lores = itemStackUtils.getLore(icon);
        if (lores == null) {
            lores = new ArrayList<>();
        }
        List<String> loreTexts;
        if (type == ShopTypeEnum.sale) {
            loreTexts = messages.get().getSaleShopItemCustomIconLores();
        } else {
            loreTexts = messages.get().getBuyShopItemCustomIconLores();
        }
        if (loreTexts == null || loreTexts.isEmpty()) {
            return icon;
        }
        Map<String, Object> context = getIconLoreContext();
        for (String loreText : loreTexts) {
            lores.add(MessageBuilder.from(loreText).resolve(context));
        }
        itemStackUtils.setLore(icon, lores);
        return icon;
    }

    public ItemStack toOwnerIcon() {
        ItemStack icon = item.clone();
        ItemStackUtils itemStackUtils = ShopItem.itemStackUtils.get();
        List<String> lores = itemStackUtils.getLore(icon);
        if (lores == null) {
            lores = new ArrayList<>();
        }
        List<String> loreTexts;
        if (type == ShopTypeEnum.sale) {
            loreTexts = messages.get().getSaleShopItemOwnerIconLores();
        } else {
            loreTexts = messages.get().getBuyShopItemOwnerIconLores();
        }
        if (loreTexts == null || loreTexts.isEmpty()) {
            return icon;
        }
        Map<String, Object> context = getIconLoreContext();
        for (String loreText : loreTexts) {
            lores.add(MessageBuilder.from(loreText).resolve(context));
        }
        itemStackUtils.setLore(icon, lores);
        return icon;
    }
}