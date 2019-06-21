package com.paranoidcake.KotlinLoginSecurity.commands

import com.paranoidcake.KotlinLoginSecurity.Main
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandUnregister: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        return when {
            Main.loggedPlayers.getOrDefault(sender.uniqueId, false) -> {
                Main.passwords.minus(sender.uniqueId.toString())
                Main.loggedPlayers[sender.uniqueId] = false

                Main.passwordData[sender.uniqueId.toString()] = null
                Main.passwordData.save(Main.passwordFile)

                sender.sendMessage("[KotLogin]: ${ChatColor.GREEN}Successfully unregistered!")
                true
            }
            else -> {
                sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Please login/register first!")
                true
            }
        }
    }
}