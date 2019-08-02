package lol.clann.minecraft.plugin.taobao.constant;

import lombok.Getter;

/**
 *
 * @author pyz
 * @date 2019/6/2 11:38 PM
 */
@Getter
public enum ShopTypeEnum {
    sale("出售"), buy("收购"); // 不规范的 enum 命名 —— 754503921
    private final String displayName;

    ShopTypeEnum(String displayName) {
        this.displayName = displayName;
    }
}
