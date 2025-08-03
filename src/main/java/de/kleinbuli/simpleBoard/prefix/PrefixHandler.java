package de.kleinbuli.simpleBoard;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PrefixHandler {

    /**
     * Aktualisiert die Tablist + Nametags aller Spieler mit individuellem Prefix & Suffix.
     */
    public static void updatePrefix() {
        for (Player target : Bukkit.getOnlinePlayers()) {
            setupPlayerTeam(target);
        }
    }

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


        team.prefix(prefix.getPrefix());
        team.color(prefix.namedTextColor());
        prefix.options().forEach(team::setOption);


        team.getEntries().forEach(team::removeEntry);
        team.addEntry(player.getName());
    }

}
