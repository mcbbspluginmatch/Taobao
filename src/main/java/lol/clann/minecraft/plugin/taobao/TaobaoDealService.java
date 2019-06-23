package lol.clann.minecraft.plugin.taobao;

import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.plugin.taobao.mapper.TaobaoMapper;
import lol.clann.minecraft.plugin.taobao.message.Message;
import lol.clann.minecraft.plugin.taobao.message.Messages;
import lol.clann.minecraft.plugin.taobao.model.domain.DealLog;
import lol.clann.minecraft.plugin.taobao.model.domain.Shop;
import lol.clann.minecraft.plugin.taobao.model.domain.ShopItem;
import lol.clann.minecraft.springboot.adapter.bukkit.rawmessage.RawMessage;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.InventoryUtils;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.JSUtils;
import lol.clann.minecraft.springboot.adapter.bukkit.utils.MenuUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责交易
 *
 * @author pyz
 * @date 2019/6/5 10:40 PM
 */
@Service
public class TaobaoDealService {
    private Economy economy;
    @Autowired
    private TaobaoMapper taobaoMapper;
    @Autowired
    private InventoryUtils inventoryUtils;
    @Autowired
    private Config config;
    @Autowired
    private ItemStackUtils itemStackUtils;
    @Autowired
    private MenuUtils menuUtils;
    @Autowired
    private JSUtils jsUtils;
    @Autowired
    private Messages messages;

    private String noShopMsg = ChatColor.RED + "店铺不存在";

    @PostConstruct
    private void init() {
        economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void buy(Player player, InventoryClickEvent event, long shopItemId, int count) {
        if (count == -1) {
            count = Integer.MAX_VALUE;
        }
        if (count <= 0) {
            player.sendMessage(ChatColor.RED + "无效的数量:" + count);
            return;
        }
        ShopItem shopItem = taobaoMapper.fetchShopItemByIdForUpdate(shopItemId);
        if (shopItem == null) {
            player.sendMessage(ChatColor.RED + "无效的商品ID:" + shopItemId);
            return;
        }
        if (shopItem.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "这是您自己的商店");
            return;
        }
        OfflinePlayer owner = Bukkit.getOfflinePlayer(shopItem.getOwner());
        if (owner == null) {
            player.sendMessage(ChatColor.RED + "店主账号已注销!");
            return;
        }
        int invCapability = inventoryUtils.capabilityFor(player.getInventory(), shopItem.getItem());
        if (invCapability == 0) {
            player.sendMessage(ChatColor.RED + "背包空间不足!");
            return;
        }
        if (shopItem.getCount() == 0) {
            player.sendMessage(ChatColor.RED + "库存不足!");
            return;
        }
        shopItem.getItem().setAmount(1);
        count = Math.min(count, shopItem.getCount());
        count = Math.min(count, invCapability);
        long maxBuyCount = (long) economy.getBalance(player) / shopItem.getPrice();
        if (maxBuyCount <= 0) {
            notify(player, messages.getNotEnoughMony(), new HashMap<>());
            return;
        }
        count = (int) Math.min(count, maxBuyCount);
        long cost = count * shopItem.getPrice();
        long tax = (long) Math.ceil(config.getSaleShopTax() * cost);
        EconomyResponse r = economy.withdrawPlayer(player, cost);
        if (!r.transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "扣款失败!");
            return;
        }
        r = economy.depositPlayer(owner, cost - tax);
        if (!r.transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "向店主转账失败!");
            economy.depositPlayer(player, cost);
            return;
        }
//        交易税转账
        depositTaxReceiver(tax);
//        更新商品信息
        shopItem.setCount(shopItem.getCount() - count);
//        生成日志
        DealLog log = new DealLog();
        log.setCost(cost);
        log.setTax(tax);
        log.setCount(count);
        log.setDay(LocalDate.now());
        log.setPlayer(player.getName());
        log.setShopId(shopItem.getShopId());
        log.setShopItemId(shopItem.getId());
        log.setType(shopItem.getType());
        taobaoMapper.insertDealLog(log);
        refreshShopItemStatistics(log.getShopItemId(), shopItem.getCount());
        refreshShopStatistics(log.getShopId());
