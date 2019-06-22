package lol.clann.minecraft.plugin.taobao.model.domain;

import lol.clann.minecraft.plugin.taobao.constant.ShopTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 *
 * @author pyz
 * @date 2019/6/4 11:09 PM
 */
@Getter
@Setter
@ToString
public class DealLog {
    private long id;
    private long shopId;
    private long shopItemId;
    private ShopTypeEnum type;
    private String player;
    private int count;
    private long cost;
    private long tax;
    private LocalDate day;
}