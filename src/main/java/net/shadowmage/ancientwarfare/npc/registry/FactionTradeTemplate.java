package net.shadowmage.ancientwarfare.npc.registry;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.trade.FactionTrade;

import java.util.List;

public class FactionTradeTemplate {
	private final List<ItemStack> input;
	private final List<ItemStack> output;
	private final int refillFrequency;
	private final int maxTrades;

	public FactionTradeTemplate(List<ItemStack> input, List<ItemStack> output, int refillFrequency, int maxTrades) {
		this.input = input;
		this.output = output;
		this.refillFrequency = refillFrequency;
		this.maxTrades = maxTrades;
	}

	public FactionTrade toTrade() {
		FactionTrade trade = new FactionTrade();
		trade.setMaxAvailable(maxTrades);
		trade.setRefillFrequency(refillFrequency);
		int slot = 0;
		for (ItemStack stack : input) {
			trade.setInputStack(slot, stack);
		}
		slot = 0;
		for (ItemStack stack : output) {
			trade.setOutputStack(slot, stack);
		}
		return trade;
	}
}