//        添加物品到玩家背包
        List<ItemStack> items = new ArrayList();
        while (count > 0) {
            int part = Math.min(count, shopItem.getItem().getMaxStackSize());
            if (part == 0) {
                break;
            }
            count -= part;
            ItemStack item = shopItem.getItem().clone();
            item.setAmount(part);
            items.add(item);
        }
        player.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
        event.getInventory().setItem(event.getRawSlot(), shopItem.toIcon());


        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("player", player.getName());
        msgContext.put("count", log.getCount());
        msgContext.put("name", itemStackUtils.getDisplayName(shopItem.getItem()));
        msgContext.put("owner", owner.getName());
        msgContext.put("cost", log.getCost());
        msgContext.put("income", log.getCost() - log.getTax());
        msgContext.put("tax", log.getTax());

        // 通知
        notify(player, messages.getBuy(), msgContext);
        if (owner.isOnline()) {
            notify((Player) owner, messages.getBuyNotifyOwner(), msgContext);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void sale(Player player, InventoryClickEvent event, long shopItemId, int count) {
        if (count == -1) {
            count = Integer.MAX_VALUE;
        }
        if (count <= 0) {
            player.sendMessage(ChatColor.RED + "无效的数量:" + count);
            return;
        }
        ShopItem shopItem = taobaoMapper.fetchShopItemByIdForUpdate(shopItemId);
        if (shopItem == null) {
            player.sendMessage(ChatColor.RED + "无效的商品ID:" + shopItemId);
            return;
        }
        if (shopItem.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "这是您自己的商店");
            return;
        }
        OfflinePlayer owner = Bukkit.getOfflinePlayer(shopItem.getOwner());
        if (owner == null) {
            player.sendMessage(ChatColor.RED + "店主账号已注销!");
            return;
        }
//        计算出售数量
        int shopCapability = shopItem.getMaxCount() - shopItem.getCount();
        if (shopCapability <= 0) {
            player.sendMessage(ChatColor.RED + "店铺空间不足!");
            return;
        }
        int itemCount = inventoryUtils.count(player.getInventory(), shopItem.getItem());
        if (itemCount == 0) {
            player.sendMessage(ChatColor.RED + "背包中没有该商品!");
            return;
        }
        long banlance = (long) economy.getBalance(owner);
        int maxSaleCount = (int) (banlance / shopItem.getPrice());
        if (maxSaleCount == 0) {
            notify(player, messages.getNotEnoughMony(), new HashMap<>());
            return;
        }
        count = Math.min(Math.min(Math.min(shopCapability, itemCount), maxSaleCount), count);
//        从背包提取指定数量的物品
        inventoryUtils.pull(player.getInventory(), shopItem.getItem(), count);
        long cost = count * shopItem.getPrice();
        long tax;
        long cost1 = taobaoMapper.calcSysShopDailyCost(player.getName());
        long cost2 = cost1 + cost;
        if (taobaoMapper.isSysShop(shopItem.getShopId())) {
//            系统商店,动态税率
            Map<String, Object> map = new HashMap<>();
//            当日交易额
            map.put("cost", cost1);
            long tax1 = ((Number) jsUtils.evelScript(config.getSysBuyShopTaxScript(), map)).longValue();
            map.put("cost", cost2);
            long tax2 = (long) Math.ceil(((Number) jsUtils.evelScript(config.getSysBuyShopTaxScript(), map)).doubleValue());
            tax = tax2 - tax1;
        } else {
//            玩家商店静态税率
            tax = (long) Math.ceil((config.getBuyShopTax() * cost));
        }
        EconomyResponse r = economy.withdrawPlayer(owner, cost);
        if (!r.transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "店主扣款失败!");
            return;
        }
        r = economy.depositPlayer(player, cost - tax);
        if (!r.transactionSuccess()) {
            player.sendMessage(ChatColor.RED + "收款失败!");
            economy.depositPlayer(owner, cost);
            return;
        }
//        交易税转账
        depositTaxReceiver(tax);
//        更新商品信息
        shopItem.setCount(shopItem.getCount() + count);
