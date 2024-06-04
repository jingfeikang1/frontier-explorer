package org.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class Util {
	public static <T> ArrayList<ArrayList<T>> to2DArray(ArrayList<T> array, int length) {
		ArrayList<ArrayList<T>> result = new ArrayList<>();

		// Check if the length is valid
		if (length <= 0) {
			System.out.println("Invalid length");
			return result;
		}

		// Calculate the number of rows needed
		int numRows = (int) Math.ceil((double) array.size() / length);

		// Build the 2D ArrayList
		int index = 0;
		for (int i = 0; i < numRows; i++) {
			ArrayList<T> row = new ArrayList<>();
			for (int j = 0; j < length && index < array.size(); j++) {
				row.add(array.get(index));
				index++;
			}
			result.add(row);
		}
		return result;
	}

	public static <T, U> ArrayList<T> removeDuplicates(ArrayList<T> array, Function<T, U> propertyExtractor) {
		Set<U> seen = new HashSet<U>();
		ArrayList<T> resultList = new ArrayList<>();

		for (T item : array) {
			U propertyValue = propertyExtractor.apply(item);
			if (seen.add(propertyValue)) {
				resultList.add(item);
			}
		}

		return resultList;
	}
	
	public static <T> int count(ArrayList<T> list, Function<T, Boolean> predicate) {
		int count = 0;
		for (T obj : list) {
            if (predicate.apply(obj)) {
                count++;
            }
        }
		return count;
	}

	public static float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	public static double random(float min, float max) {
		return min + Math.random() * (max - min);
	}
	public static int random(int min, float max) {
		return (int)(min + Math.random() * (max - min));
	}
}
