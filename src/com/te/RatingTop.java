package com.te;

import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;

public class RatingTop {
	int TOP_SIZE = 50;
	RatingPlace[] places = new RatingPlace[TOP_SIZE];

	String main_activator;
	String add_activator = null;
	private String header;
	private String starts_with1; //... place takes player ...
	private String starts_with2p; //... place take playerS ...
	private String middle1; //who 
	private String middle2p;
	private String ends_with1; //hours on the server
	private String ends_with2_4;
	private String ends_with5_0;
	private final String last_nichnames_separator = " è ";
	
	public RatingTop(String a, String h, String s1, String s2, String m1, String m2, String e1, String e2, String e3) {
		main_activator = a;
		header = h;
		starts_with1 = s1;
		starts_with2p = s2;
		middle1 = m1;
		middle2p = m2;
		ends_with1 = e1;
		ends_with2_4 = e2;
		ends_with5_0 = e3;
	}
	public RatingTop(String a, String a2, String h, String s1, String s2, String m1, String m2, String e1, String e2, String e3) {
		main_activator = a;
		add_activator = a2;
		header = h;
		starts_with1 = s1;
		starts_with2p = s2;
		middle1 = m1;
		middle2p = m2;
		ends_with1 = e1;
		ends_with2_4 = e2;
		ends_with5_0 = e3;
	}
	
	//need optimization <= sorted array
	public void try_add(int value, String nick) {
		/*if(this instanceof VanillaRatingTop && ((VanillaRatingTop)this).criteria == Statistic.PLAY_ONE_MINUTE)
			value = (int) (Math.round((double)value/720)/100);  //div ticks by 20*60*60*/
			//value = (int) (Math.round((double)value*10/6)/100); //div minutes by 60
		if(this instanceof VanillaRatingTop && value == 0)
			return;
		for(int i=0; i < TOP_SIZE;i++) {
			RatingPlace rp = places[i];
			if(rp == null) {
				places[i] = new RatingPlace(value, nick);
				break;
			}
			else if(rp.place_value < value) {
				for(int j = TOP_SIZE-1; j > i; j--) {
					places[j] = places[j-1];
				}
				places[i] = new RatingPlace(value, nick);
				break;
			}
			else if(rp.place_value == value) {
				rp.nicknames.add(nick);
				break;
			}
		}
	}
	
	public void output_to_player(CommandSender sender, int num) {
		if(places.length == 0 || places[0] == null) return; //empty list
		
		sender.sendMessage(header);
		
		boolean small_values = false, is_top_time = this instanceof VanillaRatingTop && ((VanillaRatingTop)this).criteria == Statistic.PLAY_ONE_MINUTE;
		if(is_top_time && places[0].place_value < 20*60*60*10)
			small_values = true;
		
		for(int i = 0; i<Math.min(TOP_SIZE, num); i++) {
			if(places[i] != null) {
				String names = "";
				int length = places[i].nicknames.size();
				for(int j = 0; j < length-1; j++) {
					String name = places[i].nicknames.get(j);
					if(j > 0) {
						names += ", ";
					}
					names += name;
				}
				if(length > 1) {
					names += last_nichnames_separator;
				}
				names += places[i].nicknames.get(length-1);
				
				int int_val = places[i].place_value;
				String value = Integer.toString(int_val);
				if(is_top_time) {
					value = Integer.toString((int) (Math.round(((double)int_val)/720)/100) );  //div ticks by 20*60*60
				}
				String end;
				if(small_values) {
					if(is_top_time)
						value = Double.toString((double)Math.round(((double)int_val)/720)/100);  //div ticks by 20*60*60
					end = ends_with5_0;
				}
				else {
					end = ( (int_val+9)%10 > 3 || (int_val > 10 && int_val < 20) ) ? ends_with5_0 : (int_val%10 > 1 ? ends_with2_4 : ends_with1);
				}
				//[1] [place takes player] [FEST_Channel] [who had played] [10] [hours on server]
				if(places[i].nicknames.size() == 1)
					sender.sendMessage((i+1) + starts_with1 + names + middle1 + value + end);
				else
					sender.sendMessage((i+1) + starts_with2p + names + middle2p + value + end);
			}
			else break;
		}
	}
}
