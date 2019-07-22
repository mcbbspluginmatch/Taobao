package lol.clann.minecraft.plugin.taobao.command;

import lol.clann.minecraft.plugin.taobao.Config;
import lol.clann.minecraft.plugin.taobao.TaobaoDealService;
import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lol.clann.minecraft.plugin.taobao.model.domain.DealRule;
import lol.clann.minecraft.springboot.api.annotation.command.*;
import lol.clann.minecraft.springboot.api.bukkit.command.constant.CommandConcurrentLevelEnum;
import lol.clann.minecraft.springboot.api.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.api.context.PluginContext;
import lol.clann.minecraft.springboot.api.model.core.Plugin;
import lol.clann.minecraft.springboot.api.utils.ConfigUtils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.function.Consumer;

/**
 *
 * @author pyz
 * @date 2019/7/14 3:24 PM
 */
@CommandDefine(name = "taobao", description = "淘宝商城", order = -1)
public class TaobaoOPCommand {
    private Plugin plugin = PluginContext.getPlugin("Taobao");
    @Autowired
    private TaobaoDealService taobaoDealService;
    @Autowired
    private Config config;
    @Autowired
    private ItemStackUtils itemStackUtils;

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

    @Command(showArgs = "addSaleRule [--allowSale=<true|false>] [--matchNbt] [--matchDurability] [--minPrice=<最低售价>] [--ruleId=<规则标识>] [--comment=\"<备注>\"]",
            cooldown = 1000,
            onlyPlayer = true,
            concurrentLevel = CommandConcurrentLevelEnum.SENDER,
            des = "为手里物品添加销售规则.\n" +
                    "--allowSale: 允许出售(默认true)\n" +
                    "--matchNbt: 是否匹配nbt,如lore,附魔等(默认true)\n" +
                    "--matchDurability: 是否匹配耐久值(默认true)\n" +
                    "--minPrice=<最低售价>: 指定允许的最低售价\n" +
                    "--ruleId=<规则标识>: 指定规则标识,方便有权限的玩家绕过这条规则的限制\n" +
                    "--comment=\"<备注>\": 备注,没什么用,方便腐竹看",
            async = true)
    private void addSaleRule(@Sender Player sender
            , @Flag("allowSale") boolean allowSale
            , @Flag("matchNbt") boolean matchNbt
            , @Flag("matchDurability") boolean matchDurability
            , @Flag("minPrice") long minPrice
            , @Optional @Flag("ruleId") String ruleId
            , @Optional @Flag("comment") String comment
    ) {
        ItemStack hand = sender.getItemInHand();
        if (itemStackUtils.isEmpty(hand)) {
            sender.sendMessage(ChatColor.RED + "请把物品拿在手上");
            return;
        }
        DealRule rule = new DealRule();
        rule.setItem(hand.clone());
        rule.getItem().setAmount(1);
        rule.setAllowSale(allowSale);
        rule.setMatchNbt(matchNbt);
        rule.setMatchDurability(matchDurability);
        rule.setMinPrice(minPrice);
        rule.setRuleId(ruleId);
        rule.setComment(comment);
        config.getDealRules().add(rule);
        plugin.saveConfig();
        sender.sendMessage(ChatColor.GREEN + "出售规则添加成功!");
    }

    /**
     * 设置系统商店
     *
     * @param sender
     * @param owner
     */
    @Command(showArgs = "setAdmin <owner>", cooldown = 1000, concurrentLevel = CommandConcurrentLevelEnum.SENDER, des = "设置为系统商店", async = true)
    private void setAdmin(@Sender CommandSender sender, String owner) {
        taobaoDealService.setAdmin(sender, owner);
    }

}
