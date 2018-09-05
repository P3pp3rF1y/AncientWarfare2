package net.shadowmage.ancientwarfare.structure.template.datafixes;

public interface IRuleNameFixer extends IFixer {
	FixResult<String> fix(String data);
}
