package org.openlca.core.benchmarks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;

import org.openlca.core.matrix.LongPair;

import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TLongByteHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;

public class RegFlowIndexTest {

	private static List<Long> generateFlowIDs(int size) {
		Random rand = new Random();
		long next = rand.longs(10_000, 20_000)
				.findFirst().getAsLong();
		ArrayList<Long> longs = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			longs.add(next);
			next += rand.nextInt(4) + 1;
		}
		Collections.shuffle(longs, rand);
		return longs;
	}

	private static List<Long> generateLocationIDs(int size) {
		Random rand = new Random();
		PrimitiveIterator.OfLong stream = rand.longs(
				1000, 2000).iterator();
		ArrayList<Long> longs = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			longs.add(stream.nextLong());
		}
		return longs;
	}

}

interface Index {

	boolean contains(long flowID, long locationID);

	int putInput(long flowID, long locationID);

	int putOutput(long flowID, long locationID);

	int get(long flowID, long locationID);

	boolean isInput(long flowID, long locationID);

}

class LongPairIndex implements Index {

	private final HashMap<LongPair, Integer> index = new HashMap<>();
	private final HashMap<LongPair, Boolean> input = new HashMap<>();

	@Override
	public boolean contains(long flowID, long locationID) {
		Integer i = index.get(LongPair.of(flowID, locationID));
		return i == null ? false : true;
	}

	@Override
	public int putInput(long flowID, long locationID) {
		LongPair pair = LongPair.of(flowID, locationID);
		Integer i = index.get(LongPair.of(flowID, locationID));
		if (i != null)
			return i;
		int idx = index.size();
		index.put(pair, idx);
		input.put(pair, true);
		return idx;
	}

	@Override
	public int putOutput(long flowID, long locationID) {
		LongPair pair = LongPair.of(flowID, locationID);
		Integer i = index.get(LongPair.of(flowID, locationID));
		if (i != null)
			return i;
		int idx = index.size();
		index.put(pair, idx);
		input.put(pair, false);
		return idx;
	}

	@Override
	public int get(long flowID, long locationID) {
		Integer i = index.get(LongPair.of(flowID, locationID));
		return i == 0 ? -1 : i;
	}

	@Override
	public boolean isInput(long flowID, long locationID) {
		Boolean b = input.get(LongPair.of(flowID, locationID));
		return b == null ? false : b;
	}
}

class TLong2LevelIndex implements Index {

	private int size = 0;
	private final TLongObjectHashMap<TLongIntHashMap> index = new TLongObjectHashMap<>();
	private final TLongByteHashMap input = new TLongByteHashMap(
			Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1L,
			(byte) 0);

	@Override
	public int get(long flowID, long locationID) {
		TLongIntHashMap m = index.get(flowID);
		if (m == null)
			return -1;
		return m.get(locationID);
	}

	@Override
	public boolean contains(long flowID, long locationID) {
		int i = get(flowID, locationID);
		return i >= 0;
	}

	@Override
	public boolean isInput(long flowID, long locationID) {
		byte b = input.get(flowID);
		return b > 0;
	}

	@Override
	public int putInput(long flowID, long locationID) {
		return put(flowID, locationID, true);
	}

	@Override
	public int putOutput(long flowID, long locationID) {
		return put(flowID, locationID, false);
	}

	private int put(long flowID, long locationID, boolean isInput) {
		TLongIntHashMap m = index.get(flowID);

		if (m == null) {
			m = new TLongIntHashMap(
					Constants.DEFAULT_CAPACITY,
					Constants.DEFAULT_LOAD_FACTOR,
					-1L, // no entry key
					-1); // no entry value

		}

		int idx = m.get(locationID);
		if (idx >= 0)
			return idx;

		idx = size;
		m.put(locationID, idx);
		size++;
		input.put(flowID, isInput ? (byte) 1 : 0);
		return idx;
	}
}
