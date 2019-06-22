package lol.clann.minecraft.plugin.taobao.model.domain;

import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.adapter.context.SpringContext;
import lol.clann.minecraft.springboot.adapter.model.LazyField;
import lol.clann.minecraft.springboot.adapter.model.Lores;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

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

    public ItemStack toIcon() {
        ItemStack icon = item.clone();
        ItemStackUtils itemStackUtils = ShopItem.itemStackUtils.get();
        Lores lores = itemStackUtils.getLore(icon);
        if (lores == null) {
            lores = new Lores();
        }
        lores.add("§c§m §c§m §c§m §e§m §e§m §e§m §2§m §2§m §2§m §b§m §b§m §b§m §d§m §d§m §d§m §e§l商品信息§d§m §d§m §d§m §b§m §b§m §b§m §2§m §2§m §2§m §e§m §e§m §e§m §c§m §c§m §c§m §c§m");
        lores.add("§e单价:    §a" + getPrice() + "$");
        if (type == ShopTypeEnum.sale) {
            lores.add("§e库存数量:§a" + getCount());
            lores.add("§e累计交易量: §a" + getTotalDealCount());
            lores.add("§e累计交易额: §a" + getTotalDealCost());
            lores.add("§e购买1个: §a左键点击");
            lores.add("§e购买2个: §a按1  §e购买4个: §a按2");
            lores.add("§e购买8个: §a按3  §e购买16个:§a按4");
            lores.add("§e以此类推...");
            lores.add("§e尽量多的购买该商品:§aShift+左键点击");
        } else {
            lores.add("§e剩余空间:§a" + (getMaxCount() - getCount()));
            lores.add("§e累计交易量: §a" + getTotalDealCount());
            lores.add("§e累计交易额: §a" + getTotalDealCost());
            lores.add("§e出售1个: §a左键点击");
            lores.add("§e出售2个: §a按1  §e出售4个: §a按2");
            lores.add("§e出售8个: §a按3  §e出售16个:§a按4");
            lores.add("§e以此类推...");
            lores.add("§e尽量多的出售该商品:§aShift+左键点击");
        }
        itemStackUtils.setLore(icon, lores);
        return icon;
    }

    public ItemStack toOwnerIcon() {
        ItemStack icon = item.clone();
        ItemStackUtils itemStackUtils = ShopItem.itemStackUtils.get();
        Lores lores = itemStackUtils.getLore(icon);
        if (lores == null) {
            lores = new Lores();
        }
        lores.add("§c§m §c§m §c§m §e§m §e§m §e§m §2§m §2§m §2§m §b§m §b§m §b§m §d§m §d§m §d§m §e§l商品信息§d§m §d§m §d§m §b§m §b§m §b§m §2§m §2§m §2§m §e§m §e§m §e§m §c§m §c§m §c§m §c§m");
        lores.add("§e单价:    §a" + getPrice() + "$");
        if (type == ShopTypeEnum.sale) {
            lores.add("§e库存数量:§a" + getCount());
            lores.add("§e累计交易量: §a" + getTotalDealCount());
            lores.add("§e累计交易额: §a" + getTotalDealCost());
            lores.add("§e左键点击:§a从库存取出一组该商品");
            lores.add("§eShift+左键点击:§a从库存尽量多的取出该商品");
            lores.add("§e右建点击:§a添加一组该商品到库存");
            lores.add("§eShift+右建点击:§a将背包内所有该商品添加到库存");
        } else {
            lores.add("§e库存数量:§a" + getCount());
            lores.add("§e剩余空间:§a" + (getMaxCount() - getCount()));
            lores.add("§e累计交易量: §a" + getTotalDealCount());
            lores.add("§e累计交易额: §a" + getTotalDealCost());
            lores.add("§e左键点击:§a从库存取出一组该商品");
            lores.add("§eShift+左键点击:§a从库存尽量多的取出该商品");
        }
        lores.add("§e序号:§a" + getOrder());
        lores.add("§e摊位ID:§a" + getId());
        itemStackUtils.setLore(icon, lores);
        return icon;
    }
}