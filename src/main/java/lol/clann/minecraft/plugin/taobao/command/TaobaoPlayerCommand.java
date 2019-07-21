package lol.clann.minecraft.plugin.taobao.command;

import lol.clann.minecraft.plugin.taobao.TaobaoDealService;
import lol.clann.minecraft.plugin.taobao.TaobaoGuiManager;
import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.springboot.api.annotation.command.*;
import lol.clann.minecraft.springboot.api.bukkit.command.constant.CommandConcurrentLevelEnum;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * SBP的指令格式(这个格式只是规定showArgs该怎么写,方便玩家看到统一的效果,对指令解析没有任何用处)
 * <> 表示一个参数
 * [] 表示可选参数
 * | 表示参数的可选值
 *
 * 如 /taoboa open <player> <buy|sale> [<page>]
 *
 * 使用注解@CommandDefine来定义一个指令(无需写到plugin.yml里,话说SBP插件就没有plugin.yml)
 * 注: @CommandDefine兼具@Service和@Component的功能,所以TaobaoPlayerCommand也是一个bean
 *
 * 这里定义了/taobao指令
 *
 * 下面对子指令createShop进行讲解
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

    /**
     * 创建商店
     *
     * @Command注解在方法上定义一个子指令
     * 此处定义的子指令是 /taobao createShop
     *
     * 一个具体的指令由指令前缀,和参数部分组成
     *
     * 指令前的组成 @CommandDefine.name + @CommandDefine.mapping + @Command.mapping
     * 例如一个子指令定义为 @CommandDefine(name="taobao",mapping="xx yy")  @Command(mapping="zz") 方法名为open
     * 则这个子指令的指令前缀是 /taobao xx yy zz open
     * 指令参数紧跟在指令前缀后面
     * 指令参数就是方法的参数
     *
     * @Sender注解的参数是指令的执行者,不占用参数位置
     * @Optional注解的参数是可选的
     *
     * @param player
     */
    @Command(
            onlyPlayer = true, // 这个指令只允许玩家指令
            onlyOp = false,  // 非op可以执行
            showArgs = "createShop <name>", // showArgs只是展示给玩家看的,告诉玩家指令格式
            cooldown = 1000,  // 指令的冷却时间(毫秒),不写就没冷却, 没错 冷却就这么简单
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,   // 指令的并发等级 并发等级分3级 (有些指令比较耗时,同一个玩家只允许有一个指令正在执行,或者全服只允许有一个指令正在执行)
            // 默认CommandConcurrentLevelEnum.NONE 无限制
            // CommandConcurrentLevelEnum.SENDER 对于同一个玩家,此指令只允许有一条正在执行
            // CommandConcurrentLevelEnum.ALL 对于全服,此指令只允许有一条正在执行
            des = "创建商店,手续费:${taobao.cost.createShopCost}",  // 指令描述,会展示在帮助信息中, 可以使用占位符 ${xxx} 来引用配置配置
            resolveColorCode = true,  // 是否解析玩家输入中的颜色字符 (就是自动将&转换成§)
            permission = "taobao.command.createShop",  // 执行此指令所需的权限
            async = true // 是否异步执行
    )
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
