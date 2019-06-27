package lol.clann.minecraft.plugin.taobao.model.domain;

import com.ruoshui.utils.java.datetime.DateTimeUtils;
import lol.clann.minecraft.plugin.taobao.Config;
import lol.clann.minecraft.plugin.taobao.message.Messages;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.adapter.context.SpringContext;
import lol.clann.minecraft.springboot.adapter.model.LazyField;
import lol.clann.minecraft.springboot.adapter.model.Lores;
import lol.clann.minecraft.springboot.adapter.model.message.MessageBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
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
    private Date updated;

    public ItemStack toIcon() {
        ItemStack icon = this.icon.clone();
        itemStackUtils.get().setDisplayName(icon, title);
        Lores lores = itemStackUtils.get().getLore(icon);
        if (lores == null) {
            lores = new Lores();
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