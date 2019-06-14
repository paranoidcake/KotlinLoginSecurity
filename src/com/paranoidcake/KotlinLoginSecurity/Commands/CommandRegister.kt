package com.paranoidcake.KotlinLoginSecurity.Commands

import com.paranoidcake.KotlinLoginSecurity.Main
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandRegister: CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        val message = args.joinToString()

        when {
            message == "" -> return false

            !Main.loggedPlayers.getOrDefault(sender.uniqueId, false) -> {
                val storedPass = Main.passwords.getOrDefault(sender.uniqueId.toString(), "")

                return if (storedPass == "") {
                    Main.passwords[sender.uniqueId.toString()] = message
                    Main.loggedPlayers[sender.uniqueId] = true
                    sender.sendMessage("[KotLogin]: ${ChatColor.GREEN}Registered ${ChatColor.WHITE}(you are automatically logged in)")

                    true
                }
                else {
                    sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Already registered!")
                    true
                }

            }
            else -> {
                sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Already registered!")
                return true
            }
        }

    }
}