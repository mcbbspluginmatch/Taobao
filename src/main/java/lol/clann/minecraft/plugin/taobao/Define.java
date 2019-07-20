package lol.clann.minecraft.plugin.taobao;

import com.google.auto.service.AutoService;
import lol.clann.minecraft.springboot.api.spi.plugin.PluginDefine;

/**
 *
 * @author pyz
 * @date 2019/6/2 11:36 PM
 */
@AutoService(PluginDefine.class)
public class Define implements PluginDefine {
    @Override
    public String getId() {
        return "Taobao";
    }

    @Override
    public String getDisplayName() {
        return "淘宝商城";
    }

    @Override
    public String getVersion() {
        return "0.0.8";
    }
}
