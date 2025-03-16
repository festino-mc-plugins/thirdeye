package festp.top;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class OnlineStatPlayer implements StatPlayer {
	private final Player player;
	
	public OnlineStatPlayer(Player p)
	{
		player = p;
	}

	@Override
	public long getStat(Statistic criteria, Object subcriteria) {
		long value;
		if (criteria == Statistic.KILL_ENTITY && subcriteria instanceof EntityType) {
			value = player.getStatistic(criteria, (EntityType) subcriteria);
		} else if (subcriteria != null && subcriteria instanceof Material) {
			value = player.getStatistic(criteria, (Material) subcriteria);
		} else {
			value = player.getStatistic(criteria);
		}
		return value;
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public boolean isValid() {
		return player != null && player.isOnline();
	}
	
}
