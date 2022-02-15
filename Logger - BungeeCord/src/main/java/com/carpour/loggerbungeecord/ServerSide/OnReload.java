package com.carpour.loggerbungeecord.ServerSide;

import com.carpour.loggerbungeecord.Database.External.ExternalData;
import com.carpour.loggerbungeecord.Database.SQLite.SQLiteData;
import com.carpour.loggerbungeecord.Discord.Discord;
import com.carpour.loggerbungeecord.Main;
import com.carpour.loggerbungeecord.Utils.FileHandler;
import com.carpour.loggerbungeecord.Utils.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class OnReload implements Listener {

    private final Main main = Main.getInstance();

    @EventHandler
    public void onServerReload(ProxyReloadEvent event){

        CommandSender cS = event.getSender();
        String playerName = event.getSender().getName();
        String serverName = main.getConfig().getString("Server-Name");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (main.getConfig().getBoolean("Log-Server.Reload")) {

            if (cS instanceof ProxiedPlayer) {

                ProxiedPlayer player = (ProxiedPlayer) event.getSender();
                String server = player.getServer().getInfo().getName();

                if (player.hasPermission("loggerproxy.exempt")) return;

                //Log To Files Handling
                if (main.getConfig().getBoolean("Log-to-Files")) {

                    if (main.getConfig().getBoolean("Staff.Enabled") && player.hasPermission("loggerproxy.staff.log")) {

                        if (!Messages.getString("Discord.Server-Reload-Player-Staff").isEmpty()) {

                            Discord.staffChat(player, Objects.requireNonNull(Messages.getString("Discord.Server-Reload-Player-Staff")).replaceAll("%time%", dateTimeFormatter.format(ZonedDateTime.now())).replaceAll("%server%", server), false);

                        }

                        try {

                            BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getStaffLogFile(), true));
                            out.write(Messages.getString("Files.Server-Reload-Player-Staff-").replaceAll("%time%", dateTimeFormatter.format(ZonedDateTime.now())).replaceAll("%server%", server).replaceAll("%player%", playerName) + "\n");
                            out.close();

                        } catch (IOException e) {

                            Main.getInstance().getLogger().warning("An error occurred while logging into the appropriate file.");
                            e.printStackTrace();

                        }

                        if (main.getConfig().getBoolean("External.Enable") && main.external.isConnected()) {


                            ExternalData.serverReload(serverName, playerName, true);

                        }

                        if (main.getConfig().getBoolean("SQLite.Enable") && main.getSqLite().isConnected()) {

                            SQLiteData.insertServerReload(serverName, playerName, true);

                        }

                        return;

                    }

                    try {

                        BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getReloadLogFile(), true));
                        out.write(Messages.getString("Files.Server-Reload-Player").replaceAll("%time%", dateTimeFormatter.format(ZonedDateTime.now())).replaceAll("%server%", server).replaceAll("%player%", playerName) + "\n");
                        out.close();

                    } catch (IOException e) {

                        Main.getInstance().getLogger().warning("An error occurred while logging into the appropriate file.");
                        e.printStackTrace();

                    }
                }

                //Discord Integration
                if (main.getConfig().getBoolean("Staff.Enabled") && player.hasPermission("loggerproxy.staff.log")) {

                    if (!Messages.getString("Discord.Server-Reload-Player-Staff").isEmpty()) {

                        Discord.staffChat(player, Objects.requireNonNull(Messages.getString("Discord.Server-Reload-Player-Staff")).replaceAll("%time%", dateTimeFormatter.format(ZonedDateTime.now())).replaceAll("%server%", server), false);

                    }

                } else {

                    if (!Messages.getString("Discord.Server-Reload-Player").isEmpty()) {

                        Discord.serverReload(playerName, Objects.requireNonNull(Messages.getString("Discord.Server-Reload-Player")).replaceAll("%time%", dateTimeFormatter.format(ZonedDateTime.now())).replaceAll("%server%", server), false);

                    }
                }

                //MySQL Handling
                if (main.getConfig().getBoolean("External.Enable") && main.external.isConnected()) {

                    try {

                        ExternalData.serverReload(serverName, playerName, player.hasPermission("loggerproxy.staff.log"));

                    } catch (Exception e) {

                        e.printStackTrace();

                    }
                }

                //SQLite Handling
                if (main.getConfig().getBoolean("SQLite.Enable") && main.getSqLite().isConnected()) {

                    try {

                        SQLiteData.insertServerReload(serverName, playerName, player.hasPermission("loggerproxy.staff.log"));

                    } catch (Exception exception) {

                        exception.printStackTrace();

                    }
                }

            } else {

                try {

                    BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getReloadLogFile(), true));
                    out.write(Messages.getString("Files.Server-Side.Reload-Console").replaceAll("%time%", dateTimeFormatter.format(ZonedDateTime.now())) + "\n");
                    out.close();

                } catch (IOException e) {

                    Main.getInstance().getLogger().warning("An error occurred while logging into the appropriate file.");
                    e.printStackTrace();

                }

                //Discord
                if (!Messages.getString("Discord.Server-Side.Restart-Console").isEmpty()) {

                    Discord.serverReload(null, Objects.requireNonNull(Messages.getString("Discord.Server-Side.Restart-Console")).replaceAll("%time%", dateTimeFormatter.format(ZonedDateTime.now())).replaceAll("%time%", dateTimeFormatter.format(ZonedDateTime.now())), false);

                }

                //MySQL Handling
                if (main.getConfig().getBoolean("External.Enable") && main.external.isConnected()) {

                    try {

                        ExternalData.serverReload(serverName, null, true);

                    } catch (Exception e) {

                        e.printStackTrace();

                    }
                }

                //SQLite Handling
                if (main.getConfig().getBoolean("SQLite.Enable") && main.getSqLite().isConnected()) {

                    try {

                        SQLiteData.insertServerReload(serverName, null, true);

                    } catch (Exception exception) {

                        exception.printStackTrace();

                    }
                }
            }
        }
    }
}
