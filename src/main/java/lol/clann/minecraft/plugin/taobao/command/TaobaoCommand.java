package lol.clann.minecraft.plugin.taobao.command;

import lol.clann.minecraft.plugin.taobao.Config;
import lol.clann.minecraft.plugin.taobao.TaobaoDealService;
import lol.clann.minecraft.plugin.taobao.TaobaoGuiManager;
import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.springboot.adapter.bukkit.command.annotation.*;
import lol.clann.minecraft.springboot.adapter.bukkit.command.constant.CommandConcurrentLevelEnum;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.adapter.context.PluginContext;
import lol.clann.minecraft.springboot.adapter.core.Plugin;
import lol.clann.minecraft.springboot.adapter.utils.ConfigUtils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 *
 * @author pyz
 * @date 2019/6/4 11:18 PM
 */
@CommandDefine(name = "taobao", description = "淘宝商城", order = -1)
public class TaobaoCommand {

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
            async = true)
    private void createShop(@Sender Player player, String name) {
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

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "setOrder <商品ID> <移动格数>",
            cooldown = 1000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "设置商品的序号,商品按序号升序排列,手续费:${taobao.cost.setOrderCost}",
            async = true)
    private void setOrder(@Sender Player player, long shopItemId, int order) {
        taobaoDealService.setOrder(player, shopItemId, order);
    }

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "newSale <价格>",
            cooldown = 4000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "创建新的出售商品,手续费:${taobao.cost.newSaleCost}",
            async = true)
    private void newSale(@Sender Player player, long price) {
        taobaoDealService.newSale(player, price);
    }

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "newBuy <价格> <店铺容量>",
            cooldown = 4000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "创建新的收购商品,手续费:${taobao.cost.newBuyCost}",
            async = true)
    private void newBuy(@Sender Player player, long price, int maxCount) {
        taobaoDealService.newBuy(player, price, maxCount);
    }

    @Command(onlyPlayer = true,
            onlyOp = false,
            showArgs = "deleteShopItem <商品ID> [--force]",
            cooldown = 4000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "下架指定商品,当指定了 --force 时,强制下架,忽略库存",
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
            async = true)
    private void setShopName(@Sender Player player, String name) {
        taobaoDealService.setShopName(player, name);
    }

    @Command(showArgs = "setIcon",
            onlyPlayer = true,
            onlyOp = false,
            cooldown = 4000,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "消耗一个手里物品,并将其设置为店铺图标,手续费:${taobao.cost.setIconCost}",
            async = true)
    private void setIcon(@Sender Player player) {
        taobaoDealService.setIcon(player);
    }

    @Command(showArgs = "setPrePageButton",
            onlyPlayer = true,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "设置上一页按钮为手里物品",
            async = true)
    private void setPrePageButton(@Sender Player player) throws IOException, ObjectMappingException, IllegalAccessException {
        setButton(player, config::setPrePageButton);
    }

    @Command(showArgs = "setNextPageButton",
            onlyPlayer = true,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "设置下一页按钮为手里物品",
            async = true)
    private void setNextPageButton(@Sender Player player) throws IOException, ObjectMappingException, IllegalAccessException {
        setButton(player, config::setNextPageButton);
    }

    @Command(showArgs = "setGoBackMenuButton",
            onlyPlayer = true,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "设置返回主菜单按钮为手里物品",
            async = true)
    private void setGoBackMenuButton(@Sender Player player) throws IOException, ObjectMappingException, IllegalAccessException {
        setButton(player, config::setGoBackMenuButton);
    }

    @Command(showArgs = "setRefreshButton",
            onlyPlayer = true,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "设置刷新按钮为手里物品",
            async = true)
    private void setRefreshButton(@Sender Player player) throws IOException, ObjectMappingException, IllegalAccessException {
        setButton(player, config::setRefreshButton);

    }

    private void setButton(Player player, Consumer<ItemStack> setter) throws IOException, IllegalAccessException, ObjectMappingException {
        ItemStack hand = player.getItemInHand();
        if (itemStackUtils.isEmpty(hand)) {
            player.sendMessage(ChatColor.RED + "请把物品拿在手上");
            return;
        }
        ItemStack button = hand.clone();
        button.setAmount(1);
        setter.accept(button);
        Plugin plugin = PluginContext.getPlugin("Taobao");
        ConfigUtils.saveConfig(plugin.getConfigurationNode(), config);
        plugin.directSaveConfig();
        player.sendMessage(ChatColor.GREEN + "变更按钮图标成功");
    }

    @Command(showArgs = "clear <owner> <type>", cooldown = 1000, concurrentLevel = CommandConcurrentLevelEnum.SENDER, des = "清空指定商店库存", async = true)
    private void clear(@Sender CommandSender sender, String owner, ShopTypeEnum type) {
        taobaoDealService.clear(sender, owner, type);
    }

    @Command(showArgs = "delete <owner>", cooldown = 4000, concurrentLevel = CommandConcurrentLevelEnum.SENDER, des = "删除指定商店,包括库存", async = true)
    private void delete(@Sender CommandSender sender, String owner) {
        taobaoDealService.delete(sender, owner);
    }

}
