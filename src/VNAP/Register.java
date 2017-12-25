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

import PluginReference.ChatColor;
import PluginReference.MC_Command;
import PluginReference.MC_Player;

import java.util.List;

public class Register implements MC_Command {
    private Connection conn;

    public Register(Connection conn) {
        this.conn = conn;
    }

    @Override
    public String getCommandName() {
        return "register";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getHelpLine(MC_Player mc_player) {
        return ChatColor.GOLD + "/register <password> <password>" + ChatColor.WHITE + " -- Register into the server";
    }

    @Override
    public void handleCommand(MC_Player player, String[] args) {
        if (args.length != 2)
            player.sendMessage(this.getHelpLine(player));
        else {
            try {
                String playerName = player.getName();
                if (conn.playerExists(playerName)) {
                    player.sendMessage(ChatColor.RED + "You're already registered.");
                    player.sendMessage(ChatColor.RED + "Log Ing using /login <password>.");
                } else {
                    if (!args[0].equals(args[1])) {
                        player.sendMessage(ChatColor.RED + "Passwords do not match !");
                        player.sendMessage(ChatColor.RED + "Check your passwords and try again.");
                    } else {
                        conn.register(playerName, args[0]);
                        MyPlugin.logInPlayer(playerName);

                        player.setInvulnerable(false);
                        player.sendMessage(ChatColor.GOLD + "Successfully registered !");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.GRAY + "There was a problem while register. Contact an OP !");
            }
        }
    }

    @Override
    public boolean hasPermissionToUse(MC_Player mc_player) {
        return mc_player == null ? false : true;
    }

    @Override
    public List<String> getTabCompletionList(MC_Player mc_player, String[] strings) {
        return null;
    }
}
