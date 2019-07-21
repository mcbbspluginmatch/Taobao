package lol.clann.minecraft.plugin.taobao;

import lol.clann.minecraft.plugin.taobao.model.domain.DealRule;
import lol.clann.minecraft.springboot.api.annotation.config.Configure;
import lol.clann.minecraft.springboot.api.bukkit.model.ItemStackBuilder;
import lol.clann.minecraft.springboot.api.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.api.context.SpringContext;
import lol.clann.minecraft.springboot.api.model.LazyField;
import lol.com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 可能有人没见过@Getter和@Setter注解,百度搜lombok自行了解
 *
 * 使用@Configure注解来定义配置节点,一个字段就是一个节点
 *
 * @Configure(value = "taobao.icon.prePage", comment = "上一页按钮")
 *
 * 配置节点建议全部放在插件id下面(比如 淘宝的所有配置节点都是 taobao.xxx)
 *
 * 配置类中的@Configure字段,全部需要指定默认值,生成配置文件时,会使用这些默认值
 *
 * SBP会扫描插件中所有bean中被@Configure注解的字段,并将其写入配置文件中(意思是每个类都可以使用@Configure来声明配置节点,但建议将所有配置节点集中到特定的一个或多个类中)
 *
 * 在其他bean中,就可以使用@Autowired定义对这个bean的依赖,并使用这些配置节点
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
    @Configure(value = "taobao.taxReceiver", comment = "如果设置了这个,会把税转到指定玩家身上,通常设置为系统商店所有者")
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
    @Configure(value = "taobao.newSale.rule.dealRules", comment = "出售规则,可用来禁止物品出售或者限制最低售价")
    private List<DealRule> dealRules = new ArrayList<>();
    @Configure(value = "taobao.newSale.rule.rejectItemNames", comment = "名字黑名单,每条规则可以包含多个关键字,物品的名字同时包含某个规则的所有关键字,就禁止出售.")
    private List<List<String>> rejectItemNames = new ArrayList<>();
    @Configure(value = "taobao.newSale.rule.rejectItemLores", comment = "lore黑名单,每条规则可以包含多个关键字,物品的lore同时包含某个规则的所有关键字,就禁止出售.")
    private List<List<String>> rejectItemLores = new ArrayList<>();

    {
        rejectItemNames.add(Lists.newArrayList("§o", "点券"));
        rejectItemNames.add(Lists.newArrayList("节操"));
        rejectItemNames.add(Lists.newArrayList("国王"));
        rejectItemNames.add(Lists.newArrayList("服主"));
        rejectItemNames.add(Lists.newArrayList("淘宝"));
        rejectItemNames.add(Lists.newArrayList("腾讯"));
        rejectItemNames.add(Lists.newArrayList("京东"));
        rejectItemNames.add(Lists.newArrayList("爸爸"));

        rejectItemLores.add(Lists.newArrayList("§o", "点券"));
        rejectItemLores.add(Lists.newArrayList("节操"));
        rejectItemLores.add(Lists.newArrayList("国王"));
        rejectItemLores.add(Lists.newArrayList("服主"));
        rejectItemLores.add(Lists.newArrayList("淘宝"));
        rejectItemLores.add(Lists.newArrayList("腾讯"));
        rejectItemLores.add(Lists.newArrayList("京东"));
        rejectItemLores.add(Lists.newArrayList("爸爸"));
    }
}
