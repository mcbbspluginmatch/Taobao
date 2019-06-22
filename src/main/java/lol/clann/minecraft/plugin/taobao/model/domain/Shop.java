package lol.clann.minecraft.plugin.taobao.model.domain;

import com.ruoshui.utils.java.datetime.DateTimeUtils;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.adapter.context.SpringContext;
import lol.clann.minecraft.springboot.adapter.model.LazyField;
import lol.clann.minecraft.springboot.adapter.model.Lores;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

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
        Lores lores = itemStackUtils.get().getLore(icon.clone());
        if (lores == null) {
            lores = new Lores();
        }
        lores.add("§a店主:§e" + owner);
        lores.add("§a店铺ID:§e" + id);
        lores.add("§a收购商品:§c" + goodsBuyCount + "§a种");
        lores.add("§a出售商品:§c" + goodsSaleCount + "§a种");
        lores.add("§a总营业额:§c" + totalCost + "$");
        lores.add("§a上次交易时间:§c" + DateTimeUtils.format(updated, "yyyy-MM-dd hh:mm:ss"));
        lores.add("§a左键进入§c收购区  §a右建进入§c出售区");
        itemStackUtils.get().setDisplayName(icon, title);
        itemStackUtils.get().setLore(icon, lores);
        return icon;
    }
}