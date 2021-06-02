package com.te.top;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

public class VanillaRatingTop extends RatingTop {
	Statistic criteria;
	Object subcriteria;

	
	public VanillaRatingTop(Statistic c, String name) {
		super(name);
		criteria = c;
	}

	public VanillaRatingTop(Statistic c, String name, String subname) {
		super(name, subname);
		criteria = c;
		if (TopUtils.contains(TopUtils.MATERIAL_STATISTIC_LIST, criteria)) {
			add_activator = subname;
			subcriteria = Material.valueOf(subname.toUpperCase());
		}
		if (criteria == Statistic.KILL_ENTITY) {
			add_activator = subname;
			subcriteria = EntityType.valueOf(subname.toUpperCase());
		}
	}

	@Override
	public void getStatistic(StatPlayer p) {
		long value = p.getStat(criteria, subcriteria);
		try_update( value, p.getName() );
	}

	@Override
	public void getUniqueStatistic(StatPlayer p) {
		long value = p.getStat(criteria, subcriteria);
		try_add( value, p.getName() );
	}
}
