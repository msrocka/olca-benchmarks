package exlink;

import gnu.trove.map.hash.TLongObjectHashMap;
import org.openlca.core.database.IDatabase;
import org.openlca.core.matrix.LongPair;
import org.openlca.core.model.ProcessType;

public class ProviderTable {

	private final TLongObjectHashMap map;

	private ProviderTable(TLongObjectHashMap map) {
		this.map = map;
	}

	public LongPair get() {
		return null;
	}

	public static Builder withPreferredType(ProcessType type) {
		return null;
	}

	public static class Builder {

		private IDatabase db;
		private ProcessType preferredType;

	}

}