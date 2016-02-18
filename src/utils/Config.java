package utils;

import java.util.ArrayList;

public class Config<K, V> {
	public Pair<Object, Object> firstLine;
	public ArrayList<Pair<K, V>> contents = new ArrayList<Pair<K, V>>();
}