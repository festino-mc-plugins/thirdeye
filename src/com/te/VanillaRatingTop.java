package com.te;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public class VanillaRatingTop extends RatingTop {
	Statistic criteria;
	Object additional;

	
	public VanillaRatingTop(Statistic c, String a, String h, String s1, String s2, String m1, String m2, String e1, String e2, String e3) {
		super(a, h, s1, s2, m1, m2, e1, e2, e3);
		criteria = c;
	}

	public VanillaRatingTop(Statistic c, String a, String a2, String h, String s1, String s2, String m1, String m2, String e1, String e2, String e3) {
		super(a, a2, h, s1, s2, m1, m2, e1, e2, e3);
		criteria = c;
		if(criteria == Statistic.MOB_KILLS) {
			//EntityType.
			if(add_activator == "bat");
			if(add_activator == "squid");
			if(add_activator == "chick");
			if(add_activator == "cow");
			if(add_activator == "pig");
			if(add_activator == "sheep");

			if(add_activator == "cat");
			if(add_activator == "dog");
			if(add_activator == "parrot");
			
			if(add_activator.contains("cave") || add_activator.contains("poison"));
			if(add_activator == "creeper");
			if(add_activator == "spider");
			if(add_activator == "enderman");
			
			if(add_activator == "drowned");
			if(add_activator == "zombie");
			if(add_activator == "villager");
			if(add_activator == "zombie");
			if(add_activator == "husk");

			if(add_activator == "skeleton");
			if(add_activator == "wither");
			if(add_activator == "stray");

			if(add_activator == "magma");
			if(add_activator == "slime");

			if(add_activator == "pigman");
			if(add_activator == "blaze");
			if(add_activator == "silverfish");
			if(add_activator == "witch");
			
			if(add_activator == "salmon");
			if(add_activator == "wither");
			if(add_activator == "phantom");
		}
	}
}
