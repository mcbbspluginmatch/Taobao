package lol.clann.minecraft.plugin.taobao;

import com.ruoshui.utils.java.math.MathUtils;
import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.plugin.taobao.mapper.TaobaoMapper;
import lol.clann.minecraft.plugin.taobao.model.domain.Shop;
import lol.clann.minecraft.plugin.taobao.model.domain.ShopItem;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.MenuUtils;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.ServerUtils;
import lol.clann.minecraft.springboot.adapter.model.menu.Menu;
import lol.clann.minecraft.springboot.adapter.model.menu.constant.MenuClickConcurrentLevelEnum;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author pyz
 * @date 2019/6/5 7:53 PM
 */
@Service
public class TaobaoGuiManager {
    @Autowired
    private Config config;
    @Autowired
    private TaobaoMapper taobaoMapper;
    @Autowired
    private MenuUtils menuUtils;
    @Autowired
    private ServerUtils serverUtils;
    @Autowired
    private TaobaoDealService taobaoDealService;
    @Autowired
    private TaobaoGuiManager taobaoGuiManager;

    public void openShopMenu(Player player, int page) {
        List<Shop> shops = taobaoMapper.fetchShopMenuContent(page * config.getContentSize() - config.getContentSize(), config.getContentSize());
        int shopCount = taobaoMapper.fetchShopCount();
        Menu menu = new Menu(config.getGuiSize(), config.getTitle());
        menu.setAsync(true);
        menu.setConcurrentLevel(MenuClickConcurrentLevelEnum.CLICKER);
        for (int i = 0; i < shops.size(); i++) {
            Shop shop = shops.get(i);
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
//        上一页
        if (page > 1) {
            menu.setButton(config.getContentSize(), config.getPrePageButton().clone(), (p, e) -> {
                taobaoGuiManager.openShopMenu(player, page - 1);
            });
        }
//        刷新
        menu.setButton(config.getContentSize() + 2, config.getRefreshButton().clone(), (p, e) -> {
            taobaoGuiManager.openShopMenu(player, page);
        });
//        下一页
        if (shopCount > page * config.getContentSize()) {
            menu.setButton(config.getGuiSize() - 1, config.getNextPageButton().clone(), (p, e) -> {
                taobaoGuiManager.openShopMenu(player, page + 1);
            });
        }
        menuUtils.open(player, menu);
    }

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
