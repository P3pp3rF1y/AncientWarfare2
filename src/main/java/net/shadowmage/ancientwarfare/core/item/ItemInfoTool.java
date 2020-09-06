package net.shadowmage.ancientwarfare.core.item;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.GuiInfoTool;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.ItemTools;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemInfoTool extends ItemBaseCore {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
			.registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer())
			.registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer())
			.registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer())
			.registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer())
			.create();

	public ItemInfoTool() {
		super("info_tool");
		setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(getMode(stack).getDisplayName() + " mode");
	}

	private void printSimpleMessage(EntityPlayer player, IBlockState state) {
		//noinspection ConstantConditions
		player.sendMessage(new TextComponentString("Block name: " + state.getBlock().getRegistryName().toString()));
		if (!state.getProperties().isEmpty()) {
			player.sendMessage(new TextComponentString("Properties:"));
			for (Map.Entry<IProperty<?>, Comparable<?>> prop : state.getProperties().entrySet()) {
				player.sendMessage(new TextComponentString(prop.getKey().getName() + " : " + prop.getValue().toString()));
			}
		}
	}

	private void printJSON(EntityPlayer player, IBlockState state) {
		String json = BlockTools.serializeToJson(state).toString();
		printAndCopyToClipboard(player, json);
	}

	public void printItemInfo(EntityPlayer player, ItemStack infoTool, ItemStack stack) {
		Mode mode = getMode(infoTool);
		switch (mode) {
			case INFO:
				printSimpleMessage(player, stack);
				break;
			case JSON:
				printJSON(player, stack);
				break;
			case LOOT_ENTRY:
				printLootEntryJSON(player, stack);
				break;
		}
	}

	private void printSimpleMessage(EntityPlayer player, ItemStack stack) {
		//noinspection ConstantConditions
		player.sendMessage(new TextComponentString("Item name: " + stack.getItem().getRegistryName().toString()));
		player.sendMessage(new TextComponentString("Meta: " + stack.getMetadata()));
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions
			player.sendMessage(new TextComponentString("NBT: " + stack.getTagCompound().toString()));
		}
	}

	private void printJSON(EntityPlayer player, ItemStack stack) {
		String json = ItemTools.serializeToJson(stack).toString();
		printAndCopyToClipboard(player, json);
	}

	private void printAndCopyToClipboard(EntityPlayer player, String json) {
		StringSelection stringSelection = new StringSelection(json);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		player.sendMessage(new TextComponentString(json));
		player.sendMessage(new TextComponentString("Copied to clipboard"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			return super.onItemRightClick(world, player, hand);
		}

		RayTraceResult hit = rayTrace(world, player, true);
		//noinspection ConstantConditions
		if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = world.getBlockState(hit.getBlockPos());
			Mode mode = getMode(player.getHeldItem(hand));
			switch (mode) {
				case INFO:
					printSimpleMessage(player, state);
					break;
				case JSON:
					printJSON(player, state);
					break;
				case LOOT_ENTRY:
					printLootEntryJSON(player, state, hit, world);
					break;
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		if (player.isSneaking()) {
			return new ActionResult<>(EnumActionResult.SUCCESS, cycleMode(player.getHeldItem(hand)));
		} else {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_INFO_TOOL);
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
	}

	private void printLootEntryJSON(EntityPlayer player, IBlockState state, RayTraceResult hit, World world) {
		printLootEntryJSON(player, state.getBlock().getPickBlock(state, hit, world, hit.getBlockPos(), player));
	}

	private void printLootEntryJSON(EntityPlayer player, ItemStack stack) {
		List<LootFunction> functions = new ArrayList<>();
		functions.add(new SetCount(new LootCondition[0], new RandomValueRange(1)));
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions
			functions.add(new SetNBT(new LootCondition[0], stack.getTagCompound()));
		}
		if (stack.isItemStackDamageable()) {
			functions.add(new SetDamage(new LootCondition[0], new RandomValueRange(stack.getItemDamage())));
		} else if (stack.getMetadata() > 0) {
			functions.add(new SetMetadata(new LootCondition[0], new RandomValueRange(stack.getMetadata())));
		}
		//noinspection ConstantConditions
		LootEntry lootEntry = new LootEntryItem(stack.getItem(), 1, 0, functions.toArray(new LootFunction[0]), new LootCondition[0], stack.getItem().getRegistryName().toString());
		JsonObject json = GSON.toJsonTree(lootEntry).getAsJsonObject();
		json.remove("quality");
		json.remove("entryName");
		printAndCopyToClipboard(player, GSON.toJson(json));
	}

	private ItemStack cycleMode(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		//noinspection ConstantConditions
		stack.getTagCompound().setString("mode", getMode(stack).cycle().name().toLowerCase());
		return stack;
	}

	private Mode getMode(ItemStack stack) {
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions
			return Mode.fromString(stack.getTagCompound().getString("mode"));
		}
		return Mode.INFO;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_INFO_TOOL, GuiInfoTool.class);
	}

	enum Mode {
		INFO("Info"),
		JSON("JSON"),
		LOOT_ENTRY("Loot Entry");

		private String displayName;

		Mode(String displayName) {
			this.displayName = displayName;
		}

		private static final Map<String, Mode> NAME_MODE_MAP;

		static {
			ImmutableMap.Builder<String, Mode> builder = new ImmutableMap.Builder<>();
			for (Mode mode : Mode.values()) {
				builder.put(mode.name().toLowerCase(), mode);
			}
			NAME_MODE_MAP = builder.build();
		}

		public Mode cycle() {
			switch (this) {
				case INFO:
					return JSON;
				case JSON:
					return LOOT_ENTRY;
				case LOOT_ENTRY:
					return INFO;
			}
			return INFO;
		}

		public static Mode fromString(String mode) {
			return NAME_MODE_MAP.getOrDefault(mode.toLowerCase(), INFO);
		}

		public String getDisplayName() {
			return displayName;
		}
	}
}
