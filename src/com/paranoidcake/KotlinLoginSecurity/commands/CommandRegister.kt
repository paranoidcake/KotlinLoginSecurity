package com.paranoidcake.KotlinLoginSecurity.commands

import com.paranoidcake.KotlinLoginSecurity.Main
import com.paranoidcake.KotlinLoginSecurity.PlayerHandler
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

                    PlayerHandler.unJailPlayer(sender, returnPlayer = false, recoverInventory = false)

                    true
                }
                else {
//                    sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Already registered!")
                    sender.sendMessage("[KotLogin]: ${ChatColor.DARK_RED}Something went wrong! ${ChatColor.YELLOW}(CommandRegister, line 21: Player had a saved password and true logged entry)")
                    sender.sendMessage("${ChatColor.YELLOW}Please tell me about it, or remember the error log for when I'm around")

                    true
                }

            }
            else -> {
                sender.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Already registered!")
                sender.sendMessage("${ChatColor.YELLOW}If you wish to change your password please use ${ChatColor.UNDERLINE}/reregister")
                return true
            }
        }

    }
}