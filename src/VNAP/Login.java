/*
 * Copyright 2018 Alvaro Stagg [alvarostagg@protonmail.com]
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

public class Login implements MC_Command {
    private Connection conn;

    public Login(Connection conn) { this.conn = conn; }

    @Override
    public String getCommandName() {
        return "login";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getHelpLine(MC_Player mc_player) {
        return ChatColor.GOLD + "/login <password>" + ChatColor.WHITE + " -- Login into the server";
    }

    @Override
    public void handleCommand(MC_Player player, String[] args) {
        if (args.length != 1)
            player.sendMessage(this.getHelpLine(player));
        else {
            try {
                String playerName = player.getName();
                boolean success = conn.login(playerName, args[0]);
                if (success) {
                    MyPlugin.logInPlayer(playerName);

                    player.setInvulnerable(false);
                    player.sendMessage(ChatColor.GREEN + "Successfully logged !");
                } else {
                    player.sendMessage(ChatColor.RED + "Wrong password !");
                    player.sendMessage(ChatColor.RED + "Check your password and try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.GRAY + "Internal error while trying to login !");
                player.sendMessage(ChatColor.GRAY + "Contact an OP !");
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
