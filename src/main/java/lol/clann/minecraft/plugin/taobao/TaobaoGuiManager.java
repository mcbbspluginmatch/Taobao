package lol.clann.minecraft.plugin.taobao;

import com.ruoshui.utils.java.math.MathUtils;
import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.plugin.taobao.mapper.TaobaoMapper;
import lol.clann.minecraft.plugin.taobao.model.domain.Shop;
import lol.clann.minecraft.plugin.taobao.model.domain.ShopItem;
import lol.clann.minecraft.springboot.api.bukkit.utils.MenuUtils;
import lol.clann.minecraft.springboot.api.model.menu.Menu;
import lol.clann.minecraft.springboot.api.model.menu.constant.MenuClickConcurrentLevelEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用@Component或者@Service就可以定义一个bean
 * 每个bean有唯一的名字,一般是类名首字母小写
 * 某些特殊功能的bean名字就很容易冲突,此时就需要自己指定名字(比如Config类几乎每个插件都会有,肯定需要指定名字避免冲突)
 * 可以像这样指定bean的名字 @Component("Taobao-TaobaoGuiManager") / @Service("Taobao-TaobaoGuiManager")
 * 需要指定bean名字时一般是以插件ID加上类名作为类名作为bean名字
 *
 * 这样声明好一个bean后,在插件启动时,SBP会自动创建这个类的对象,并将依赖的bean注入进来
 *
 * @author pyz
 * @date 2019/6/5 7:53 PM
 */
@Service("Taobao-TaobaoGuiManager")
public class TaobaoGuiManager {
    /**
     * @Autowired 注解用来定义当前bean需要的依赖 (可以看到{@link Config}也是一个bean)
     * TaobaoGuiManager需要使用配置,所以这里定义了对Config的依赖
     * 这样声明后,在当前bean被加载好后,这个字段就已经有值了
     */
    @Autowired
    private Config config;
    /**
     * 可以定义任意多个依赖
     */
    @Autowired
    private TaobaoMapper taobaoMapper;
    /**
     * 创建gui需要使用这个bean
     */
    @Autowired
    private MenuUtils menuUtils;
    @Autowired
    private TaobaoDealService taobaoDealService;
    @Autowired
    private TaobaoGuiManager taobaoGuiManager;

    /**
     * 让指定玩家打开指定页的Taobao主菜单
     *
     * @param player 指定玩家
     * @param page 页码
     */
    public void openShopMenu(Player player, int page) {
        List<Shop> shops = taobaoMapper.fetchShopMenuContent(page * config.getContentSize() - config.getContentSize(), config.getContentSize());
        int shopCount = taobaoMapper.fetchShopCount();
//        创建一个Gui
        Menu menu = new Menu(config.getGuiSize(), config.getTitle());
//        设置点击时异步触发
        menu.setAsync(true);
//        设置并发等级为点记者(请参考指令并发等级)
        menu.setConcurrentLevel(MenuClickConcurrentLevelEnum.CLICKER);
//        设置店铺图标
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);
//            这里设置指定格子按钮
//            index:格子序号
//            itemStack: 格子图标
//            callback: 点击回调
//            public Menu setButton(int index, ItemStack itemStack, ButtonClickCallback callback)
            menu.setButton(i, shops.get(i).toIcon(), (p, e) -> {
                String owner = shop.getOwner();
                ShopTypeEnum type;
                int shopPage = 1;
                if (e.getClick().isLeftClick()) {
                    type = ShopTypeEnum.buy;
                } else {
                    type = ShopTypeEnum.sale;
                }
                taobaoGuiManager.openShop(player, owner, type, shopPage);
            });
        }
//        设置上一页按钮
        if (page > 1) {
            menu.setButton(config.getContentSize(), config.getPrePageButton().clone(), (p, e) -> {
                taobaoGuiManager.openShopMenu(player, page - 1);
            });
        }
//        设置刷新按钮
        menu.setButton(config.getContentSize() + 2, config.getRefreshButton().clone(), (p, e) -> {
            taobaoGuiManager.openShopMenu(player, page);
        });
