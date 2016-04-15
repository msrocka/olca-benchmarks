package exlink;

import org.openlca.core.database.IDatabase;

public class TechIndex {

	public static TechIndex build(IDatabase db, long refExchange) throws Exception {
		return new TechIndexBuilder(db).build(refExchange);
	}
}
