package benchmarks;

import java.io.File;
import exlink.TechIndex;
import org.openlca.core.database.IDatabase;
import org.openlca.core.database.derby.DerbyDatabase;

public class Main {

	public static void main(String[] args) {
		try {
			String dbPath = "C:\\Users\\Besitzer\\openLCA-data-1.4\\databases\\ecoinvent_3_2_apos";
			IDatabase db = new DerbyDatabase(new File(dbPath));
			TechIndex.build(db, 79684);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
