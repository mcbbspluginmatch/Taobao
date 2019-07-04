package lol.clann.minecraft.plugin.taobao;

import lol.clann.minecraft.springboot.adapter.api.config.Configure;
import lol.clann.minecraft.springboot.adapter.bukkit.model.ItemStackBuilder;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.adapter.context.SpringContext;
import lol.clann.minecraft.springboot.adapter.model.LazyField;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.springframework.stereotype.Service;

/**
 *
 * @author pyz
 * @date 2019/6/2 11:37 PM
 */
@Getter
@Service("Taobao-Config")
public class Config {
    private final int guiSize = 6 * 9;
    private final int contentSize = guiSize - 9;
    private static final LazyField<ItemStackUtils> itemStackUtils = LazyField.of(() -> SpringContext.getBean(ItemStackUtils.class));

    @Setter
    @Configure(value = "taobao.icon.prePage", comment = "上一页按钮")
    private ItemStack prePageButton = ItemStackBuilder.builder(Material.DIAMOND).displayName("§2上一页").build();
    @Setter
    @Configure(value = "taobao.icon.nextPage", comment = "下一页按钮")
    private ItemStack nextPageButton = ItemStackBuilder.builder(Material.DIAMOND).displayName("§2下一页").build();
    @Setter
    @Configure(value = "taobao.icon.goBackMenu", comment = "返回主菜单按钮")
    private ItemStack goBackMenuButton = ItemStackBuilder.builder(Material.DIAMOND).displayName("§2返回主菜单").build();
    @Setter
    @Configure(value = "taobao.icon.refreshButton", comment = "刷新gui的按钮")
    private ItemStack refreshButton = ItemStackBuilder.builder(Material.DIAMOND).displayName("§2刷新").build();
    @Configure(value = "taobao.tax.buyShop", comment = "收购商店交易税率")
    private double buyShopTax = 0.1;
    @Configure(value = "taobao.tax.saleShop", comment = "出售商店交易税率")
    private double saleShopTax = 0.1;
    @Configure(value = "taobao.tax.putStock", comment = "增加出售商店库存时的库存税(主要目的是防止玩家把商店当成仓库)")
    private double putStockTax = 0.05;
    @Configure(value = "taobao.taxReceiver", comment = "如果设置了这个,会把税转到指定玩家身上")
    private String taxReceiver = "";
    /**
     * cost为玩家在系统商店的当日交易额
     * 计算结果为当日应收多少税
     */
    @Configure(value = "taobao.tax.sysBuyShopTaxScript", comment = "系统商店动态税率计算公式,主要用来防止玩家无节制的从系统商店揽财.(cost是当日系统收购商店交易额,计算结果为应收税额)")
    private String sysBuyShopTaxScript = "context.cost < 1000000 ? (context.cost * ((context.cost * 0.8 / 1000000 / 2)+0.1)) : ((100000 + 1000000 * 0.8 / 2) + (context.cost - 1000000) * 0.9)";
    @Configure(value = "taobao.menu.title", comment = "淘宝商城gui名字")
    private String title = ChatColor.GREEN + "淘宝商城" + ChatColor.WHITE;
    @Configure(value = "taobao.cost.createShopCost", comment = "创建淘宝商店的手续费")
    private long createShopCost = 100000;
    @Configure(value = "taobao.cost.newBuyCost", comment = "创建收购商品的手续费")
    private long newBuyCost = 10000;
    @Configure(value = "taobao.cost.newSaleCost", comment = "创建出售商品的手续费")
    private long newSaleCost = 10000;
    @Configure(value = "taobao.cost.setIconCost", comment = "变更店铺图标的手续费")
    private long setIconCost = 10000;
    @Configure(value = "taobao.cost.setShopNameCost", comment = "变更店铺名字的手续费")
    private long setShopNameCost = 10000;
    @Configure(value = "taobao.cost.setOrderCost", comment = "变更物品序号的手续费")
    private long setOrderCost = 10000;
}
