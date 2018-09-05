package net.shadowmage.ancientwarfare.structure.template.datafixes;

public interface IDataFixer extends IFixer {
	FixResult<String> fix(String ruleName, String data);
}
