package com.te;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Camera {
	Player p;
	Location l;
	
	public Camera(Player p, Location l) {
		this.p = p;
		this.l = l;
	}
}
