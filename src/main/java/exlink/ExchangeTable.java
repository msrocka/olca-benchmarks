package exlink;

import java.sql.ResultSet;
import java.util.function.Consumer;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.NativeSql;
import org.openlca.core.matrix.cache.ConversionTable;
import org.openlca.core.matrix.cache.FlowTypeTable;

public class ExchangeTable {
    
    /** 
     * Iterates over each exchange in the exchanges table. This function is used to
     * get the providers of product outputs and waste inputs (treatments) from the
     * database.     
     */
    public static void fullScan(IDatabase db, Consumer<PicoExchange> fn) {
        String sql = "SELECT id, f_owner, f_flow, f_default_provider, f_currency "
                + "f_flow_property_factor, f_unit, resulting_amount_value, cost_value, "
                + "resulting_amount_formula, cost_formula, is_input, avoided_product "
                + "from tbl_exchanges";
        ConversionTable conversions = ConversionTable.create(db);
        FlowTypeTable flowTypes = FlowTypeTable.create(db);
        try {
            NativeSql.on(db).query(sql, r -> {
	            try {
		            PicoExchange e = read(r, conversions, flowTypes, false);
		            fn.accept(e);
		            return true;
	            } catch (Exception e) {
		         	throw new RuntimeException("failed to read exchange", e);
	            }
            });
        } catch (Exception e) {
            throw new RuntimeException("failed to scan exchange table", e);
        }
    }
    
    
    private static PicoExchange read(
        
        ResultSet r,
        ConversionTable conversions,
        FlowTypeTable flowTypes,
        boolean withUncertainty
        
        ) throws Exception {
            
        PicoExchange e = new PicoExchange();
        
        e.exchangeID = r.getLong(1);
        e.processID = r.getLong(2);
        e.flowID = r.getLong(3);
        e.flowType = flowTypes.getType(e.flowID);
        e.providerID = r.getLong(4);
        e.currencyID = r.getLong(5);
        
        long propertyID = r.getLong(6);
        long unitID = r.getLong(7);
        e.conversionFactor = conversions.getUnitFactor(unitID) 
                / conversions.getPropertyFactor(propertyID);
        e.amount = r.getDouble(8);
        e.costValue = r.getDouble(9);
        
        e.amountFormula = r.getString(10);
        e.costFormula = r.getString(11);
        e.isInput = r.getBoolean(12);
        e.isAvoidedProduct = r.getBoolean(13);
        
        if (withUncertainty) {
            // TODO: get uncertainty information   
        }
        
        return e;       
    }
}
