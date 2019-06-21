package com.paranoidcake.KotlinLoginSecurity.commands

import com.paranoidcake.KotlinLoginSecurity.Main
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandReregister: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        val logged = Main.loggedPlayers[sender.uniqueId]
        val message = args.joinToString()

        return if(logged != null) {
            if(logged == true) {
                sender.sendMessage("[KotLogin]: ${ChatColor.GREEN}Successfully re-registered!")

                true
            } else {
                sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Please login first!")

                true
            }
        } else {
            sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Please register first!")

            true
        }
    }
}