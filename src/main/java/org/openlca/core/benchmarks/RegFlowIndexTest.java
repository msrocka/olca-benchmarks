package org.openlca.core.benchmarks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openlca.core.matrix.FlowIndex2;
import org.openlca.core.matrix.LongPair;
import org.openlca.core.matrix.RegFlowIndex;
import org.openlca.core.model.descriptors.FlowDescriptor;
import org.openlca.core.model.descriptors.LocationDescriptor;

import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TLongByteHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class RegFlowIndexTest {

	private final int SIZE = 10_000;
	private List<Long> flowIDs;
	private List<Long> locationIDs;

	private List<FlowDescriptor> flows;
	private List<LocationDescriptor> locations;

	@Setup
	public void setup() {
		flowIDs = generateFlowIDs(SIZE);
		locationIDs = generateLocationIDs(SIZE);

		flows = new ArrayList<>();
		locations = new ArrayList<>();
		TLongObjectHashMap<FlowDescriptor> flowMap = new TLongObjectHashMap<>();
		TLongObjectHashMap<LocationDescriptor> locMap = new TLongObjectHashMap<>();
		for (int i = 0; i < SIZE; i++) {
			long flowID = flowIDs.get(i);
			FlowDescriptor flow = flowMap.get(flowID);
			if (flow == null) {
				flow = new FlowDescriptor();
				flow.id = flowID;
				flowMap.put(flowID, flow);
			}

			long locationID = locationIDs.get(i);
			LocationDescriptor loc = locMap.get(locationID);
			if (loc == null) {
				loc = new LocationDescriptor();
				loc.id = locationID;
				locMap.put(locationID, loc);
			}

			flows.add(flow);
			locations.add(loc);
		}
	}

	@Benchmark
	public void testLongPairIndex() {
		doTests(new LongPairIndex());
	}

	@Benchmark
	public void testTLong2LevelIndex() {
		doTests(new TLong2LevelIndex());
	}

	@Benchmark
	public void testJ2LevelIndex() {
		doTests(new J2LevelIndex());
	}

	@Benchmark
	public void testUnregTLongIndex() {
		doTests(new UnregTLongIndex());
	}

	@Benchmark
	public void testUnregJIndex() {
		doTests(new UnregJIndex());
	}

	private void doTests(Index index) {
		for (int i = 0; i < SIZE; i++) {
			boolean b = true;
			for (int j = 0; j < SIZE; j++) {
				long flowID = flowIDs.get(j);
				long locationID = locationIDs.get(j);
				b = !b;
				if (b) {
					index.putInput(flowID, locationID);
				} else {
					index.putOutput(flowID, locationID);
				}
				index.isInput(flowID, locationID);
				index.contains(flowID, locationID);
				index.get(flowID, locationID);
			}
		}
	}

	@Benchmark
	public void testRegionalizedFlowIndex() {
		RegFlowIndex index = new RegFlowIndex();
		for (int i = 0; i < SIZE; i++) {
			boolean b = true;
			for (int j = 0; j < SIZE; j++) {
				FlowDescriptor flow = flows.get(j);
				LocationDescriptor loc = locations.get(j);
				b = !b;
				if (b) {
					index.putInput(flow, loc);
				} else {
					index.putOutput(flow, loc);
				}
				index.isInput(flow);
				index.contains(flow, loc);
				index.of(flow, loc);
			}
		}
	}

	@Benchmark
	public void testFlowIndex2() {
		FlowIndex2 index = FlowIndex2.createRegionalized();
		for (int i = 0; i < SIZE; i++) {
			boolean b = true;
			for (int j = 0; j < SIZE; j++) {
				FlowDescriptor flow = flows.get(j);
				LocationDescriptor loc = locations.get(j);
				b = !b;
				int idx;
				if (b) {
					idx = index.putInput(flow, loc);
				} else {
					idx = index.putOutput(flow, loc);
				}
				index.at(idx);
				index.contains(flow, loc);
				index.of(flow, loc);
			}
		}
	}

	private List<Long> generateFlowIDs(int size) {
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

	private List<Long> generateLocationIDs(int size) {
		Random rand = new Random();
		PrimitiveIterator.OfLong stream = rand.longs(
				1000, 2000).iterator();
		ArrayList<Long> longs = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			longs.add(stream.nextLong());
		}
		return longs;
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(RegFlowIndexTest.class.getName())
				.warmupIterations(2)
				.measurementIterations(5)
				.forks(1)
				.build();
		new Runner(opt).run();
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
			index.put(flowID, m);
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

class J2LevelIndex implements Index {

	private int size = 0;
	private final HashMap<Long, HashMap<Long, Integer>> index = new HashMap<>();
	private final HashMap<Long, Boolean> input = new HashMap<>();

	@Override
	public int get(long flowID, long locationID) {
		HashMap<Long, Integer> m = index.get(flowID);
		if (m == null)
			return -1;
		Integer i = m.get(locationID);
		return i == null ? -1 : i;
	}

	@Override
	public boolean contains(long flowID, long locationID) {
		int i = get(flowID, locationID);
		return i >= 0;
	}

	@Override
	public boolean isInput(long flowID, long locationID) {
		Boolean b = input.get(flowID);
		return b == null ? false : b;
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
		HashMap<Long, Integer> m = index.get(flowID);

		if (m == null) {
			m = new HashMap<>();
			index.put(flowID, m);
		}

		Integer i = m.get(locationID);
		if (i != null)
			return i;

		int idx = size;
		m.put(locationID, idx);
		size++;
		input.put(flowID, isInput);
		return idx;
	}
}

class UnregTLongIndex implements Index {

	private final TLongIntHashMap index = new TLongIntHashMap(
			Constants.DEFAULT_CAPACITY,
			Constants.DEFAULT_LOAD_FACTOR,
			-1L, // no entry key
			-1); // no entry value

	private final TLongByteHashMap input = new TLongByteHashMap(
			Constants.DEFAULT_CAPACITY,
			Constants.DEFAULT_LOAD_FACTOR,
			-1L,
			(byte) 0);

	@Override
	public int get(long flowID, long locationID) {
		return index.get(flowID);
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
		return put(flowID, true);
	}

	@Override
	public int putOutput(long flowID, long locationID) {
		return put(flowID, false);
	}

	private int put(long flowID, boolean isInput) {
		int idx = index.get(flowID);
		if (idx >= 0)
			return idx;
		idx = index.size();
		index.put(flowID, idx);
		input.put(flowID, isInput ? (byte) 1 : 0);
		return idx;
	}

}

class UnregJIndex implements Index {

	private final HashMap<Long, Integer> index = new HashMap<>();
	private final HashMap<Long, Boolean> input = new HashMap<>();

	@Override
	public int get(long flowID, long locationID) {
		Integer i = index.get(flowID);
		return i == null ? -1 : i;
	}

	@Override
	public boolean contains(long flowID, long locationID) {
		int i = get(flowID, locationID);
		return i >= 0;
	}

	@Override
	public boolean isInput(long flowID, long locationID) {
		Boolean b = input.get(flowID);
		return b == null ? false : b;
	}

	@Override
	public int putInput(long flowID, long locationID) {
		return put(flowID, true);
	}

	@Override
	public int putOutput(long flowID, long locationID) {
		return put(flowID, false);
	}

	private int put(long flowID, boolean isInput) {
		Integer i = index.get(flowID);
		if (i != null)
			return i;
		int idx = index.size();
		index.put(flowID, idx);
		input.put(flowID, isInput);
		return idx;
	}

}
