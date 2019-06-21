package com.paranoidcake.KotlinLoginSecurity

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener: Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (Main.passwords.containsKey(event.player.uniqueId.toString())) {
            if(Main.loggedPlayers[event.player.uniqueId] == true) {

                event.player.kickPlayer("[KotLogin]: Account already logged in")
            } else {
                if(!Main.jailedPlayers.getOrDefault(event.player.uniqueId, false)) {
                    try {
                        if(!Main.jailedPlayers.getOrDefault(event.player.uniqueId, false)){
                            PlayerHandler.jailPlayer(event.player, Main.jails[0])
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        Main.error("No jail to place player ${event.player.name} in!")
                    }
                }

                event.player.sendMessage("[KotLogin]: ${ChatColor.YELLOW}Please login to your account with /login <password>")
            }
        } else {
            Main.loggedPlayers[event.player.uniqueId] = false

            event.player.sendMessage("[KotLogin]: ${ChatColor.YELLOW}WIP Plugin")
            event.player.sendMessage("[KotLogin]: ${ChatColor.RED}Please register by typing /register <password>")
            event.player.sendMessage("[KotLogin]: ${ChatColor.DARK_RED}Passwords are stored in plaintext! Do not enter anything you want to keep secure!")
        }
    }

    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        Main.attempts[event.player.uniqueId] = 0
        Main.loggedPlayers[event.player.uniqueId] = false
    }

    @EventHandler
    fun onInvOpen(event: InventoryClickEvent) {
        if (Main.jailedPlayers.getOrDefault(event.whoClicked.uniqueId, false)){

            event.whoClicked.closeInventory()
            event.isCancelled = true
        }
    }
}