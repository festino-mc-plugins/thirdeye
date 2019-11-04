package com.te;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The MIT License
 * Copyright (c) 2015 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Loads the data of offline players
 * 
 * Should be compatible with versions later than 1.6.4
 * 
 * @author Techcable
 */
public class OfflinePlayerLoader extends Reflection {
    
    /**
     * Returns the given players data
     * 
     * Loads the player's data from its file if it is offline
     * If the player is online the online version is returned
     * 
     * Players returned by this method may or may not be spawned and should only be used to access data
     * 
     * @param id the player's uuid
     * 
     * @return a player's data
     * 
     * @throws RuntimeException if the loading failed
     */
    public static Player loadPlayer(UUID id) {
        return loadPlayer(Bukkit.getOfflinePlayer(id));
    }
    
    /**
     * Returns the given players data
     * 
     * Loads the player's data from its file if it is offline
     * If the player is online the online version is returned
     * 
     * Players returned by this method may or may not be spawned and should only be used to access data
     * 
     * @param player the player
     * 
     * @return a player's data
     * 
     * @throws RuntimeException if the loading failed
     */
    public static Player loadPlayer(OfflinePlayer player) {
        if (player == null) return null;
        if (player instanceof Player) {
            return (Player) player;
        }
        return loadPlayer(player.getUniqueId(), player.getName());
    }
    
    private static Player loadPlayer(UUID id, String name) {
        Object server = getMinecraftServer();
        Object interactManager = newPlayerInteractManager();
        Object worldServer = getWorldServer();
        Object profile = newGameProfile(id, name);
        Class<?> entityPlayerClass = getNmsClass("EntityPlayer");
        Constructor entityPlayerConstructor = makeConstructor(entityPlayerClass, getNmsClass("MinecraftServer"), getNmsClass("WorldServer"), getUtilClass("com.mojang.authlib.GameProfile"), getNmsClass("PlayerInteractManager"));
        Object entityPlayer = callConstructor(entityPlayerConstructor, server, worldServer, profile, interactManager);
        Player player = (Player) getBukkitEntity(entityPlayer);
        return player;
    }

    private static Object newGameProfile(UUID id, String name) {
        Class<?> gameProfileClass = getUtilClass("com.mojang.authlib.GameProfile");
        if (gameProfileClass == null) { //Before uuids
            return name;
        }
        Constructor gameProfileConstructor = null;
        gameProfileConstructor = makeConstructor(gameProfileClass, UUID.class, String.class);
        if (gameProfileConstructor == null) { //Verson has string constructor
            gameProfileConstructor = makeConstructor(gameProfileClass, String.class, String.class);
            return callConstructor(gameProfileConstructor, id.toString(), name);
        } else { //Version has uuid constructor
            return callConstructor(gameProfileConstructor, id, name);
        }
    }

    private static Object newPlayerInteractManager() {
        Object worldServer = getWorldServer();
        Class<?> playerInteractClass = getNmsClass("PlayerInteractManager");
        Class<?> worldClass = getNmsClass("WorldServer");
        Constructor c = makeConstructor(playerInteractClass, worldClass);
        return callConstructor(c, worldServer);
    }

    private static Object getWorldServer() {
        Object server = getMinecraftServer();
        Class<?> minecraftServerClass = getNmsClass("MinecraftServer");
        //((CraftServer)Bukkit.getServer()).getServer().getWorlds().iterator().next()
        Method getWorlds = makeMethod(minecraftServerClass, "getWorlds");
        Method iterator = makeMethod(Iterable.class, "iterator");
        Method next = makeMethod(Iterator.class, "next");
        return callMethod(next, callMethod(iterator, callMethod(getWorlds, server)));
        //Method getWorldServer = makeMethod(minecraftServerClass, "getWorldServer", int.class);
        //return callMethod(getWorldServer, server, 0);
    }

    //NMS Utils

    private static Object getMinecraftServer() {
        return callMethod(makeMethod(getCbClass("CraftServer"), "getServer"), Bukkit.getServer());
    }

    private static Entity getBukkitEntity(Object o) {
        Method getBukkitEntity = makeMethod(o.getClass(), "getBukkitEntity");
        return callMethod(getBukkitEntity, o);
    }

	
	public long getOfflinePlayerStatistic(OfflinePlayer player, Statistic statistic) {
        //Default server world always be the first of the list
        File worldFolder = new File(Bukkit.getServer().getWorlds().get(0).getWorldFolder(), "stats");
        File playerStatistics = new File(worldFolder, player.getUniqueId().toString() + ".json");
        if(playerStatistics.exists()){
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) parser.parse(new FileReader(playerStatistics));
            } catch (IOException | ParseException e) {
                Bukkit.getLogger().log(Level.WARNING, "Falha ao ler o arquivo de estatisticas do jogador " + player.getName(), e);
            }
            StringBuilder statisticNmsName = new StringBuilder("stat.");
            for(char character : statistic.name().toCharArray()) {
                if(statisticNmsName.charAt(statisticNmsName.length() - 1) == '_') {
                    statisticNmsName.setCharAt(statisticNmsName.length() - 1, Character.toUpperCase(character));
                }else {
                    statisticNmsName.append(Character.toLowerCase(character));
                }
            }
            if(jsonObject.containsKey(statisticNmsName.toString())) {
                return (long) jsonObject.get(statisticNmsName.toString());
            }else {
                //This statistic has not yet been saved to file, so it is 0
                return 0;
            }
        }
        //Any statistic can be -1?
        return -1;
    }
}