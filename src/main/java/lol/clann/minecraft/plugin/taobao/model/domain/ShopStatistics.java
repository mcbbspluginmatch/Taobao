package lol.clann.minecraft.plugin.taobao.model.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author pyz
 * @date 2019/6/22 7:42 PM
 */
@Getter
@Setter
@ToString
public class ShopStatistics {
    private long id;
    private int goodsSaleCount;
    private int goodsBuyCount;
    private int totalSaleCount;
    private long totalSaleCost;
    private int totalBuyCount;
    private long totalBuyCost;
    private long totalCost;
}
