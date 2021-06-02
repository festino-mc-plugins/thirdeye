package com.te.top;

import org.bukkit.Statistic;

public interface StatPlayer {

	public String getName();
	public long getStat(Statistic criteria, Object subcriteria);
	public boolean isValid();
}
