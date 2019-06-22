package lol.clann.minecraft.plugin.taobao.constant;

import lombok.Getter;

/**
 *
 * @author pyz
 * @date 2019/6/2 11:38 PM
 */
@Getter
public enum ShopTypeEnum {
    sale("出售"), buy("收购");
    private final String displayName;

    ShopTypeEnum(String displayName) {
        this.displayName = displayName;
    }
}
