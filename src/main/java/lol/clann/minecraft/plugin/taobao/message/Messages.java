package lol.clann.minecraft.plugin.taobao.message;

import com.google.common.collect.Lists;
import lol.clann.minecraft.springboot.adapter.api.config.Configure;
import lol.clann.minecraft.springboot.adapter.api.config.TranslateColoreCode;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author pyz
 * @date 2019/6/23 12:14 PM
 */
@Getter
@Service("Taobao-Messages")
public class Messages {
    @TranslateColoreCode
    @Configure(value = "taobao.msg.newSale.broadcast", comment = "上架新商品的广播消息")
    private Message newSaleBroadcast = new Message(true, "玩家${player}上架了出售商品:${name}");
    @TranslateColoreCode
    @Configure(value = "taobao.msg.newSale.msg", comment = "上架新商品的提示信息")
    private Message newSale = new Message(true, "恭喜您,上架出售商品成功,商品ID:${id},手续费:${cost}");
    @TranslateColoreCode
    @Configure(value = "taobao.msg.notEnoughMony", comment = "余额不足的提示信息")
    private Message notEnoughMony = new Message(true, "余额不足");
    @TranslateColoreCode
    @Configure(value = "taobao.msg.requireMony", comment = "余额不足的提示信息,会提示需要多少钱")
    private Message requireMony = new Message(true, "余额不足,需要:${money}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.newBuy.broadcast", comment = "上架新商品的广播消息")
    private Message newBuyBroadcast = new Message(true, "玩家${player}上架了收购商品:${name}");
    @TranslateColoreCode
    @Configure(value = "taobao.msg.newBuy.msg", comment = "上架新商品的提示信息")
    private Message newBuy = new Message(true, "恭喜您,上架收购商品成功,商品ID:${id},手续费:${cost}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.deleteShopItem.msg", comment = "下架商品的提示信息")
    private Message deleteShopItem = new Message(true, "您下架了${type}商品${name},商品ID:${id}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.deleteShopItem.broadcast", comment = "下架商品的广播消息")
    private Message deleteShopItemBroadcast = new Message(true, "玩家${player}下架了${type}商品${name}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.renameShop.msg", comment = "店铺更名的消息通知")
    private Message renameShop = new Message(true, "您的店铺名字更改为:${name}");
    @TranslateColoreCode
    @Configure(value = "taobao.msg.renameShop.broadcast", comment = "店铺更名的广播通知")
    private Message renameShopBroadcast = new Message(true, "玩家${player}的店铺名字更改为:${name}");


    @TranslateColoreCode
    @Configure(value = "taobao.msg.setShopIcon.msg", comment = "更换店铺图标的消息通知")
    private Message setShopIcon = new Message(true, "您的店铺图标更换为:${name}");
    @TranslateColoreCode
    @Configure(value = "taobao.msg.setShopIcon.broadcast", comment = "更换店铺图标的广播通知")
    private Message setShopIconBroadcast = new Message(true, "玩家${player}的店铺图标更换为:${name}");


    @TranslateColoreCode
    @Configure(value = "taobao.msg.createShop.msg", comment = "创建店铺的消息通知")
    private Message createShop = new Message(true, "您创建了新的店铺:${name},店铺ID:${id}");
    @TranslateColoreCode
    @Configure(value = "taobao.msg.createShop.broadcast", comment = "创建店铺的广播通知")
    private Message createShopBroadcast = new Message(true, "玩家${player}的创建新的店铺:${name}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.setOrder.msg", comment = "更改摊位序号的消息通知")
    private Message setOrder = new Message(true, "商品 ${id} 序号变更为 ${order}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.putStock.msg", comment = "添加库存的消息通知")
    private Message putStock = new Message(true, "您添加了${count}个${name}到${type}商店,手续费:${money}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.pullStock.msg", comment = "取出库存的消息通知")
    private Message pullStock = new Message(true, "您从${type}商店取出了${count}个${name}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.sale.msg", comment = "出售成功的消息通知")
    private Message sale = new Message(true, "您出售了${count}个${name}到${owner}的商店,交易额:${cost},收入:${income},缴税:${tax},日交易额:${todayTotalCost}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.sale.notifyOwner", comment = "出售成功,通知店主的消息通知")
    private Message saleNotifyOwner = new Message(true, "玩家${player}出售了${count}个${name}到您的商店,花费:${cost}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.buy.msg", comment = "购买成功的消息通知")
    private Message buy = new Message(true, "您从${owner}的商店购买了${count}个${name},花费:${cost}");

    @TranslateColoreCode
    @Configure(value = "taobao.msg.buy.notifyOwner", comment = "购买成功,通知店主的消息通知")
    private Message buyNotifyOwner = new Message(true, "${player}从您的商店购买了${count}个${name},交易额:${cost},缴税:${tax},收入:${income}");


