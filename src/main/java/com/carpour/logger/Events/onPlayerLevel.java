package com.carpour.logger.Events;

import com.carpour.logger.Discord.Discord;
import com.carpour.logger.Main;
import com.carpour.logger.Utils.FileHandler;
import com.carpour.logger.MySQL.MySQLData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class onPlayerLevel implements Listener {

    private final Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLevelChange(PlayerLevelChangeEvent event){

        Player player = event.getPlayer();
        String playerName = player.getName();
        int Level = main.getConfig().getInt("Player-Level.Log-Above");
        double pLevel= event.getNewLevel();
        String serverName = main.getConfig().getString("Server-Name");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        if (player.hasPermission("logger.exempt")){ return; }

        if (main.getConfig().getBoolean("Log-to-Files") && (main.getConfig().getBoolean("Log.Player-Level"))) {

            if (main.getConfig().getBoolean("Staff.Enabled") && player.hasPermission("logger.staff")){

                if (pLevel == Level) {

                    Discord.staffChat(player, "⬆️ **|** \uD83D\uDC6E\u200D♂ Has arrived to the set amount of Level which is **" + Level + "**", false, Color.green);

                    try {

                        BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getStaffFile(), true));
                        out.write("[" + dateFormat.format(date) + "] The Level of the Staff <" + playerName + "> has arrived to the set amount which is " + Level + "\n");
                        out.close();

                        if (main.getConfig().getBoolean("MySQL.Enable") && (main.getConfig().getBoolean("Log.Player-Level")) && (main.sql.isConnected())) {

                            MySQLData.levelChange(serverName, playerName,true);

                        }

                    } catch (IOException e) {

                        System.out.println("An error occurred while logging into the appropriate file.");
                        e.printStackTrace();

                    }
                }

                return;

            }

            if (pLevel == Level) {

                Discord.playerLevel(player, "⬆️ Has arrived to the set amount of Level which is **" + Level + "**", false, Color.green);

                try {

                    BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getPlayerLevelFile(), true));
                    out.write("[" + dateFormat.format(date) + "] The Level of the Player <" + playerName + "> has arrived to the set amount which is " + Level + "\n");
                    out.close();

                } catch (IOException e) {

                    System.out.println("An error occurred while logging into the appropriate file.");
                    e.printStackTrace();

                }
            }

        }

        if (main.getConfig().getBoolean("MySQL.Enable") && (main.getConfig().getBoolean("Log.Player-Level")) && (main.sql.isConnected())){

            if (pLevel == Level) {

                try {

                    MySQLData.levelChange(serverName, playerName, false);

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        }
    }
}
