package com.paranoidcake.KotlinLoginSecurity.Commands

import com.paranoidcake.KotlinLoginSecurity.Main
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandListJails: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        val jailsSize = Main.jails.size

        return if(jailsSize == 0) {
            sender.sendMessage("[KotLogin]: ${ChatColor.RED}No jails found!")

            true
        } else {
            var output = String()
            for (i in 0 until jailsSize) {
                val currentJail = Main.jails[i]
                    output += "\n${ChatColor.DARK_GRAY}ID: ${ChatColor.WHITE}$i, ${ChatColor.DARK_GRAY}world: ${ChatColor.WHITE}${currentJail.world?.name}, ${ChatColor.DARK_GRAY}pos: ${ChatColor.WHITE}[${currentJail.x}, ${currentJail.y}, ${currentJail.z}]"
            }
            sender.sendMessage("[KotLogin]: ${ChatColor.LIGHT_PURPLE}Jails:$output")

            true
        }

    }
}