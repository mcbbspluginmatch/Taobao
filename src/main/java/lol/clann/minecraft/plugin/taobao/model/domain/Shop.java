package lol.clann.minecraft.plugin.taobao.model.domain;

import com.ruoshui.utils.java.datetime.DateTimeUtils;
import lol.clann.minecraft.plugin.taobao.Config;
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

import java.util.*;

/**
 *
 * @author pyz
 * @date 2019/6/4 11:09 PM
 */
@Getter
@Setter
@ToString
public class Shop {
    private static final LazyField<ItemStackUtils> itemStackUtils = LazyField.of(() -> SpringContext.getBean(ItemStackUtils.class));
    private static final LazyField<Config> config = LazyField.of(() -> SpringContext.getBean(Config.class));
    private static final LazyField<Messages> messages = LazyField.of(() -> SpringContext.getBean(Messages.class));

    private long id;
    private boolean sys;
    private String owner;
    private String title;
    private int goodsSaleCount;
    private int goodsBuyCount;
    private int totalSaleCount;
    private long totalSaleCost;
    private int totalBuyCount;
    private long totalBuyCost;
    private long totalCost;
    private ItemStack icon;
    private Date updated = new Date();

    public ItemStack toIcon() {
        ItemStack icon = this.icon.clone();
        itemStackUtils.get().setDisplayName(icon, title);
        List<String> lores = itemStackUtils.get().getLore(icon);
        if (lores == null) {
            lores = new ArrayList<>();
        }
        List<String> loreTexts = messages.get().getShopIconLores();
        if (loreTexts == null || loreTexts.isEmpty()) {
            return icon;
        }
        Map<String, Object> context = new HashMap<>();
        context.put("${owner}", owner);
        context.put("${shopId}", id);
        context.put("${goodsBuyCount}", goodsBuyCount);
        context.put("${goodsSaleCount}", goodsSaleCount);
        context.put("${totalCost}", totalCost);
        context.put("${lastTradeTime}", DateTimeUtils.format(updated, "yyyy-MM-dd hh:mm:ss"));
        for (String loreText : loreTexts) {
            lores.add(MessageBuilder.from(loreText)
                    .resolve(context));
        }
        itemStackUtils.get().setLore(icon, lores);
        return icon;
    }
}