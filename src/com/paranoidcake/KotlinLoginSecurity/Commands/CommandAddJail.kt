package com.paranoidcake.KotlinLoginSecurity.Commands

import com.paranoidcake.KotlinLoginSecurity.Main
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.floor

class CommandAddJail: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        return if(sender.isOp){
            when {
                args.isEmpty() -> {
                    val pos = sender.location

                    pos.x = floor(pos.x)+0.5
                    pos.y = floor(pos.y)+0.5
                    pos.z = floor(pos.z)+0.5

                    Main.jails.add(pos)
                    sender.sendMessage("[KotLogin]: ${ChatColor.GREEN} Jail added at [${pos.x}, ${pos.y}, ${pos.z}]")

                    true
                } args.size == 3 -> {
                    val pos = Location(sender.world, args[0].toDouble(), args[1].toDouble(), args[2].toDouble())

                    Main.jails.add(pos)
                    sender.sendMessage("[KotLogin]: ${ChatColor.GREEN} Jail added at [${pos.x}, ${pos.y}, ${pos.z}]")

                    true
                }
                // TODO: World selection
                else -> {
                    sender.sendMessage("[KotLogin]: ${ChatColor.RED}Please enter a valid location!")

                    true
                }
            }
        } else {
            sender.sendMessage("[KotLogin]: ${ChatColor.DARK_RED}You must be an operator to perform this command!")

            true
        }
    }
}