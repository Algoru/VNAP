/*
 * Copyright 2017 Alvaro Stagg [alvarostagg@protonmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package VNAP;

import PluginReference.*;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class MyPlugin extends PluginBase {
    public static MC_Server server;
    public static ArrayList<String> inPlayers;

    private Configuration cfg;
    private Connection conn;
    private MC_Location playerLocation;

    private final String CONFIGURATION_FILE = "./plugins_mod/VNAP/db.json";

    @Override
    public void onStartup(MC_Server server) {
        this.server = server;
        inPlayers = new ArrayList<String>();

        boolean firstUse = false;

        File configDir = new File("./plugins_mod/VNAP");
        if (!configDir.exists()) {
            firstUse = true;
            System.out.println(" [*] VNAP configuration dir not found. Creating...");
            if (configDir.mkdir())
                System.out.println(" [+] VNAP configuration dir was created !");
            else
                System.out.println(" [-] Unable to create configuration dir !");
        } else
            System.out.println(" [+] VNAP configuration dir found !");

        try {
            cfg = new Configuration(CONFIGURATION_FILE);
            cfg.readConfiguration();
        } catch (Exception e) {
            System.out.println(" [-] Unable to set configuration from configuration file: " + e.getMessage());
        }

        if (firstUse) {
            System.out.println(" [*] SEEMS LIKE THIS IS THE FIRST TIME YOU RUN THIS PLUGIN.");
            System.out.println(" [*] PLEASE, EDIT \"VNAP/db.json\" FILE TO CONFIGURE YOUR");
            System.out.println(" [*] DATABASE. OTHERWISE, THE PLUGIN WON'T WORK !");
        } else {
            System.out.println(" [+] Trying DB configuration...");
            try {
                conn = new Connection(cfg);
                System.out.println(" [+] Connected to database !");

                server.registerCommand(new Register(conn));
                server.registerCommand(new Login());
            } catch (Exception e) {
                System.out.println(" [-] Unable to connect with database: " + e.getMessage());
            }
        }

        System.out.println("=== VNAP loaded ===");
    }

    @Override
    public void onShutdown() {
        if (!inPlayers.isEmpty())
            inPlayers.clear();

        System.out.println("=== VNAP disabled ===");
    }

    private void printRequestLoginMessage(MC_Player mc_player) {
        mc_player.sendMessage(ChatColor.RED + "Please use /login Pass to login");
        mc_player.sendMessage(ChatColor.RED + "¿Don't have an account? Create it with:");
        mc_player.sendMessage(ChatColor.RED + "/register <password> <password>");
    }

    @Override
    public void onPlayerJoin(MC_Player player) {
        playerLocation = player.getLocation();

        String playerName = player.getName();
        boolean playerExists = false;

        try {
            playerExists = conn.playerExists(playerName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (playerExists) {
            player.sendMessage(ChatColor.RED + "Welcome " +
                    ChatColor.BOLD + ChatColor.UNDERLINE + ChatColor.AQUA + playerName +
                    ChatColor.RESET + " to " + cfg.getServerName() + " !");
            player.sendMessage(ChatColor.RED + "Please, use /register <password> <password> to create an account");
        } else {
            player.sendMessage(ChatColor.RED + "Welcome back, " + ChatColor.AQUA + ChatColor.BOLD + ChatColor.UNDERLINE +
                    playerName + ChatColor.RESET + " !");
            player.sendMessage(ChatColor.RED + "Please use /login <password> to login");
        }

        player.setInvulnerable(true);
    }

    @Override
    public void onPlayerInput(MC_Player mc_player, String msg, MC_EventInfo ei) {
        if (!inPlayers.contains(mc_player.getName())) {
            if (!(msg.contains("/login") || msg.contains("/register"))) {
                ei.isCancelled = true;
                printRequestLoginMessage(mc_player);
            }
        }
    }

    @Override
    public void onAttemptPlayerMove(MC_Player mc_player, MC_Location locFrom, MC_Location locTo, MC_EventInfo ei) {
        if (!inPlayers.contains(mc_player.getName())) {
            if (!mc_player.getLocation().equals(playerLocation))
                mc_player.teleport(playerLocation);
        }
    }

    @Override
    public void onAttemptItemDrop(MC_Player mc_player, MC_ItemStack is, MC_EventInfo ei) {
        if (!inPlayers.contains(mc_player.getName())) {
            ei.isCancelled = true;
            printRequestLoginMessage(mc_player);
        }
    }

    @Override
    public void onAttemptItemPickup(MC_Player mc_player, MC_ItemStack is, boolean isXpOrb, MC_EventInfo ei) {
        if (!inPlayers.contains(mc_player.getName())) {
            ei.isCancelled = true;
            printRequestLoginMessage(mc_player);
        }
    }

    @Override
    public void onAttemptEntityInteract(MC_Player mc_player, MC_Entity ent, MC_EventInfo ei) {
        if (!inPlayers.contains(mc_player.getName())) {
            ei.isCancelled = true;
            printRequestLoginMessage(mc_player);
        }
    }

    @Override
    public void onAttemptBlockBreak(MC_Player mc_player, MC_Location loc, MC_EventInfo ei) {
        if (!inPlayers.contains(mc_player.getName())) {
            ei.isCancelled = true;
            printRequestLoginMessage(mc_player);
        }
    }

    @Override
    public void onAttemptPlaceOrInteract(MC_Player mc_player, MC_Location loc, MC_DirectionNESWUD dir, MC_Hand hand, MC_EventInfo ei) {
        if (!inPlayers.contains(mc_player.getName())) {
            ei.isCancelled = true;
            printRequestLoginMessage(mc_player);
        }
    }

    @Override
    public void onPlayerLogout(String playerName, UUID uuid) {
        if (inPlayers.contains(playerName))
            inPlayers.remove(playerName);
    }

    @Override
    public PluginInfo getPluginInfo() {
        PluginInfo info = new PluginInfo();
        info.description = "Register and login players to your Rainbow server.";
        info.name = "VNAP";
        info.version = "1.2";
        return info;
    }
}