//        生成日志
        DealLog log = new DealLog();
        log.setCost(cost);
        log.setTax(tax);
        log.setCount(count);
        log.setDay(LocalDate.now());
        log.setPlayer(player.getName());
        log.setShopId(shopItem.getShopId());
        log.setShopItemId(shopItem.getId());
        log.setType(shopItem.getType());
        taobaoMapper.insertDealLog(log);
        refreshShopItemStatistics(log.getShopItemId(), shopItem.getCount());
        refreshShopStatistics(log.getShopId());
        event.getInventory().setItem(event.getRawSlot(), shopItem.toIcon());

        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("player", player.getName());
        msgContext.put("count", log.getCount());
        msgContext.put("name", itemStackUtils.getDisplayName(shopItem.getItem()));
        msgContext.put("owner", owner.getName());
        msgContext.put("cost", log.getCost());
        msgContext.put("income", log.getCost() - log.getTax());
        msgContext.put("tax", log.getTax());
        msgContext.put("todayTotalCost", cost2);

        // 通知
        notify(player, messages.getSale(), msgContext);
        if (owner.isOnline()) {
            notify((Player) owner, messages.getSaleNotifyOwner(), msgContext);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void setOrder(Player player, long shopItemId, int order) {
        ShopItem shopItem = taobaoMapper.fetchShopItemByIdForUpdate(shopItemId);
        if (!shopItem.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "这不是您的商店");
            return;
        }
        if (taobaoMapper.fetchShopItemByOrder(shopItem.getShopId(), shopItem.getType(), order) != null) {
            player.sendMessage(ChatColor.RED + "该序号已被使用,请更换");
            return;
        }
        shopItem.setOrder(order);
        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("money", config.getSetOrderCost());
        msgContext.put("id", shopItemId);
        msgContext.put("order", order);

        if (withdrawPlayer(player, config.getSetOrderCost(), true)) {
            taobaoMapper.update(shopItem);
            notify(player, messages.getSetOrder(), msgContext);
        } else {
            notify(player, messages.getRequireMony(), msgContext);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void newSale(Player player, long price) {
        newShopItem(player, ShopTypeEnum.sale, price, Integer.MAX_VALUE);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void newBuy(Player player, long price, int maxCount) {
        newShopItem(player, ShopTypeEnum.buy, price, maxCount);
    }

    private void newShopItem(Player player, ShopTypeEnum type, long price, int maxCount) {
        if (price <= 0) {
            player.sendMessage(ChatColor.RED + "价格必须为整正数!");
            return;
        }
        if (maxCount <= 0) {
            player.sendMessage(ChatColor.RED + "店铺容量必须为正数!");
            return;
        }
        ItemStack item = player.getItemInHand();
        if (itemStackUtils.isEmpty(item)) {
            player.sendMessage(ChatColor.RED + "请把物品拿在手上!");
            return;
        }
        item = item.clone();
        item.setAmount(1);
        Shop shop = fetchShopByOwnerForUpdate(player, player.getName(), noShopMsg);
        if (shop == null) {
            return;
        }
        int maxOrder = taobaoMapper.fetchMaxOrder(shop.getId(), type.name());
        ShopItem shopItem = new ShopItem();
        shopItem.setShopId(shop.getId());
        shopItem.setItem(item);
        shopItem.setOrder(maxOrder + 1000);
        shopItem.setMaxCount(maxCount);
        shopItem.setOwner(player.getName());
        shopItem.setPrice(price);
        shopItem.setType(type);
        long newShopItemCost = type == ShopTypeEnum.buy ? config.getNewBuyCost() : config.getNewSaleCost();

        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("player", player.getName());
        msgContext.put("name", itemStackUtils.getDisplayName(item));
        msgContext.put("type", shopItem.getType().getDisplayName());
        msgContext.put("money", newShopItemCost);

        if (withdrawPlayer(player, newShopItemCost, true)) {
            taobaoMapper.insertShopItem(shopItem);
            refreshShopStatistics(shopItem.getShopId());

//      消息通知
            msgContext.put("id", shopItem.getId());
            if (type == ShopTypeEnum.buy) {
                notify(player, messages.getNewBuy(), msgContext);
            } else {
                notify(player, messages.getNewSale(), msgContext);
            }

            if (type == ShopTypeEnum.buy) {
                broadcast(messages.getNewBuyBroadcast(), item, msgContext);
            } else {
                broadcast(messages.getNewSaleBroadcast(), item, msgContext);
            }
        } else {
//      消息通知
            notify(player, messages.getRequireMony(), msgContext);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteShopItem(Player player, long shopItemId, boolean force) {
        ShopItem shopItem = taobaoMapper.fetchShopItemByIdForUpdate(shopItemId);
        if (shopItem == null) {
            player.sendMessage(ChatColor.RED + "无效的shopItemId");
            return;
        }
        if (!shopItem.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "这不是您的店铺");
            return;
        }
        if (!force && shopItem.getCount() > 0) {
            player.sendMessage(ChatColor.RED + "请先清空库存!");
            return;
        }
        taobaoMapper.deleteShopItem(shopItem.getId());
        refreshShopStatistics(shopItem.getShopId());
//      消息通知
        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("player", player.getName());
        msgContext.put("id", shopItemId);
        msgContext.put("name", itemStackUtils.getDisplayName(shopItem.getItem()));
        msgContext.put("type", shopItem.getType().getDisplayName());
        notify(player, messages.getDeleteShopItem(), msgContext);
        broadcast(messages.getDeleteShopItemBroadcast(), shopItem.toIcon(), msgContext);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(CommandSender sender, String owner) {
        Shop shop = fetchShopByOwnerForUpdate(sender, owner, noShopMsg);
        if (shop == null) {
            return;
        }
        taobaoMapper.deleteShop(shop.getId());
        taobaoMapper.deleteShopItemByShopId(shop.getId());
        sender.sendMessage(ChatColor.GREEN + "店铺删除成功:" + owner);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void clear(CommandSender sender, String owner, ShopTypeEnum type) {
        Shop shop = fetchShopByOwnerForUpdate(sender, owner, noShopMsg);
        if (shop == null) {
            return;
        }
        taobaoMapper.clearShopItemCount(shop.getId(), type);
        sender.sendMessage(ChatColor.GREEN + "已情况玩家" + ChatColor.RED + owner + ChatColor.GREEN + "的" + ChatColor.RED + type.getDisplayName() + ChatColor.GREEN + "店铺");
    }

    @Transactional(rollbackFor = Throwable.class)
    public void putStock(Player player, InventoryClickEvent event, long shopItemId, int putCount) {
        if (putCount == -1) {
            putCount = Integer.MAX_VALUE;
        }
        if (putCount <= 0) {
            player.sendMessage(ChatColor.RED + "数量不能为负");
            return;
        }
        ShopItem shopItem = taobaoMapper.fetchShopItemByIdForUpdate(shopItemId);
        if (shopItem == null) {
            player.sendMessage(ChatColor.RED + "指定商品不存在");
            return;
        }
        if (shopItem.getType() != ShopTypeEnum.sale) {
            player.sendMessage(ChatColor.RED + "只有出售店铺可以添加库存!");
            return;
        }
        if (!shopItem.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "这不是您的店铺");
            return;
        }
        ItemStack itemStack = shopItem.getItem();
        int itemCount = inventoryUtils.count(player.getInventory(), itemStack);
        if (itemCount <= 0) {
            player.sendMessage(ChatColor.RED + "您背包内没有该物品");
            return;
        }
        int capability = shopItem.getMaxCount() - shopItem.getCount();
        if (capability <= 0) {
            player.sendMessage(ChatColor.RED + "店铺容量不足");
            return;
        }
        int count = Math.min(putCount, Math.min(itemCount, capability));
        long tax = (long) Math.ceil(config.getPutStockTax() * count);

        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("count", count);
        msgContext.put("money", tax);
        msgContext.put("type", shopItem.getType().getDisplayName());
        msgContext.put("name", itemStackUtils.getDisplayName(shopItem.getItem()));

        if (!withdrawPlayer(player, tax, true)) {
            notify(player, messages.getRequireMony(), msgContext);
            return;
        }
        inventoryUtils.pull(player.getInventory(), itemStack, count);
        shopItem.setCount(shopItem.getCount() + count);
        taobaoMapper.update(shopItem);
        event.getInventory().setItem(event.getRawSlot(), shopItem.toOwnerIcon());

        notify(player, messages.getPutStock(), msgContext);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void pullStock(Player player, InventoryClickEvent event, long shopItemId, int pullCount) {
        if (pullCount == -1) {
            pullCount = Integer.MAX_VALUE;
        }
        if (pullCount <= 0) {
            player.sendMessage(ChatColor.RED + "数量不能为负");
            return;
        }
        ShopItem shopItem = taobaoMapper.fetchShopItemByIdForUpdate(shopItemId);
        if (shopItem == null) {
            player.sendMessage(ChatColor.RED + "指定商品不存在");
            return;
        }
        if (!shopItem.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "这不是您的店铺");
            return;
        }
        ItemStack itemStack = shopItem.getItem();
        int itemCount = shopItem.getCount();
        if (itemCount <= 0) {
            player.sendMessage(ChatColor.RED + "库存不足");
            return;
        }
        int capability = inventoryUtils.capabilityFor(player.getInventory(), itemStack);
        if (capability <= 0) {
            player.sendMessage(ChatColor.RED + "背包容量不足");
            return;
        }
        int count = Math.min(pullCount, Math.min(itemCount, capability));
        int left = inventoryUtils.put(player.getInventory(), itemStack, count);
        count -= left;
        shopItem.setCount(shopItem.getCount() - count);
        taobaoMapper.update(shopItem);
        event.getInventory().setItem(event.getRawSlot(), shopItem.toOwnerIcon());

        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("count", count);
        msgContext.put("type", shopItem.getType().getDisplayName());
        msgContext.put("name", itemStackUtils.getDisplayName(shopItem.getItem()));

        notify(player, messages.getPullStock(), msgContext);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void setAdmin(CommandSender sender, String owner) {
        Shop shop = fetchShopByOwnerForUpdate(sender, owner, noShopMsg);
        if (shop == null) {
            return;
        }
        shop.setSys(true);
        taobaoMapper.updateShop(shop);
        taobaoMapper.setAllShopItemCountAndCapabilityByShopId(shop.getId());
        sender.sendMessage(String.format(ChatColor.GREEN + "设置%s的商店为系统商店", owner));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void setShopName(Player player, String name) {
        Shop shop = fetchShopByOwnerForUpdate(player, player.getName(), noShopMsg);
        if (shop == null) {
            player.sendMessage(String.format(ChatColor.RED + "该玩家没有商店:", name));
            return;
        }
        shop.setTitle(ChatColor.GREEN + name + ChatColor.WHITE);
        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("player", player.getName());
        msgContext.put("name", name);
        if (withdrawPlayer(player, config.getSetShopNameCost(), true)) {
            taobaoMapper.updateShop(shop);
            notify(player, messages.getRenameShop(), msgContext);
            broadcast(messages.getRenameShopBroadcast(), shop.getIcon(), msgContext);
        } else {
            notifyNotEnoughMoney(player, config.getSetShopNameCost());
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void createShop(Player player, String name) {
        Long shopId = taobaoMapper.fetchShopIdByOwner(player.getName());
        if (shopId != null) {
            player.sendMessage(ChatColor.RED + "店铺已存在,请勿重复操作");
            return;
        }
        Shop shop = new Shop();
        shop.setTitle(ChatColor.GREEN + name + ChatColor.WHITE);
        shop.setIcon(itemStackUtils.asCraftItemStackCopy(new ItemStack(Material.STONE)));
        shop.setOwner(player.getName());

        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("player", player.getName());
        msgContext.put("money", config.getCreateShopCost());
        msgContext.put("name", name);

        if (withdrawPlayer(player, config.getCreateShopCost(), true)) {
            taobaoMapper.insertShop(shop);
            msgContext.put("id", shop.getId());
            notify(player, messages.getCreateShop(), msgContext);
            broadcast(messages.getCreateShopBroadcast(), shop.getIcon(), msgContext);
        } else {
            notify(player, messages.getRequireMony(), msgContext);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void setIcon(Player player) {
        Shop shop = fetchShopByOwnerForUpdate(player, player.getName(), noShopMsg);
        if (shop == null) {
            player.sendMessage(ChatColor.RED + "您尚未开设商店,请输入指令查看帮助:/taobao");
            return;
        }
        ItemStack hand = player.getItemInHand();
        if (itemStackUtils.isEmpty(hand)) {
            player.sendMessage(ChatColor.RED + "请把物品拿在手上");
            return;
        }
        if (hand.getAmount() <= 0) {
            throw new RuntimeException("非法物品数量:" + hand.getAmount());
        }
        shop.setIcon(hand.clone());
        shop.getIcon().setAmount(1);

        Map<String, Object> msgContext = new HashMap<>();
        msgContext.put("player", player.getName());
        msgContext.put("money", config.getSetIconCost());
        msgContext.put("name", itemStackUtils.getDisplayName(shop.getIcon()));

        if (withdrawPlayer(player, config.getSetIconCost(), true)) {
            taobaoMapper.updateShop(shop);
            hand.setAmount(hand.getAmount() - 1);
            if (hand.getAmount() > 0) {
                player.setItemInHand(hand);
            } else {
                player.setItemInHand(null);
            }
            notify(player, messages.getSetShopIcon(), msgContext);
            broadcast(messages.getSetShopIconBroadcast(), shop.getIcon(), msgContext);
        } else {
            notify(player, messages.getRequireMony(), msgContext);
        }
    }

    private void refreshShopStatistics(long shopId) {
        taobaoMapper.updateShopStatistics(shopId);
    }

    /**
     * 刷新商品交易数量,交易额
     *
     * @param shopItemId
     */
    private void refreshShopItemStatistics(long shopItemId, long count) {
        taobaoMapper.updateShopItemAfterDeal(shopItemId, count);
    }

    private Shop fetchShopByOwnerForUpdate(CommandSender sender, String owner, String noShopNotify) {
        Long shopId = taobaoMapper.fetchShopIdByOwner(owner);
        if (shopId == null) {
            sender.sendMessage(noShopNotify);
            return null;
        }
        Shop shop = taobaoMapper.fetchShopByIdForUpdate(shopId);
        if (shop == null) {
            sender.sendMessage(noShopNotify);
            return null;
        }
        return shop;
    }

    private boolean withdrawPlayer(OfflinePlayer player, double money, boolean transferToTaxReceiver) {
        if (economy.has(player, money)) {
            EconomyResponse r = economy.withdrawPlayer(player, money);
            if (r.transactionSuccess() && transferToTaxReceiver) {
                depositTaxReceiver(money);
            }
            return r.transactionSuccess();
        } else {
            return false;
        }
    }

    private void depositTaxReceiver(double money) {
        if (config.getTaxReceiver() != null && !config.getTaxReceiver().isEmpty()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(config.getTaxReceiver());
            if (op != null) {
                economy.depositPlayer(op, money);
            }
        }
    }

    private void broadcast(Message msg, ItemStack showItemStack, Map<String, Object> msgContext) {
        if (msg != null && msg.isEnable()) {
            RawMessage.createRawMessage(msg.getMessage().resolve(msgContext))
                    .showItem(showItemStack)
                    .broadcast();
        }
    }

    private void broadcast(Message msg, Map<String, Object> msgContext) {
        if (msg != null && msg.isEnable()) {
            Bukkit.broadcastMessage(msg.getMessage().resolve(msgContext));
        }
    }

    private void notify(CommandSender sender, Message msg, ItemStack showItemStack, Map<String, Object> msgContext) {
        if (msg != null && msg.isEnable()) {
            RawMessage.createRawMessage(msg.getMessage().resolve(msgContext))
                    .showItem(showItemStack)
                    .send(sender);
        }
    }

    private void notify(CommandSender sender, Message msg, Map<String, Object> msgContext) {
        if (msg != null && msg.isEnable()) {
            sender.sendMessage(msg.getMessage().resolve(msgContext));
        }
    }

    private void notifyNotEnoughMoney(CommandSender sender, long money) {
        Message msg = messages.getRequireMony();
        if (msg != null && msg.isEnable()) {
            sender.sendMessage(msg.getMessage().builder().var("money", money).build());
        }
    }
}