    @TranslateColoreCode
    @Configure(value = "taobao.msg.shopIconLores", comment = "商店图标的lore,可用变量:${owner} ${shopId} ${goodsBuyCount} ${goodsSaleCount} ${totalCost} ${lastTradeTime}")
    private List<String> shopIconLores = Lists.newArrayList(
            "§a店主:§e${owner}",
            "§a店铺ID:§e${shopId}",
            "§a收购商品:§c${goodsBuyCount}§a种",
            "§a出售商品:§c${goodsSaleCount}§a种",
            "§a总营业额:§c${totalCost}$",
            "§a上次交易时间:§c${lastTradeTime}",
            "§a左键进入§c收购区  §a右建进入§c出售区"
    );


    @TranslateColoreCode
    @Configure(value = "taobao.msg.saleShopItemOwnerIconLores", comment = "出售商品,店主视角的图标文案,可用变量:${shopItemId} ${order} ${price} ${count} ${totalDealCount} ${totalDealCost}")
    private List<String> saleShopItemOwnerIconLores = Lists.newArrayList(
            "§c§m §c§m §c§m §e§m §e§m §e§m §2§m §2§m §2§m §b§m §b§m §b§m §d§m §d§m §d§m §e§l商品信息§d§m §d§m §d§m §b§m §b§m §b§m §2§m §2§m §2§m §e§m §e§m §e§m §c§m §c§m §c§m §c§m",
            "§e单价:    §a${price}$",
            "§e库存数量:§a${count}",
            "§e累计交易量: §a${totalDealCount}",
            "§e累计交易额: §a${totalDealCost}",
            "§e左键点击:§a从库存取出一组该商品",
            "§eShift+左键点击:§a从库存尽量多的取出该商品",
            "§e右建点击:§a添加一组该商品到库存",
            "§eShift+右建点击:§a将背包内所有该商添加到库存",
            "§e序号:§a${order}",
            "§e摊位ID:§a${shopItemId}"
    );

    @TranslateColoreCode
    @Configure(value = "taobao.msg.saleShopItemCustomIconLores", comment = "出售商品,顾客视角的图标文案,可用变量:${price} ${count} ${totalDealCount} ${totalDealCost}")
    private List<String> saleShopItemCustomIconLores = Lists.newArrayList(
            "§c§m §c§m §c§m §e§m §e§m §e§m §2§m §2§m §2§m §b§m §b§m §b§m §d§m §d§m §d§m §e§l商品信息§d§m §d§m §d§m §b§m §b§m §b§m §2§m §2§m §2§m §e§m §e§m §e§m §c§m §c§m §c§m §c§m",
            "§e单价:    §a${price}$",
            "§e库存数量:§a${count}",
            "§e累计交易量: §a${totalDealCount}",
            "§e累计交易额: §a${totalDealCost}",
            "§e购买1个: §a左键点击",
            "§e购买2个: §a按1  §e购买4个: §a按2",
            "§e购买8个: §a按3  §e购买16个:§a按4",
            "§e以此类推...",
            "§e尽量多的购买该商品:§aShift+左键点击"
    );

    @TranslateColoreCode
    @Configure(value = "taobao.msg.buyShopItemOwnerIconLores", comment = "收购商品,店主视角的图标文案,可用变量:${shopItemId} ${order} ${price} ${count} ${capability} ${totalDealCount} ${totalDealCost}")
    private List<String> buyShopItemOwnerIconLores = Lists.newArrayList(
            "§c§m §c§m §c§m §e§m §e§m §e§m §2§m §2§m §2§m §b§m §b§m §b§m §d§m §d§m §d§m §e§l商品信息§d§m §d§m §d§m §b§m §b§m §b§m §2§m §2§m §2§m §e§m §e§m §e§m §c§m §c§m §c§m §c§m",
            "§e单价:    §a${price}$",
            "§e库存数量:§a${count}",
            "§e剩余空间:§a${capability}",
            "§e累计交易量: §a${totalDealCount}",
            "§e累计交易额: §a${totalDealCost}",
            "§e左键点击:§a从库存取出一组该商品",
            "§eShift+左键点击:§a从库存尽量多的取出该商品",
            "§e序号:§a${order}",
            "§e摊位ID:§a${shopItemId}"
    );

    @TranslateColoreCode
    @Configure(value = "taobao.msg.buyShopItemCustomIconLores", comment = "收购商品,顾客视角的图标文案,可用变量:${price} ${capability} ${totalDealCount} ${totalDealCost}")
    private List<String> buyShopItemCustomIconLores = Lists.newArrayList(
            "§c§m §c§m §c§m §e§m §e§m §e§m §2§m §2§m §2§m §b§m §b§m §b§m §d§m §d§m §d§m §e§l商品信息§d§m §d§m §d§m §b§m §b§m §b§m §2§m §2§m §2§m §e§m §e§m §e§m §c§m §c§m §c§m §c§m",
            "§e单价:    §a${price}$",
            "§e剩余空间:§a${capability}",
            "§e累计交易量: §a${totalDealCount}",
            "§e累计交易额: §a${totalDealCost}",
            "§e出售1个: §a左键点击",
            "§e出售2个: §a按1  §e出售4个: §a按2",
            "§e出售8个: §a按3  §e出售16个:§a按4",
            "§e以此类推...",
            "§e尽量多的出售该商品:§aShift+左键点击"
    );


}
