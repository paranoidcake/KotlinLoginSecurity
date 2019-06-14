package com.paranoidcake.KotlinLoginSecurity

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PlayerHandler {
    companion object {
        fun kickPlayer(player: Player, message: String) {
            Bukkit.getScheduler().callSyncMethod(Main.pluginRef, fun () {
                player.kickPlayer(message)
            })
        }

        fun jailPlayer(player: Player, location: Location, hideInventory: Boolean = true) {
            Main.jailedPlayers[player.uniqueId] = true

            if(hideInventory) {
                Main.inventories[player.uniqueId.toString()] = player.inventory.contents

                if(Main.inventoryData.get("${player.uniqueId}") == null) {
                    Main.inventoryData.set("${player.uniqueId}.size", player.inventory.contents.size)

                    for (i in 0 until player.inventory.contents.size) {
                        if(player.inventory.contents[i] != null) {
                            Main.inventoryData.createSection("${player.uniqueId}.$i", player.inventory.contents[i].serialize())
                        } else {
                            Main.inventoryData.set("${player.uniqueId}.$i", null)
                        }
                    }

                    Main.inventoryData.save(Main.inventoryFile)

                    player.inventory.clear()
                    player.updateInventory()
                }
            }

            if(player.location.distance(location) > 10){
                Main.playerPosData.createSection(player.uniqueId.toString(), player.location.serialize())

                Main.playerPosData.save(Main.playerPosFile)
            }

            player.teleport(location)

            Main.log("Player ${player.name} jailed at [${location.x}, ${location.y}, ${location.z}]")

            player.walkSpeed = 0.0f // Default speed = 0.2f
            player.isInvulnerable = true
            player.gameMode = GameMode.ADVENTURE
            player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, Int.MAX_VALUE, 128))
        }

        fun unJailPlayer(player: Player, returnPlayer:Boolean=true) {
            Main.jailedPlayers[player.uniqueId] = false

            player.walkSpeed = 0.2f
            player.gameMode = GameMode.SURVIVAL
            player.isInvulnerable = false
            player.removePotionEffect(PotionEffectType.JUMP)

            if(returnPlayer) {
                if(Main.jails.isNotEmpty()) {
                    try {
                        val returnLocation = Location.deserialize(Main.playerPosData.getConfigurationSection(player.uniqueId.toString())!!.getValues(false))

                        player.teleport(returnLocation)
                    } catch (e: KotlinNullPointerException) {
                        Main.log("No previous location found for player ${player.name}")
                    }
                } else {
                    Main.error("No jail to return ${player.name} from!")
                }
            }

            // TODO: Fix "LinkedHashMap as ItemStack ClassCastException
            // player.inventory.contents = Main.inventories.getOrDefault(player.uniqueId.toString(), recoverInventory(player.uniqueId.toString()))

            // Always using recoverInventory() for now
            player.inventory.contents = recoverInventory(player.uniqueId.toString())
            player.updateInventory()

            Main.inventoryData.set(player.uniqueId.toString(), null)
            Main.inventoryData.save(Main.inventoryFile)

            Main.log("Player ${player.name} unjailed")
        }

        private fun recoverInventory (uniqueId: String): Array<ItemStack?> {
            val size = Main.inventoryData["$uniqueId.size"]?.toString()?.toInt()

            return if (size != null) {
                val recovered = Array<ItemStack?>(size) { null }

                for(i in 0 until size) {
                    val cur = Main.inventoryData.getConfigurationSection("$uniqueId.$i")?.getValues(false)
                    if(cur != null) {
                        recovered[i] = ItemStack.deserialize(cur)
                    }
                }

                recovered
            } else {

                emptyArray()
            }
        }
    }


}