//        设置下一页按钮
        if (shopCount > page * config.getContentSize()) {
            menu.setButton(config.getGuiSize() - 1, config.getNextPageButton().clone(), (p, e) -> {
                taobaoGuiManager.openShopMenu(player, page + 1);
            });
        }
//        指定玩家打开此Gui
        menuUtils.open(player, menu);
    }

    //    打开指定的店铺
    public void openShop(Player player, String owner, ShopTypeEnum type, int page) {
        List<ShopItem> shops = taobaoMapper.fetchShopContent(owner, type.name(), page * config.getContentSize() - config.getContentSize(), config.getContentSize());
        int shopCount = taobaoMapper.fetchShopItemCount(owner, type.name());
        StringBuilder sb = new StringBuilder("§2");
        if (type == ShopTypeEnum.sale) {
            sb.append("出售店铺 §6- ");
        } else {
            sb.append("收购店铺 §6- ");
        }
        sb.append("§c");
        sb.append(owner);
        boolean isOwner = player.getName().equals(owner);
        Menu menu = new Menu(config.getGuiSize(), sb.toString());
        menu.setAsync(true);
        menu.setConcurrentLevel(MenuClickConcurrentLevelEnum.CLICKER);
        for (int i = 0; i < shops.size(); i++) {
            ShopItem shopItem = shops.get(i);
            int maxStackCount = shopItem.getItem().getMaxStackSize();
            long shopItemId = shopItem.getId();
            if (isOwner) {
//                店主打开
                menu.setButton(i, shopItem.toOwnerIcon(), (p, e) -> {
                    String action;
                    if (type == ShopTypeEnum.sale) {
                        if (e.getClick().isLeftClick()) {
                            action = "pullStock";
                        } else {
                            action = "putStock";
                        }
                    } else {
                        action = "pullStock";
                    }
                    int count = e.getClick().isShiftClick() ? -1 : maxStackCount;
                    if ("pullStock".equals(action)) {
                        taobaoDealService.pullStock(player, e, shopItemId, count);
                    } else {
                        taobaoDealService.putStock(player, e, shopItemId, count);
                    }
                });
            } else {
//                消费者打开
                menu.setButton(i, shopItem.toIcon(), (p, e) -> {
                    String action;
                    if (type == ShopTypeEnum.sale) {
                        action = "buy";
                    } else {
                        action = "sale";
                    }
                    int count = calcCount(e);
                    if ("buy".equals(action)) {
                        taobaoDealService.buy(player, e, shopItemId, count);
                    } else {
                        taobaoDealService.sale(player, e, shopItemId, count);
                    }
                });
            }
        }

//        上一页
        if (page > 1) {
            menu.setButton(config.getContentSize(), config.getPrePageButton().clone(), (p, e) -> {
                taobaoGuiManager.openShop(player, owner, type, page - 1);
            });
        }
//        下一页
        if (shopCount > page * config.getContentSize()) {
            menu.setButton(config.getGuiSize() - 1, config.getNextPageButton().clone(), (p, e) -> {
                taobaoGuiManager.openShop(player, owner, type, page + 1);
            });
        }

        {
//            返回主菜单
            menu.setButton(config.getContentSize() + 1, config.getGoBackMenuButton().clone(), (p, e) -> {
                taobaoGuiManager.openShopMenu(player, 1);
            });
        }
        {
            //        刷新
            menu.setButton(config.getContentSize() + 2, config.getRefreshButton().clone(), (p, e) -> {
                taobaoGuiManager.openShop(player, owner, type, page);
            });
        }
        menuUtils.open(player, menu);
    }

    private int calcCount(InventoryClickEvent event) {
        if (event.getClick() == ClickType.NUMBER_KEY) {
            return (int) MathUtils.powIntegral(2, event.getHotbarButton() + 1);
        } else if (event.isShiftClick()) {
            return -1;
        } else {
            return 1;
        }
    }

}
