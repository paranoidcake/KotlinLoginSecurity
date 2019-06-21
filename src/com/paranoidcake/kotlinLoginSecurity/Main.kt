package com.paranoidcake.kotlinLoginSecurity

import com.paranoidcake.kotlinLoginSecurity.commands.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap
import java.io.File
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import kotlin.collections.ArrayList

class Main: JavaPlugin() {
    companion object {
        val loggedPlayers = HashMap<UUID, Boolean>()
        val jailedPlayers = HashMap<UUID, Boolean>()
        val inventories = HashMap<String, Array<ItemStack>>()

        lateinit var passwords: HashMap<String, String>
        lateinit var jails: ArrayList<Location>

        lateinit var playerPosFile: File
        lateinit var playerPosData: YamlConfiguration

        lateinit var inventoryFile: File
        lateinit var inventoryData: YamlConfiguration

        lateinit var passwordFile: File
        lateinit var passwordData: YamlConfiguration

        val attempts = HashMap<UUID, Int>()

        lateinit var pluginRef: Plugin

        fun broadcast(message: String, color: Any = ChatColor.WHITE) {
            Bukkit.broadcastMessage("[KotLogin]: $color$message")
        }

        fun log(message: String) {

            Bukkit.getLogger().info("[kotlinLoginSecurity] $message")
        }
        fun error(message: String) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[kotlinLoginSecurity] &4Error: $message"))
        }
    }

    private val jailsFile = File(dataFolder, "jails.yml")
    private val jailsData = YamlConfiguration.loadConfiguration(jailsFile)

    override fun onEnable() {
        pluginRef = server.pluginManager.getPlugin("kotlinLoginSecurity")!!

        // --------------------- Assign lateinit vars
        playerPosFile = File(dataFolder, "playerPositions.yml")
        playerPosData = YamlConfiguration.loadConfiguration(playerPosFile)

        inventoryFile = File(dataFolder, "playerInventories.yml")
        inventoryData = YamlConfiguration.loadConfiguration(inventoryFile)

        passwordFile = File(dataFolder, "passwords.yml")
        passwordData = YamlConfiguration.loadConfiguration(passwordFile)

        passwords = try {
            passwordData.getConfigurationSection("passwords")!!.getValues(false) as HashMap<String, String>
        } catch (e: KotlinNullPointerException) {
            HashMap()
        } catch (e: TypeCastException) {
            HashMap()
        }

        try {
            val size = jailsData.get("jails.size").toString().toInt()

            jails = ArrayList()

            for(i in 0 until size) {
                jails.add(Location.deserialize(jailsData.getConfigurationSection("jails.$i")!!.getValues(false)))
            }
        } catch (e: KotlinNullPointerException) {
            broadcast("Null jail saved, generating default jail...", ChatColor.YELLOW)
            val world = pluginRef.server.worlds[0]
            val spawn = world.spawnLocation
            jails = arrayListOf(Location(pluginRef.server.worlds[0], spawn.x, spawn.y, spawn.z))
        } catch (e: NumberFormatException) {
            broadcast("No jails saved, generating default jail...", ChatColor.YELLOW)
            val world = pluginRef.server.worlds[0]
            val spawn = world.spawnLocation
            jails = arrayListOf(Location(pluginRef.server.worlds[0], spawn.x, spawn.y, spawn.z))
        }

        // --------------------- Register events + commands
        server.pluginManager.registerEvents(PlayerListener(), this)

        this.getCommand("register")!!.setExecutor(CommandRegister())
//        this.getCommand("unregister")!!.setExecutor(CommandUnregister()) TODO: Handle hot registration
        this.getCommand("login")!!.setExecutor(CommandLogin())
        this.getCommand("listjails")!!.setExecutor(CommandListJails())
        this.getCommand("addjail")!!.setExecutor(CommandAddJail())
        this.getCommand("removejail")!!.setExecutor(CommandRemoveJail())


    }

    override fun onDisable() {
        passwordData.createSection("passwords", passwords)
        passwordData.save(passwordFile)
        log("Passwords saved!")

        jailsData.set("jails.size", jails.size)
        for(i in 0 until jails.size) {
            jailsData.createSection("jails.$i", jails[i].serialize())
        }
        jailsData.save(jailsFile)
    }
}