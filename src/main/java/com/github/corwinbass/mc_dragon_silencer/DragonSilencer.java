package com.github.corwinbass.mc_dragon_silencer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DragonSilencer extends JavaPlugin implements Listener {

    private PacketAdapter adapter = null;

    @Override
    public void onEnable() {
        Plugin protocolLib = getServer().getPluginManager().getPlugin("ProtocolLib");
        boolean active = protocolLib != null && protocolLib.isEnabled();

        if (!active) {
            getLogger().info("This plugin requires ProtocolLib to function properly. Without it, this plugin will do nothing.");
        } else {
            try {
                this.adapter = new PacketAdapter(this, ListenerPriority.NORMAL,
                        PacketType.Play.Server.WORLD_EVENT) {
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Server.WORLD_EVENT) {
                            if (event.getPacket().getIntegers().read(0) == 1028) { // 1028 = ender dragon dies
                                event.setCancelled(true);  // block the packet, prevents playing the sound on whole server
                            }
                            if (event.getPacket().getIntegers().read(0) == 1023) { // 1023 = wither spawned
                                event.setCancelled(true); // cancel the sound of wither spawn for whole server
                            }
                        }
                    }
                };

                ProtocolLibrary.getProtocolManager().addPacketListener(this.adapter);
                getLogger().info("DragonSilencer successfully enabled.");
            } catch (Exception ex) {
                getLogger().info("DragonSilencer - error occurred during start up.");
                getLogger().info(ex.getMessage());
                getLogger().info(ex.toString());
            }
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getLocation().getWorld().getEnvironment() == World.Environment.THE_END)) return;
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            Location loc = event.getEntity().getLocation();
            loc.getWorld().getPlayers().forEach(player -> player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 1.0f));
        }
    }

    @EventHandler
    public void onWitherSpawn(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.WITHER) return;
        Location loc = event.getEntity().getLocation();
        List<Entity> nearby = event.getEntity().getNearbyEntities(32, 32, 32);
        nearby.stream().filter(e-> e.getType() == EntityType.PLAYER)
                .forEach(player->((Player)player).playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f));
    }

    @Override
    public void onDisable() {
        try {
            if (this.adapter!=null) {
                ProtocolLibrary.getProtocolManager().removePacketListener(this.adapter);
            }
            this.adapter = null;
            getLogger().info("DragonSilencer successfully disabled.");
        } catch(Exception ex) {
            getLogger().info("DragonSilencer - error occurred during disabling plugin.");
        }
    }
}
