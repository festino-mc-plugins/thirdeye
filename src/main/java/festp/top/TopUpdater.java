package festp.top;

import java.util.Arrays;
import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import festp.Logger;
import festp.utils.OfflinePlayerLoader;

public class TopUpdater implements Listener {
	private TopManager manager;
	long tick = 0;
	int frequency = 100;
	private String lastPlayer = "";
	private boolean isUpdating = false;
	
	public TopUpdater(TopManager manager) {
		this.manager = manager;
	}
	
	public void tick() {
		tick++;
		if (tick % frequency != 0 || isUpdating) {
			return;
		}
		
		Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
		if (players.length == 0) {
			return;
		}
		
		Arrays.sort(players, new Comparator<Player>() {
			@Override
			public int compare(Player p1, Player p2) {
				return p1.getName().compareTo(p2.getName());
			}
		});
		Player next = null;
		for (Player player : players) {
			if (lastPlayer.compareTo(player.getName()) < 0) {
				next = player;
				break;
			}
		}
		if (next == null) {
			next = players[0];
		}
		
		OnlineStatPlayer statPlayer = new OnlineStatPlayer(next);
		for (RatingTop rt : manager.getTops()) {
			rt.getStatistic(statPlayer);
		}
		
		lastPlayer = next.getName();
     	manager.getTabCompleter().updateOptions();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (isUpdating) {
			return;
		}
		OnlineStatPlayer player = new OnlineStatPlayer(event.getPlayer());
		for (RatingTop rt : manager.getTops()) {
			rt.getStatistic(player);
		}
     	manager.getTabCompleter().updateOptions();
	}
	
	/** Could cause lags */
	/*@EventHandler
	public void onStatisticIncrement(PlayerStatisticIncrementEvent event) {
		Player player = event.getPlayer();

		for (RatingTop rt : manager.getTops()) {
			if (rt instanceof VanillaRatingTop)
				continue;
			
			VanillaRatingTop vrt = (VanillaRatingTop) rt;
			if (vrt.criteria == event.getStatistic()) {
				rt.getStatistic(player);
			}
		}
	}*/
	
	public void reloadTops_UsingStatFiles() {
		isUpdating = true;
		clearTops();
		
		new Thread(new Runnable() {
		    public void run() {
		     	try {
		     		int error_count = 0, player_count = 0;
		     		
		     		for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers())
					{
						player_count++;
						StatPlayer player;
						if (p.isOnline()) {
							player = new OnlineStatPlayer(p.getPlayer());
						} else {
							player = new JSONStatPlayer(p.getName(), p.getUniqueId());
							if (!player.isValid()) {
								error_count++;
								//Logger.severe("Player error: no file (name: " + p.getName() + ", UUID: " + p.getUniqueId() + ")");
								continue;
							}
						}
						
						for (RatingTop rt : manager.getTops()) {
							rt.getUniqueStatistic(player);
						}
						
					}
					Logger.severe("Tops: " + player_count + " players, " + error_count + " errors");
				} catch (Exception e) {
					Logger.severe("Tops: 1 big error");
					e.printStackTrace();
				}
		     	
		     	manager.getTabCompleter().updateOptions();
		     	isUpdating = false;
		    }
		}).start();
	}

	/** <b>Warning:</b> Cause memory leak*/
	public void reloadTops_OfflinePlayerLoading() {
		isUpdating = true;
		clearTops();
		
		new Thread(new Runnable() {
		    public void run() {
		     	try {
		     		int error_count = 0, player_count = 0;
		     		for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers())
					{
						player_count++;
						Player player;
						if (p.isOnline()) {
							player = p.getPlayer();
						} else {
							try {
								player = OfflinePlayerLoader.loadPlayer(p.getUniqueId());
							} catch(Exception e) {
								e.printStackTrace();
								error_count++;
								continue;
							}
						}
						
						for (RatingTop rt : manager.getTops()) {
							rt.getUniqueStatistic(new OnlineStatPlayer(player));
						}
						
					}
					System.out.println("[ThirdEye] Tops: " + player_count + " players, " + error_count + " errors");
				} catch (Exception e) {
					System.out.println("[ThirdEye] Tops: 1 big error");
					e.printStackTrace();
				}
		     	
		     	manager.getTabCompleter().updateOptions();
		     	isUpdating = false;
		     	//System.out.print("FINISHED " + Thread.currentThread() + " (" + Thread.activeCount() + ")");
		    }
		}).start();
	}
	
	private void clearTops()
	{
		for (RatingTop rt : manager.getTops()) {
			for (int i = 0; i < rt.places.length; i++) {
				if (rt.places[i] == null) // end of spend places
					break;
				rt.places[i] = null;
			}
		}
	}
}
