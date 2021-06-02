package com.te.top;

import java.util.ArrayList;
import java.util.List;

public class RatingPlace {
	long place_value = 0;
	List<String> nicknames = new ArrayList<>();
	
	public RatingPlace(long value, String nick) {
		place_value = value;
		nicknames.add(nick);
	}
}
