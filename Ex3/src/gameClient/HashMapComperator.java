package gameClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HashMapComperator {
	public static <K extends Comparable, V extends Comparable> Map<Integer, Integer> getSortedMapByValues(
			final Map<Integer, Integer> map) {
		Map<Integer, Integer> mapSortedByValues = new LinkedHashMap<Integer, Integer>();

		// get all the entries from the original map and put it in a List
		List<Map.Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(map.entrySet());

		// sort the entries based on the value by custom Comparator
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {

			public int compare(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}

		});
		// put all sorted entries in LinkedHashMap
		for (Map.Entry<Integer, Integer> entry : list)
			mapSortedByValues.put(entry.getKey(), entry.getValue());

		// return Map sorted by values
		return mapSortedByValues;
	}
}
