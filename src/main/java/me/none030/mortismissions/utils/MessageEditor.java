package me.none030.mortismissions.utils;

import org.bukkit.ChatColor;

import java.util.Objects;

public class MessageEditor {

    private String message;

    public MessageEditor(String message) {
        this.message = Objects.requireNonNullElse(message, "");
    }

    public void color() {
        setMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void replace(String value, String replacement) {
        setMessage(message.replace(value, replacement));
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
