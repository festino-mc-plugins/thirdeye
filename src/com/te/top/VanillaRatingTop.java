package com.te.top;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public class VanillaRatingTop extends RatingTop {
	Statistic criteria;
	Object subcriteria;

	
	public VanillaRatingTop(Statistic c, String name, String h, String s1, String s2, String m1, String m2, String e1, String e2, String e3) {
		super(name, h, s1, s2, m1, m2, e1, e2, e3);
		criteria = c;
	}

	public VanillaRatingTop(Statistic c, String name, String subname, String h, String s1, String s2, String m1, String m2, String e1, String e2, String e3) {
		super(name, subname, h, s1, s2, m1, m2, e1, e2, e3);
		criteria = c;
		if(criteria == Statistic.KILL_ENTITY) {
			add_activator = subname;
			subcriteria = EntityType.valueOf(subname.toUpperCase());
		}
	}
}
