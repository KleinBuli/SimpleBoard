package de.kleinbuli.simpleBoard.prefix;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PrefixHandler {

    /**
     *  Updates the tablist Prefix of every player and applies it
     *  to the current Scoreboard of the player.
     */
    public static void updatePrefix() {
        for (Player target : Bukkit.getOnlinePlayers()) {
            setupPlayerTeam(target);
        }
    }

    /**
     * Sets up the player team. Breaks if the prefix is null
     * @param player the player to set up
     */
    private static void setupPlayerTeam(Player player) {

        Scoreboard scoreboard = player.getScoreboard();
        Prefix prefix = Prefix.getPrefix(player);

        if (prefix == null) {
            throw new RuntimeException("player prefix cannot be empty -> apply prefix before");
        }

        String teamName = ("t" + prefix.tabPriority()
                + player.getUniqueId().toString().replace("-", ""))
                .substring(0, 16);

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }


        team.prefix(prefix.getPrefix().append(Component.text(" ")));
        team.color(prefix.namedTextColor());
        prefix.options().forEach(team::setOption);


        team.getEntries().forEach(team::removeEntry);
        team.addEntry(player.getName());
    }

}
