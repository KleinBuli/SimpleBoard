package de.kleinbuli.simpleBoard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Prefix {

    private static final Map<UUID, Prefix> prefixes = new HashMap<>();

    private final Map<Team.Option, Team.OptionStatus> options = new HashMap<>();

    private final Component prefix;
    private final int tabPriority;

    private NamedTextColor namedTextColor;
    private String permission;

    public Prefix(Component prefix, int tabPriority) {
        this.prefix = prefix;
        this.tabPriority = tabPriority;
    }

    public void applyTo(Player player) {
        setPrefix(player, this);
        PrefixHandler.updatePrefix();
    }

    public Prefix option(Team.Option option, Team.OptionStatus status) {
        options.put(option, status);
        return this;
    }

    public Map<Team.Option, Team.OptionStatus> options() {
        return this.options;
    }

    public String permission() {
        return this.permission;
    }

    public NamedTextColor namedTextColor() {
        return this.namedTextColor;
    }

    public Prefix namedTextColor(NamedTextColor namedTextColor) {
        this.namedTextColor = namedTextColor;
        return this;
    }

    public Prefix permission(String permission) {
        this.permission = permission;
        return this;
    }

    public int tabPriority() {
        return tabPriority;
    }


    public Component getPrefix() {
        return prefix;
    }

    public String getPermission() {
        return permission;
    }

    public static void setPrefix(Player player, Prefix prefix) {
        prefixes.put(player.getUniqueId(), prefix);
    }

    public static void removePrefix(Player player) {
        prefixes.remove(player.getUniqueId());
    }

    public static Prefix getPrefix(Player player) {
        return prefixes.get(player.getUniqueId());
    }

    public static Map<UUID, Prefix> getPrefixes() {
        return prefixes;
    }
}
