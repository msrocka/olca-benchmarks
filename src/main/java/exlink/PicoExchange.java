package exlink;

import org.openlca.core.model.FlowType;

public class PicoExchange {

	public long exchangeID;
	public long processID;
	public long flowID;
	public long providerID;
	public long currencyID;

	public double amount;
	public double conversionFactor;
	public double costValue;

	public FlowType flowType;
	public PicoUncertainty uncertainty;

	public String amountFormula;
	public String costFormula;
	
	public boolean isInput;
	public boolean isAvoidedProduct;

}
