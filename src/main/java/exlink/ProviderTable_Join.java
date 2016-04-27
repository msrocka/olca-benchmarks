package exlink;

import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NativeSql;
import org.openlca.core.model.ProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Provider table with a join of the flow types in the
    query. */
public class ProviderTable_Join {

	private final TLongLongHashMap map;

	private ProviderTable_Join(TLongLongHashMap map) {
		this.map = map;
	}

	public static ProviderTable_Join create(IDatabase db, ProcessType preferredType) {
		return new Builder(db, preferredType).build();
	}

	private static class Builder {

		IDatabase db;
		ProcessType preferredType;
		TLongDoubleHashMap amounts;
		ProcessTypeTable processTypes;

		private TLongLongHashMap map = new TLongLongHashMap();

		Builder(IDatabase db, ProcessType preferedType) {
			this.db = db;
			this.preferredType = preferedType;
			amounts = new TLongDoubleHashMap();
			processTypes = ProcessTypeTable.create(db);
		}

		ProviderTable_Join build() {
			String query = "SELECT e.f_owner, e.f_flow, e.resulting_amount_value"
					+ " FROM tbl_exchanges e INNER JOIN tbl_flows f ON e.f_flow = f.id"
					+ " WHERE  (f.flow_type = 'PRODUCT_FLOW' AND e.is_input = 0)"
					+ " OR (f.flow_type = 'WASTE_FLOW' AND e.is_input = 1)";
			Logger log = LoggerFactory.getLogger(getClass());
			try {
				log.trace("load provider table");
				tryBuild(query);
				log.trace("provider table loaded");
			} catch (Exception e) {
				log.error("failed to load provider table", e);
			}
			return new ProviderTable_Join(map);
		}

		private void tryBuild(String query) throws Exception {
			NativeSql.on(db).query(query, r -> {
				try {
					long flowId = r.getLong("f_flow");
					long processId = r.getLong("f_owner");
					double amount = r.getDouble("resulting_amount_value");
					if (better(flowId, processId, amount)) {
						map.put(flowId, processId);
						amounts.put(flowId, amount);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return true;
			});
		}

		private boolean better(long flowId, long processId, double amount) {
			long oldProcessId = map.get(flowId);
			if (oldProcessId == 0)
				return true;
			ProcessType newType = processTypes.get(processId);
			ProcessType oldType = processTypes.get(oldProcessId);
			if (newType != oldType)
				return newType == preferredType;
			double oldAmount = amounts.get(flowId);
			return amount > oldAmount;
		}
	}
}
