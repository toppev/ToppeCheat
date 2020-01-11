package com.toppecraft.toppecheat.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ClickableMessage {


    public static void sendClickableMessage(Player p, String message, String command, String... hover) {
        p.sendMessage(message);
        TextComponent text = new TextComponent(TextComponent.fromLegacyText(message));
        if (command != null) {
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }
        if (hover != null && hover.length > 0) {
            ComponentBuilder component = new ComponentBuilder(hover[0]);
            for (int i = 1; i < hover.length; i++) {
                component.append("\n" + hover[0]);
            }
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component.create()));
        }
        //	p.spigot().sendMessage(text);
    }

}
