package lol.clann.minecraft.plugin.taobao;

import com.google.auto.service.AutoService;
import lol.clann.minecraft.springboot.adapter.api.plugin.PluginDefine;

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
}
