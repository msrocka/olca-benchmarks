package benchmarks;

import java.io.File;
import exlink.ProviderTable;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;
import org.openlca.core.model.ProcessType;

public class ProviderTableTest {

	public static void main(String[] args){
		try {
			String dbPath = "C:\\Users\\Besitzer\\openLCA-data-1.4\\databases\\ecoinvent_3_2_apos";
			IDatabase db = new DerbyDatabase(new File(dbPath));
			ProviderTable.create(db, ProcessType.UNIT_PROCESS);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
