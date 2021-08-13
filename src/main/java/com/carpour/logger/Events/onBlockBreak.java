package com.carpour.logger.Events;

import com.carpour.logger.Discord.Discord;
import com.carpour.logger.Main;
import com.carpour.logger.Utils.FileHandler;
import com.carpour.logger.MySQL.MySQLData;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class onBlockBreak implements Listener {

    private final Main main = Main.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        String playerName = player.getName();
        World world = player.getWorld();
        String worldName = world.getName();
        int x = event.getBlock().getLocation().getBlockX();
        int y = event.getBlock().getLocation().getBlockY();
        int z = event.getBlock().getLocation().getBlockZ();
        String blockName;
        blockName = event.getBlock().getType().toString();
        blockName = blockName.replaceAll("_", " ");
        String serverName = main.getConfig().getString("Server-Name");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        if (player.hasPermission("logger.exempt")){ return; }

        if (!event.isCancelled() && (main.getConfig().getBoolean("Log-to-Files")) && (main.getConfig().getBoolean("Log.Block-Break"))) {

            if (main.getConfig().getBoolean("Staff.Enabled") && player.hasPermission("logger.staff")){

                Discord.staffChat(player, "⛏️ **|** \uD83D\uDC6E\u200D♂️ [" + worldName + "] Has broke **" + blockName + "** at X = " + x + " Y = " + y + " Z = " + z, false, Color.red);

                try {

                    BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getStaffFile(), true));
                    out.write("[" + dateFormat.format(date) + "] " + "[" + worldName + "] The Staff <" + playerName + "> has broke " + blockName + " at X= " + x + " Y= " + y + " Z= " + z + "\n");
                    out.close();

                    if (main.getConfig().getBoolean("MySQL.Enable") && (main.getConfig().getBoolean("Log.Block-Break")) && (main.sql.isConnected())) {


                        MySQLData.blockBreak(serverName, worldName, playerName, blockName, x, y, z, true);

                    }

                } catch (IOException e) {

                    System.out.println("An error occurred while logging into the appropriate file.");
                    e.printStackTrace();

                }

                return;

            }

            Discord.blockBreak(player, "⛏️ [" + worldName + "] Has broke **" + blockName + "** at X = " + x + " Y = " + y + " Z = " + z, false, Color.red);

            try {

                BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getBlockBreakLogFile(), true));
                out.write("[" + dateFormat.format(date) + "] " + "[" + worldName + "] The Player <" + playerName + "> has broke " + blockName + " at X= " + x + " Y= " + y + " Z= " + z + "\n");
                out.close();

            } catch (IOException e) {

                System.out.println("An error occurred while logging into the appropriate file.");
                e.printStackTrace();

            }
        }

        if ((main.getConfig().getBoolean("MySQL.Enable")) && (main.getConfig().getBoolean("Log.Block-Break")) && (main.sql.isConnected())){

            try {

                MySQLData.blockBreak(serverName, worldName, playerName, blockName, x, y, z, false);

            }catch (Exception e){

                e.printStackTrace();

            }
        }
    }
}