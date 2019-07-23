package lol.clann.minecraft.plugin.taobao;

import com.google.auto.service.AutoService;
import lol.clann.minecraft.springboot.api.spi.plugin.PluginDefine;
import lol.com.google.common.collect.Sets;

import java.util.Set;

/**
 * Taobao插件主类
 * 主类需要实现PluginDefine,并注解 @AutoService(PluginDefine.class)
 * 才能被SBP发现
 *
 * @author pyz
 * @date 2019/6/2 11:36 PM
 */
@AutoService(PluginDefine.class)
public class Define implements PluginDefine {
    /**
     * 插件ID,要求唯一
     *
     * @return
     */
    @Override
    public String getId() {
        return "Taobao";
    }

    /**
     * 插件名字,你随便填啥都行
     *
     * @return
     */
    @Override
    public String getDisplayName() {
        return "淘宝商城";
    }

    /**
     * 插件版本
     *
     * @return
     */
    @Override
    public String getVersion() {
        return "0.1.0";
    }

    /**
     * 依赖的bukkit插件
     * 当bukkit插件缺失时,依然会正常启动,只是会打印警告日志
     * @return
     */
    @Override
    public Set<String> getPluginDepends() {
        return Sets.newHashSet("Vault");
    }

    /**
     * 插件的包路径,所有类都要求在此处定义的包下
     * 可以指定多个包
     *
     * @return
     */
    @Override
    public Set<String> getBasePackages() {
        return Sets.newHashSet("lol.clann.minecraft.plugin.taobao");
    }
}
