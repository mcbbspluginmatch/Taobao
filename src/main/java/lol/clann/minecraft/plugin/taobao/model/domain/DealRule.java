package lol.clann.minecraft.plugin.taobao.model.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author pyz
 * @date 2019/6/4 11:10 PM
 */
@Getter
@Setter
@ToString
public class DealRule {
    private long id;
    private boolean allowSale;
    private boolean matchDurability;
    private boolean matchNbt;
    private long minPrice;
    private long maxPrice;
    private ItemStack item;
}