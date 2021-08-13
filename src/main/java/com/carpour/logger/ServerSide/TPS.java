package com.carpour.logger.ServerSide;

import com.carpour.logger.Discord.Discord;
import com.carpour.logger.Main;
import com.carpour.logger.Utils.FileHandler;
import com.carpour.logger.MySQL.MySQLData;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TPS implements Runnable {

    private final Main main = Main.getInstance();

    public static int tickCount = 0;
    public static long[] TICKS = new long[600];
    String serverName = main.getConfig().getString("Server-Name");
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(int ticks) {
        if (tickCount <= ticks) {
            return 20.0D;
        }
        int target = (tickCount - 1 - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];
        return ticks / (elapsed / 1000.0D);
    }

    public void run() {

        if (main.getConfig().getBoolean("Log-to-Files") && (main.getConfig().getBoolean("Log.TPS"))) {

            if ((main.getConfig().getInt("TPS.Value-Medium")) <= 0 || (main.getConfig().getInt("TPS.Value-Medium") >= 20) ||
                    (main.getConfig().getInt("TPS.Value-Critical")) <= 0 || (main.getConfig().getInt("TPS.Value-Critical") >= 20)){

                return;

            }

            TICKS[(tickCount % TICKS.length)] = System.currentTimeMillis();

            tickCount += 1;

            if (getTPS() <= main.getConfig().getInt("TPS.Value-Medium")) {

                Discord.TPS("The TPS has dropped to " + getTPS(), false, Color.red);

                try {

                    BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getTPSLogFile(), true));
                    out.write("[" + dateFormat.format(date) + "] The TPS has dropped to " + getTPS() + "\n");
                    out.close();

                } catch (IOException e) {

                    System.out.println("An error occurred while logging into the appropriate file.");
                    e.printStackTrace();

                }

            } else if (getTPS() <= main.getConfig().getInt("TPS.Value-Critical")) {

                Discord.TPS("⚠️ WARNING! The TPS has dropped to " + getTPS(), false, Color.red);

                try {

                    BufferedWriter out = new BufferedWriter(new FileWriter(FileHandler.getTPSLogFile(), true));
                    out.write("[" + dateFormat.format(date) + "] WARNING! The TPS has dropped to " + getTPS() + "\n");
                    out.close();

                } catch (IOException e) {

                    System.out.println("An error occurred while logging into the appropriate file.");
                    e.printStackTrace();

                }

            }
        }

        if (main.getConfig().getBoolean("MySQL.Enable") && (main.getConfig().getBoolean("Log.TPS")) && (main.sql.isConnected())){

            if ((main.getConfig().getInt("TPS.Value-Medium")) <= 0 || (main.getConfig().getInt("TPS.Value-Medium") >= 20) ||
                    (main.getConfig().getInt("TPS.Value-Critical")) <= 0 || (main.getConfig().getInt("TPS.Value-Critical") >= 20)){

                return;

            }

            if (getTPS() <= main.getConfig().getInt("TPS.Value-Medium")) {

                try {

                    MySQLData.TPS(serverName, getTPS());

                }catch (Exception e){

                    e.printStackTrace();

                }

            } else if (getTPS() <= main.getConfig().getInt("TPS.Value-Critical")){

                try {

                    MySQLData.TPS(serverName, getTPS());

                }catch (Exception e){

                    e.printStackTrace();

                }
            }
        }
    }
}