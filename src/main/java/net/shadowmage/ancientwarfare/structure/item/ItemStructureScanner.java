package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.template.save.TemplateExporter;
import net.shadowmage.ancientwarfare.structure.template.scan.TemplateScanner;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.awt.Color.GREEN;

public class ItemStructureScanner extends ItemBaseStructure implements IItemKeyInterface, IBoxRenderer {
	private static final String VALIDATOR_TAG = "validator";
	private static final String STRUCTURE_NAME_TAG = "structureName";
	private static final String INCLUDE_TAG = "include";
	private static final String MOD_DEPENDENCIES_TAG = "mods";

	public ItemStructureScanner(String name) {
		super(name);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (!stack.isEmpty()) {
			ItemStructureSettings viewSettings = ItemStructureSettings.getSettingsFor(stack);
			String key = InputHandler.ALT_ITEM_USE_1.getDisplayName();
			if (!viewSettings.hasPos1()) {
				tooltip.add(I18n.format("guistrings.structure.scanner.select_first_pos", key));
				tooltip.add("(1/4)");
			} else if (!viewSettings.hasPos2()) {
				tooltip.add(I18n.format("guistrings.structure.scanner.select_second_pos", key));
				tooltip.add("(2/4)");
			} else if (!viewSettings.hasBuildKey()) {
				tooltip.add(I18n.format("guistrings.structure.scanner.select_offset", key));
				tooltip.add("(3/4)");
			} else {
				tooltip.add(key + " : " + I18n.format("guistrings.structure.scanner.click_to_process"));
				tooltip.add("(4/4)");
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		ItemStructureSettings scanSettings = ItemStructureSettings.getSettingsFor(stack);
		if (player.isSneaking()) {
			scanSettings.clearSettings();
			ItemStructureSettings.setSettingsFor(stack, scanSettings);
		} else if (readyToExport(scanSettings)) {
			BlockPos key = scanSettings.key;
			if (player.getDistance(key.getX() + 0.5d, key.getY(), key.getZ() + 0.5d) > 10) {
				player.sendMessage(new TextComponentTranslation("guistrings.structure.scanner.too_far"));
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
			player.sendMessage(new TextComponentTranslation("guistrings.structure.scanner.exporting"));
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SCANNER, 0, 0, 0);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	public static boolean readyToExport(ItemStack scanner) {
		return readyToExport(ItemStructureSettings.getSettingsFor(scanner));
	}

	private static boolean readyToExport(ItemStructureSettings scanSettings) {
		return scanSettings.hasPos1() && scanSettings.hasPos2() && scanSettings.hasBuildKey();
	}

	public static void setStructureName(ItemStack stack, String name) {
		stack.setTagInfo(STRUCTURE_NAME_TAG, new NBTTagString(name));
	}

	public static String getStructureName(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() ? stack.getTagCompound().getString(STRUCTURE_NAME_TAG) : "";
	}

	public static void setValidator(ItemStack stack, StructureValidator validator) {
		stack.setTagInfo(VALIDATOR_TAG, validator.serializeToNBT());
	}

	public static StructureValidator getValidator(ItemStack stack) {
		//noinspection ConstantConditions
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(VALIDATOR_TAG)) {
			return StructureValidationType.GROUND.getValidator();
		}

		NBTTagCompound validatorTag = stack.getTagCompound().getCompoundTag(VALIDATOR_TAG);

		StructureValidationType type = StructureValidationType.getTypeFromName(validatorTag.getString("validationType"));
		if (type == null) {
			return StructureValidationType.GROUND.getValidator();
		}

		StructureValidator validator = type.getValidator();
		validator.readFromNBT(validatorTag);
		return validator;
	}

	public static void setIncludeImmediately(ItemStack stack, boolean include) {
		stack.setTagInfo(INCLUDE_TAG, new NBTTagByte((byte) (include ? 1 : 0)));
	}

	public static boolean getIncludeImmediately(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(INCLUDE_TAG);
	}

	public static boolean scanStructure(World world, ItemStack scanner) {
		ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(scanner);

		int turns = (6 - settings.buildFace.getHorizontalIndex()) % 4;
		StructureTemplate template = TemplateScanner.scan(world, getModDependencies(scanner), settings.getMin(), settings.getMax(), settings.key, turns, getStructureName(scanner));

		StructureValidator validator = getValidator(scanner);
		template.setValidationSettings(validator);
		boolean include = getIncludeImmediately(scanner);
		if (include) {
			StructureTemplateManager.INSTANCE.addTemplate(template);
		}
		return TemplateExporter.exportTo(template, new File(include ? TemplateLoader.INCLUDE_DIRECTORY : TemplateLoader.OUTPUT_DIRECTORY));
	}

	public static Set<String> getModDependencies(ItemStack scanner) {
		//noinspection ConstantConditions
		return scanner.hasTagCompound() ? NBTHelper.getStringSet(scanner.getTagCompound().getTagList(MOD_DEPENDENCIES_TAG, Constants.NBT.TAG_STRING)) : Collections.emptySet();
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return altFunction == ItemAltFunction.ALT_FUNCTION_1;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
		if (hit == null) {
			return;
		}
		ItemStructureSettings scanSettings = ItemStructureSettings.getSettingsFor(stack);
		if (!scanSettings.hasPos1()) {
			scanSettings.setPos1(hit);
			player.sendMessage(new TextComponentTranslation("guistrings.structure.scanner.set_first_pos"));
		} else if (!scanSettings.hasPos2()) {
			scanSettings.setPos2(hit);
			player.sendMessage(new TextComponentTranslation("guistrings.structure.scanner.set_second_pos"));
		} else if (!scanSettings.hasBuildKey()) {
			scanSettings.setBuildKey(hit, player.getHorizontalFacing());
			player.sendMessage(new TextComponentTranslation("guistrings.structure.scanner.set_offset_pos"));
		} else {
			player.sendMessage(new TextComponentTranslation("guistrings.structure.scanner.click_to_process"));
		}
		ItemStructureSettings.setSettingsFor(stack, scanSettings);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBox(EntityPlayer player, ItemStack stack, float delta) {
		ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
		BlockPos firstCorner;
		BlockPos secondCorner;
		BlockPos min;
		BlockPos max;

		if (settings.hasPos1()) {
			firstCorner = settings.getPos1();
		} else {
			firstCorner = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
		}
		if (settings.hasPos2()) {
			secondCorner = settings.getPos2();
		} else {
			secondCorner = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
		}
		if (firstCorner == null || secondCorner == null) {
			return;
		}
		min = BlockTools.getMin(firstCorner, secondCorner);
		max = BlockTools.getMax(firstCorner, secondCorner);
		Util.renderBoundingBox(player, min, max, delta);

		if (settings.hasPos2()) {
			BlockPos buildKey = settings.buildKey();
			if (!settings.hasBuildKey()) {
				buildKey = BlockTools.getBlockClickedOn(player, player.world, player.isSneaking());
			}

			if (buildKey != null) {
				Util.renderBoundingBox(player, buildKey, buildKey, delta, GREEN);
				BlockPos outerFirst = new BlockPos(min.getX() - 1, buildKey.getY(), min.getZ() - 1);
				BlockPos outerSecond = new BlockPos(max.getX() + 1, buildKey.getY(), max.getZ() + 1);
				Util.renderBoundingBoxTopSide(player, outerFirst, outerSecond, delta, new Color(GREEN.getRed(), GREEN.getGreen(), GREEN.getBlue(), 80));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_SCANNER, GuiStructureScanner.class);
	}

	@SuppressWarnings("ConstantConditions")
	public static void clearSettings(ItemStack scanner) {
		ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(scanner);
		settings.clearSettings();
		ItemStructureSettings.setSettingsFor(scanner, settings);
		scanner.getTagCompound().removeTag(STRUCTURE_NAME_TAG);
		scanner.getTagCompound().removeTag(MOD_DEPENDENCIES_TAG);
		scanner.getTagCompound().removeTag((INCLUDE_TAG));
		scanner.removeSubCompound(VALIDATOR_TAG);
	}

	public static void setModDependencies(ItemStack scanner, Set<String> mods) {
		scanner.setTagInfo(MOD_DEPENDENCIES_TAG, NBTHelper.getNBTStringList(mods));
	}
}
