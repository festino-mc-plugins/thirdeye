package com.te;

import java.util.ArrayList;
import java.util.List;

public class RatingPlace {
	int place_value = 0;
	List<String> nicknames = new ArrayList<>();
	
	public RatingPlace(int value, String nick) {
		place_value = value;
		nicknames.add(nick);
	}
}
