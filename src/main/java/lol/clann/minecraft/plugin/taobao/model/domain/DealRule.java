package lol.clann.minecraft.plugin.taobao.model.domain;

import com.ruoshui.utils.java.lang.LazyField;
import lol.clann.minecraft.springboot.api.bukkit.utils.ItemStackUtils;
import lol.clann.minecraft.springboot.api.context.SpringContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 *
 * @author pyz
 * @date 2019/6/4 11:10 PM
 */
@Getter
@Setter
@ToString
@ConfigSerializable
public class DealRule {

    private static final LazyField<ItemStackUtils> itemStackUtils = LazyField.from(() -> SpringContext.getBean(ItemStackUtils.class));

    @Setting(value = "item", comment = "物品")
    private ItemStack item;
    @Setting(value = "matchDurability", comment = "是否匹配耐久值")
    private boolean matchDurability;
    @Setting(value = "matchNbt", comment = "是否匹配NBT")
    private boolean matchNbt;
    @Setting(value = "allowSale", comment = "若匹配成功,改物品是否允许出售")
    private boolean allowSale;
    @Setting(value = "allowSale", comment = "若匹配成功,限制该物品的最低售价")
    private long minPrice;
    @Setting(value = "comment", comment = "备注")
    private String comment;
    @Setting(value = "ruleId", comment = "可以为此规则指定一个唯一ID,方便用权限绕过出售规则限制")
    private String ruleId;

    public boolean match(Player player, ItemStack itemStack) {
        if (item.getType() != itemStack.getType()) {
            return false;
        }
        if (isMatchDurability() && item.getDurability() != itemStack.getDurability()) {
            return false;
        }
        if (StringUtils.hasText(getRuleId()) && player.hasPermission("taobao.dealRule.bypass." + getRuleId())) {
//                有特权,不限制
            return false;
        }
        if (isMatchNbt() && !Objects.equals(itemStackUtils.get().getTag(getItem()), itemStackUtils.get().getTag(itemStack))) {
            return false;
        }
        return true;
    }
}