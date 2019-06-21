package com.paranoidcake.KotlinLoginSecurity.commands

import com.paranoidcake.KotlinLoginSecurity.Main
import com.paranoidcake.KotlinLoginSecurity.PlayerHandler
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandLogin: CommandExecutor {

    private val attemptLimit = 3

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        val message = args.joinToString()
        if (message == "") return false

        return when {
            !Main.passwords.containsKey(sender.uniqueId.toString()) -> {
                sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Please register first!")
                true
            }
            !Main.loggedPlayers.getOrDefault(sender.uniqueId, false) -> {
                if (message == Main.passwords[sender.uniqueId.toString()]) {
                    Main.loggedPlayers[sender.uniqueId] = true
                    PlayerHandler.unJailPlayer(sender)

                    sender.sendMessage("[KotLogin]: ${ChatColor.GREEN}Logged in!")

                    true
                } else {
                    val curAttempts = Main.attempts.getOrDefault(sender.uniqueId, 0)
                    Main.attempts[sender.uniqueId] = curAttempts + 1

                    val attempts = attemptLimit - Main.attempts[sender.uniqueId]!!

                    if (attempts > 0) {
                        sender.sendMessage("[KotLogin]: ${ChatColor.RED}Incorrect password, $attempts attempts left")

                        true
                    } else {
                        PlayerHandler.kickPlayer(sender, "Too many failed login attempts")
                        Main.attempts[sender.uniqueId] = 0

                        true
                    }
                }
            }
            else -> {
                sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Already logged in!")

                true
            }
        }
    }
}