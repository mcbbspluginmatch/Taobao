package lol.clann.minecraft.plugin.taobao.message;

import lol.clann.minecraft.springboot.adapter.api.config.TranslateColoreCode;
import lol.clann.minecraft.springboot.adapter.model.message.MessageBuilder;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 *
 * @author pyz
 * @date 2019/6/23 12:14 PM
 */
@Getter
@ConfigSerializable
public class Message {
    @Setting(value = "enable", comment = "启用/停用")
    private boolean enable;
    @TranslateColoreCode
    @Setting(value = "message", comment = "消息")
    private MessageBuilder message;

    public Message() {
    }

    public Message(boolean enable, String message) {
        this.enable = enable;
        this.message = MessageBuilder.from(message);
    }
}
