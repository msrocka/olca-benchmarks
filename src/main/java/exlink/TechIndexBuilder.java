package exlink;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NativeSql;
import org.openlca.core.matrix.cache.FlowTypeTable;
import org.openlca.core.model.FlowType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TechIndexBuilder {

	private final IDatabase db;

	TechIndexBuilder(IDatabase db) {
		this.db = db;
	}

	TechIndex build(long refExchange) throws Exception {
		Logger log = LoggerFactory.getLogger(getClass());

		FlowTypeTable flowTypes = FlowTypeTable.create(db);
		String query = "select id, f_owner, f_flow, is_input from tbl_exchanges";

		log.trace("scan exchange table");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteBuffer buffer = ByteBuffer.allocate(1046);
		NativeSql.on(db).query(query, r -> {
			long flowId = r.getLong("f_flow");
			FlowType type = flowTypes.getType(flowId);
			if (type == FlowType.ELEMENTARY_FLOW)
				return true;


			return true;
		});
		log.trace("scan exchange table: done");
		return null;
	}

	private static class Exchange {
		long id;
		boolean isInput;
		long flowId;

		Exchange(ResultSet rs) throws Exception {
			id = rs.getLong("id");
			isInput = rs.getBoolean("is_input");
			flowId = rs.getLong("f_flow");
		}
	}

}
