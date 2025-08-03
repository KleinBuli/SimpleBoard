package de.kleinbuli.simpleBoard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * Utility class for creating and managing a dynamic sidebar scoreboard for each player.
 * <p>
 * The sidebar's content is provided through a {@link java.util.function.Supplier} that returns a list
 * of {@link net.kyori.adventure.text.Component}. Only changed lines are updated to improve performance.
 * </p>
 *
 */
public class Sidebar {

    private static final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

    private List<Component> lastLines = new ArrayList<>();

    private final Set<UUID> players = new HashSet<>();
    private final String name;

    private Supplier<List<Component>> linesProvider;
    private Scoreboard scoreboard;
    private Objective objective;
    private Component title;
    private int taskID;

    /**
     * Creates a new Sidebar with a given internal name.
     *
     * @param name internal identifier (used for the Objective)
     */
    public Sidebar(String name) {
        this.name = name;
    }

    /**
     * Sets the displayed title of the sidebar.
     *
     * @param title title as a Component (supports MiniMessage, RGB colors, etc.)
     * @return this Sidebar instance (for method chaining)
     */
    public Sidebar title(Component title) {
        this.title = title;
        return this;
    }

    /**
     * Gets the internal name of this sidebar.
     *
     * @return sidebar name
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the current title.
     *
     * @return title as a Component
     */
    public Component title() {
        return this.title;
    }

    /**
     * Shows this sidebar to the given player.
     * Creates and registers a new Scoreboard and Objective for that player.
     * Also triggers an immediate {@link #update()} call.
     *
     * @param player the player who should see the sidebar
     * @throws IllegalArgumentException if no title has been set
     */
    public void show(Player player) {

        if (title() == null) {
            throw new IllegalArgumentException("title cannot be empty");
        }

        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective(name(), Criteria.DUMMY, title());
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);

        players.add(player.getUniqueId());
        update();
    }

    /**
     * Hides the sidebar from the given player and removes them from the internal set.
     *
     * @param player the player whose sidebar should be removed
     */
    public void hide(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        players.remove(player.getUniqueId());
    }

    /**
     * Sets the supplier that will be called on each {@link #update()} to provide the current list of lines.
     *
     * @param provider supplier that returns a list of Components (top-to-bottom order)
     */
    public void builder(Supplier<List<Component>> provider) {
        this.linesProvider = provider;
    }

    /**
     * Updates the sidebar.
     * Only lines that have changed compared to the last update will be re-applied.
     * Extra lines from the previous update that no longer exist will be removed.
     * Typically called periodically or when the underlying data changes.
     */
    public void update() {
        if (linesProvider == null) return;

        List<Component> current = linesProvider.get();
        for (int i = 0; i < current.size(); i++) {
            Component newLine = current.get(i);
            if (i < lastLines.size() && Objects.equals(newLine, lastLines.get(i))) {
                continue;
            }
            setLine(newLine, current.size() - i);
        }
        if (lastLines.size() > current.size()) {
            for (int i = current.size(); i < lastLines.size(); i++) {
                int score = lastLines.size() - i;
                String entry = uniqueEntry(score);
                getScoreboard().resetScores(entry);
                Team team = getScoreboard().getTeam("line_" + score);
                if (team != null) team.unregister();
            }
        }
        lastLines = new ArrayList<>(current);
    }

    /**
     * Stops the scheduled updating task (if started via {@link #startUpdating}).
     */
    public void stopUpdating() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    /**
     * Starts a repeating update task that calls {@link #update()} at the given interval.
     *
     * @param plugin plugin instance for the scheduler
     * @param period interval in ticks (20 ticks = 1 second)
     */
    public void startUpdating(JavaPlugin plugin, long period) {
        taskID = Bukkit.getScheduler().runTaskTimer(plugin, this::update, 0, period).getTaskId();
    }

    /**
     * Returns the UUIDs of players currently viewing this sidebar.
     *
     * @return set of player UUIDs
     */
    public Set<UUID> getPlayers() {
        return players;
    }

    /**
     * Internal helper method to set a single line in the sidebar objective using a Team with a Component prefix.
     *
     * @param component line content
     * @param score     score value (position in the sidebar)
     */
    private void setLine(Component component, int score) {
        Scoreboard board = getObjective().getScoreboard();

        if (board == null) {
            return;
        }

        Team team = board.getTeam("line_" + score);
        String entry = uniqueEntry(score);
        if (team == null) {
            team = board.registerNewTeam("line_" + score);
        }

        team.prefix(component);
        team.addEntry(entry);
        objective.getScore(entry).setScore(score);
    }

    /**
     * Generates a unique, invisible entry string for a line based on its score position.
     *
     * @param line line number / score
     * @return string entry (must be unique in the scoreboard)
     */
    private static String uniqueEntry(int line) {
        return "§" + Integer.toHexString(line);
    }

    /**
     * Gets the underlying Objective.
     *
     * @return objective, or null if not yet initialized
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * Utility method for parsing MiniMessage strings with standard tags enabled (gradient, rainbow, colors, etc.).
     *
     * @param message MiniMessage string
     * @return parsed Component
     */
    public static Component mm(String message) {
        MiniMessage minimessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.color())
                        .resolver(StandardTags.decorations())
                        .resolver(StandardTags.gradient())
                        .resolver(StandardTags.rainbow())
                        .resolver(StandardTags.pride())
                        .resolver(StandardTags.shadowColor())
                        .resolver(StandardTags.translatable())
                        .resolver(StandardTags.font())
                        .build()).build();
        return minimessage.deserialize(message);
    }

    /**
     * Gets the scoreboard used by this sidebar.
     *
     * @return scoreboard, or null if not yet initialized
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
