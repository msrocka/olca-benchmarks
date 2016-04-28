package exlink;

import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import org.openlca.core.database.IDatabase;
import org.openlca.core.matrix.LongPair;
import org.openlca.core.model.FlowType;
import org.openlca.core.model.ProcessType;

public class ProviderTable {

	private final TLongObjectHashMap<LongPair> map;

	private ProviderTable(TLongObjectHashMap<LongPair> map) {
		this.map = map;
	}

	public static ProviderTable create(IDatabase db, ProcessType preferredType) {
		return new Builder(db, preferredType).build();
	}

	/**
	 * Returns a pair of processID and exchangeID of a product output or waste
	 * input for the given flow. It returns null if no provider is found.
	 */
	public LongPair get(long flowID) {
		return map.get(flowID);
	}

	private static class Builder {

		IDatabase db;
		ProcessType preferredType;
		TLongDoubleHashMap amounts;
		ProcessTypeTable processTypes;

		private TLongObjectHashMap<LongPair> map = new TLongObjectHashMap<>();

		Builder(IDatabase db, ProcessType preferedType) {
			this.db = db;
			this.preferredType = preferedType;
			amounts = new TLongDoubleHashMap();
			processTypes = ProcessTypeTable.create(db);
		}

		ProviderTable build() {
			ExchangeTable.fullScan(db, e -> {
				if ((!e.isInput && e.flowType == FlowType.PRODUCT_FLOW)
						|| (e.isInput && e.flowType == FlowType.WASTE_FLOW)) {
					if (better(e)) {
						map.put(e.flowID, LongPair.of(e.processID, e.exchangeID));
						amounts.put(e.flowID, e.amount);
					}
				}
			});
			return new ProviderTable(map);
		}

		private boolean better(PicoExchange e) {
			LongPair old = map.get(e.flowID);
			if (old == null)
				return true;
			ProcessType newType = processTypes.get(e.processID);
			ProcessType oldType = processTypes.get(old.getFirst());
			if (newType != oldType)
				return newType == preferredType;
			double oldAmount = amounts.get(e.flowID);
			return e.amount > oldAmount;
		}
	}
}