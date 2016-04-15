package exlink;

import gnu.trove.map.hash.TLongByteHashMap;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NativeSql;
import org.openlca.core.model.ProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores the process types for the process IDs in a database.
 */
public class ProcessTypeTable {

	private TLongByteHashMap map = new TLongByteHashMap();

	public static ProcessTypeTable create(IDatabase db) {
		return new ProcessTypeTable(db);
	}

	private ProcessTypeTable(IDatabase db) {
		init(db);
	}

	private void init(IDatabase db) {
		try {
			String sql = "select id, process_type from tbl_processes";
			NativeSql.on(db).query(sql, r -> {
				long id = r.getLong(1);
				String type = r.getString(2);
				if (type == null)
					return true;
				ProcessType t = ProcessType.valueOf(type);
				byte b = t == ProcessType.LCI_RESULT ? (byte) 1 : (byte) 0;
				map.put(id, b);
				return true;
			});
		} catch (Exception e) {
			Logger log = LoggerFactory.getLogger(getClass());
			log.error("Failed to load process types", e);
		}
	}

	public ProcessType get(long processId) {
		byte b = map.get(processId);
		return b == 0 ? ProcessType.UNIT_PROCESS : ProcessType.LCI_RESULT;
	}
}
