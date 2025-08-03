package de.kleinbuli.simpleBoard.prefix;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Configuration for a tab list prefix that can be attached to players.
 * <p>
 * Stores display component, priority, optional permission requirement, and team options.
 * Tracks which prefix is currently assigned to each player.
 * </p>
 */
public class Prefix {

    private static final Map<UUID, Prefix> prefixes = new HashMap<>();

    private final Map<Team.Option, Team.OptionStatus> options = new HashMap<>();
    private final Component prefix;
    private final int tabPriority;

    private NamedTextColor namedTextColor;
    private String permission;

    /**
     * Creates a prefix with the given component and priority.
     * Priority is set alphabetically. (e.g. 10 is a lower priority than 9, but 91 would be higher than 9)
     *
     * @param prefix      the prefix component
     * @param tabPriority ordering priority (for tab list)
     */
    public Prefix(Component prefix, int tabPriority) {
        this.prefix = prefix;
        this.tabPriority = tabPriority;
    }

    /**
     * Assigns this prefix to a player and triggers the global prefix update handler.
     *
     * @param player the player to apply the prefix to
     */
    public void applyTo(Player player) {
        setPrefix(player, this);
        PrefixHandler.updatePrefix();
    }

    /**
     * Sets an individual team option for this prefix.
     *
     * @param option the team option to set
     * @param status the value for that option
     * @return this instance
     */
    public Prefix option(Team.Option option, Team.OptionStatus status) {
        options.put(option, status);
        return this;
    }

    /**
     * Returns the configured team options.
     *
     * @return map of team options to their statuses
     */
    public Map<Team.Option, Team.OptionStatus> options() {
        return this.options;
    }

    /**
     * Returns the permission node required for this prefix, or an empty string if none is set.
     *
     * @return permission node or empty string
     */
    public String permission() {
        return this.permission != null ? this.permission : "";
    }

    /**
     * Returns the assigned named text color, defaulting to white if not set.
     *
     * @return text color
     */
    public NamedTextColor namedTextColor() {
        return this.namedTextColor != null ? this.namedTextColor : NamedTextColor.WHITE;
    }

    /**
     * Sets the named text color for this prefix.
     *
     * @param namedTextColor the color to use
     * @return this instance
     */
    public Prefix namedTextColor(NamedTextColor namedTextColor) {
        this.namedTextColor = namedTextColor;
        return this;
    }

    /**
     * Sets the required permission for this prefix.
     *
     * @param permission permission node
     * @return this instance
     */
    public Prefix permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Returns the priority used for ordering.
     *
     * @return tab priority
     */
    public int tabPriority() {
        return tabPriority;
    }

    /**
     * Returns the component used as prefix.
     *
     * @return prefix component
     */
    public Component getPrefix() {
        return prefix;
    }

    /**
     * Returns the raw permission string, or null if not set.
     *
     * @return permission or null
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Associates a prefix with a player.
     *
     * @param player the target player
     * @param prefix the prefix to assign
     */
    public static void setPrefix(Player player, Prefix prefix) {
        prefixes.put(player.getUniqueId(), prefix);
    }

    /**
     * Removes any assigned prefix from the player.
     *
     * @param player the target player
     */
    public static void removePrefix(Player player) {
        prefixes.remove(player.getUniqueId());
    }

    /**
     * Retrieves the prefix currently assigned to the player.
     *
     * @param player the player
     * @return assigned prefix or null if none
     */
    public static Prefix getPrefix(Player player) {
        return prefixes.get(player.getUniqueId());
    }

    /**
     * Returns all player-to-prefix assignments.
     *
     * @return map of player UUIDs to their prefixes
     */
    public static Map<UUID, Prefix> getPrefixes() {
        return prefixes;
    }
}
