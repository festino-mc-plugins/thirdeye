package com.te.top;

public class UtilsTop {
	public static <T> boolean contains(T[] arr, T elem) {
		for (T el : arr)
			if (el.equals(elem))
				return true;
		return false;
	}
}
