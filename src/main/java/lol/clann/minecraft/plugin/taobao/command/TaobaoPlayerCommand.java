package lol.clann.minecraft.plugin.taobao.command;

import lol.clann.minecraft.plugin.taobao.Config;
import lol.clann.minecraft.plugin.taobao.TaobaoDealService;
import lol.clann.minecraft.plugin.taobao.TaobaoGuiManager;
import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.springboot.api.annotation.command.*;
import lol.clann.minecraft.springboot.api.bukkit.command.constant.CommandConcurrentLevelEnum;
import lol.clann.minecraft.springboot.api.bukkit.utils.ItemStackUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 *
 * @author pyz
 * @date 2019/7/14 3:24 PM
 */
@CommandDefine(name = "taobao", description = "淘宝商城", order = -1)
public class TaobaoPlayerCommand {
    @Autowired
    private TaobaoGuiManager taobaoGuiManager;
    @Autowired
    private TaobaoDealService taobaoDealService;
    @Autowired
    private Config config;
    @Autowired
    private ItemStackUtils itemStackUtils;

    /**
     * 创建商店
     *
     * @param player
     */
    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "createShop <name>",
            cooldown = 1000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "创建商店,手续费:${taobao.cost.createShopCost}",
            resolveColorCode = true,
            permission = "taobao.command.createShop",
            async = true)
    private void createShop(@Sender Player player, String name) throws ExecutionException, InterruptedException {
        taobaoDealService.createShop(player, name);
    }

    /**
     * 打开淘宝商城
     *
     * @param player
     * @param page
     */
    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "open [<page>]",
            cooldown = 1000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "打开商城",
            async = true)
    private void open(@Sender Player player, @Optional Integer page) throws ExecutionException, InterruptedException {
        taobaoGuiManager.openShopMenu(player, Math.max(page == null ? 1 : page, 1));
    }

    /**
     * 打开淘宝商城
     *
     * @param player
     */
    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "openShop <owner> <buy|sale> [<page>]",
            cooldown = 1000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "打开指定店铺",
            async = true)
    private void openShop(@Sender Player player, OfflinePlayer owner, ShopTypeEnum type, @Optional Integer page) throws ExecutionException, InterruptedException {
        if (page == null) {
            page = 1;
        } else if (page <= 0) {
            page = 1;
        }
        taobaoGuiManager.openShop(player, owner.getName(), type, page);
    }

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "setOrder <商品ID> <移动格数>",
            cooldown = 1000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "设置商品的序号,商品按序号升序排列,手续费:${taobao.cost.setOrderCost}",
            permission = "taobao.command.setOrder",
            async = true)
    private void setOrder(@Sender Player player, long shopItemId, int order) throws ExecutionException, InterruptedException {
        taobaoDealService.setOrder(player, shopItemId, order);
    }

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "newSale <价格>",
            cooldown = 4000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "创建新的出售商品,手续费:${taobao.cost.newSaleCost}",
            permission = "taobao.command.newSale",
            async = true)
    private void newSale(@Sender Player player, long price) throws ExecutionException, InterruptedException {
        taobaoDealService.newSale(player, price);
    }

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "newBuy <价格> <店铺容量>",
            cooldown = 4000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "创建新的收购商品,手续费:${taobao.cost.newBuyCost}",
            permission = "taobao.command.newBuy",
            async = true)
    private void newBuy(@Sender Player player, long price, int maxCount) throws ExecutionException, InterruptedException {
        taobaoDealService.newBuy(player, price, maxCount);
    }

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "deleteShopItem <商品ID> [--force]",
            cooldown = 4000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "下架指定商品,当指定了 --force 时,强制下架,忽略库存",
            permission = "taobao.command.deleteShopItem",
            async = true)
    private void deleteShopItem(@Sender Player player, long shopItemId, @Flag("force") boolean force) {
        taobaoDealService.deleteShopItem(player, shopItemId, force);
    }

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "setShopName <名字>",
            cooldown = 1000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "设置店铺名字,手续费:${taobao.cost.setShopNameCost}",
            resolveColorCode = true,
            permission = "taobao.command.setShopName",
            async = true)
    private void setShopName(@Sender Player player, String name) throws ExecutionException, InterruptedException {
        taobaoDealService.setShopName(player, name);
    }

    @Command(showArgs = "setIcon",
            onlyPlayer = true,
            onlyOp = false,
            cooldown = 4000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "消耗一个手里物品,并将其设置为店铺图标,手续费:${taobao.cost.setIconCost}",
            permission = "taobao.command.setIcon",
            async = true)
    private void setIcon(@Sender Player player) throws ExecutionException, InterruptedException {
        taobaoDealService.setIcon(player);
    }

}
