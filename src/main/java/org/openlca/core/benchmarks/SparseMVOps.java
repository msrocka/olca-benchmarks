package org.openlca.core.benchmarks;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.ProductSystemDao;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.math.DataStructures;
import org.openlca.core.matrix.Inventory;
import org.openlca.core.matrix.MatrixData;
import org.openlca.core.matrix.cache.MatrixCache;
import org.openlca.core.matrix.format.DenseMatrix;
import org.openlca.core.matrix.format.HashMatrix;
import org.openlca.core.matrix.format.MatrixConverter;
import org.openlca.core.model.AllocationMethod;
import org.openlca.core.model.ProductSystem;
import org.openlca.julia.Julia;
import org.openlca.julia.JuliaSolver;

/**
 * What is faster: a matrix-vector multiplication and column scaling using a
 * sparse hash-table based matrix in plain Java or calling into an optimized
 * BLAS library with a dense array.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class SparseMVOps {

	private IDatabase db;
	private DenseMatrix denseB;
	private HashMatrix sparseB;
	private double[] s;

	@Setup
	public void setUp() {
		Julia.load();
		db = new DerbyDatabase(new File(Config.DB_PATH));
		ProductSystemDao dao = new ProductSystemDao(db);
		ProductSystem sys = dao.getForRefId(
			"decf842e-048b-49a9-8f77-53ce63bf27d2");
		Inventory inv = DataStructures.createInventory(
			sys, AllocationMethod.NONE, MatrixCache.createLazy(db));
		JuliaSolver solver = new JuliaSolver();
		MatrixData data = inv.createMatrix(solver);
		denseB = MatrixConverter.dense(data.enviMatrix);
		sparseB = MatrixConverter.hashSparse(data.enviMatrix);
		s = solver.solve(data.techMatrix, 0, 1.0);
	}

	@TearDown
	public void tearDown() throws Exception {
		db.close();
	}

	@Benchmark
	public void denseMultiplication() throws Exception {
		JuliaSolver solver = new JuliaSolver();
		DenseMatrix b = denseB.copy();
		solver.multiply(b, s);
	}

	@Benchmark
	public void denseScaling() throws Exception {
		JuliaSolver solver = new JuliaSolver();
		DenseMatrix b = denseB.copy();
		solver.scaleColumns(b, s);
	}

	@Benchmark
	public void sparseMultiplication() throws Exception {
		HashMatrix b = sparseB.copy();
		b.scaleColumns(s);
	}

	@Benchmark
	public void sparseScaling() throws Exception {
		HashMatrix b = sparseB.copy();
		b.multiply(s);
	}

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(SparseMVOps.class.getName())
				.warmupIterations(1)
				.measurementIterations(3)
				.forks(1)
				.build();
		new Runner(opt).run();
	}
}
