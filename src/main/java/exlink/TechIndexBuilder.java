package exlink;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NativeSql;
import org.openlca.core.matrix.cache.ConversionTable;
import org.openlca.core.matrix.cache.FlowTypeTable;
import org.openlca.core.model.FlowType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TechIndexBuilder {

	private IDatabase db;
	private FlowTypeTable flowTypes;
	private ProcessTypeTable processTypes;
	private ConversionTable conversions;

	TechIndexBuilder(IDatabase db) {
		this.db = db;
		processTypes = ProcessTypeTable.create(db);
		flowTypes = FlowTypeTable.create(db);
		conversions = ConversionTable.create(db);
	}

	TechIndex build(long refExchange) throws Exception {
		Logger log = LoggerFactory.getLogger(getClass());


		String query = "select id, f_owner, f_flow, is_input, " +
				"f_flow_property_factor, f_unit, resulting_amount_value " +
				"from tbl_exchanges";

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

	private class Exchange {
		long id;
		boolean isInput;
		long flowId;
		double amount;

		Exchange(ResultSet rs) throws Exception {
			id = rs.getLong("id");
			isInput = rs.getBoolean("is_input");
			flowId = rs.getLong("f_flow");
			double f = getConversionFactor(rs);
			amount = f * rs.getDouble("resulting_amount_value");
		}

		private double getConversionFactor(ResultSet rs) throws Exception {
			long property = rs.getLong("f_flow_property_factor");
			double propertyFactor = conversions.getPropertyFactor(property);
			long unit = rs.getLong("f_unit");
			double unitFactor = conversions.getUnitFactor(unit);
			if (propertyFactor == 0)
				return 0;
			return unitFactor / propertyFactor;
		}
	}

}
