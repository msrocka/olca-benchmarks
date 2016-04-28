package exlink;

import org.openlca.core.model.FlowType;

public class PicoExchange {

	public long processID;
	public long flowID;
	public long exchangeID;
	public long providerID;
	public long currencyID;

	public double amount;
	public double conversionFactor;
	public double costValue;

	public boolean isInput;
	public boolean isAvoidedProduct;

	public FlowType flowType;
	public PicoUncertainty uncertainty;

	public String amountFormula;
	public String costFormula;

}
