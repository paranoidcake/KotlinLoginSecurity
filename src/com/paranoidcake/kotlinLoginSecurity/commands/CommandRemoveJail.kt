package com.paranoidcake.kotlinLoginSecurity.commands

import com.paranoidcake.kotlinLoginSecurity.Main
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandRemoveJail: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        return when {
            args.isEmpty() -> {
                sender.sendMessage("[KotLogin]: ${ChatColor.RED}Please enter an id!")

                true
            }
            args.size == 1 -> {
                val id = args[0].toInt()

                Main.jails.removeAt(id)
                sender.sendMessage("[KotLogin]: ${ChatColor.GREEN}Removed jail $id")

                true
            }
            else -> {
                sender.sendMessage("[KotLogin]: ${ChatColor.RED}Please enter only one id!")

                false
            }
        }
    }
}