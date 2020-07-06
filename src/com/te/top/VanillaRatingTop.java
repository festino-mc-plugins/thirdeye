package com.te.top;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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
		if (TopUtils.contains(TopManager.MATERIAL_STATISTIC_LIST, criteria)) {
			add_activator = subname;
			subcriteria = Material.valueOf(subname.toUpperCase());
		}
		if (criteria == Statistic.KILL_ENTITY) {
			add_activator = subname;
			subcriteria = EntityType.valueOf(subname.toUpperCase());
		}
	}

	@Override
	public void getStatistic(Player p) {
		int value;
		if (criteria == Statistic.KILL_ENTITY && subcriteria instanceof EntityType) {
			value = p.getStatistic(criteria, (EntityType) subcriteria);
		} else if (subcriteria != null && subcriteria instanceof Material) {
			value = p.getStatistic(criteria, (Material) subcriteria);
		} else {
			value = p.getStatistic(criteria);
		}

		try_add( value, p.getName() );
	}
}
