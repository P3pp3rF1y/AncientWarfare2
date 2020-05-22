package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.core.input.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.input.IScrollableItem;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ItemCommandBaton extends ItemBaseNPC implements IItemKeyInterface, IScrollableItem {

	private final double attackDamage;
	int range = 120;//TODO set range from config
	private final ToolMaterial material;

	public ItemCommandBaton(String name, ToolMaterial material) {
		super(name);
		attackDamage = 4 + material.getAttackDamage();
		this.material = material;
		setMaxStackSize(1);
		setMaxDamage(material.getMaxUses());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (!stack.hasTagCompound()) {
			stack.setTagInfo("mode", new NBTBuilder().setString("mode", BatonMode.getDefault().getName()).build());
		}

		String keyText, text;
		text = "RMB" + " = " + I18n.format("guistrings.npc.baton.add_remove");
		tooltip.add(text);

		keyText = InputHandler.ALT_ITEM_USE_1.getDisplayName();
		text = keyText + " = " + "Execute Command: " + I18n.format(getMode(stack).getTranslationKey());
		tooltip.add(text);

		text = "Use scroll wheel to change mode when sneaking";
		tooltip.add(text);
	}

	/*
	 * Return the enchantability factor of the item.
	 */
	@Override
	public int getItemEnchantability() {
		return material.getEnchantability();
	}

	/*
	 * Return whether this item is repairable in an anvil.
	 */
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		ItemStack mat = material.getRepairItemStack();
		if (!mat.isEmpty() && OreDictionary.itemMatches(mat, repair, false)) { return true; }
		return super.getIsRepairable(toRepair, repair);
	}

	/*
	 * Raise the damage on the stack.
	 */
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase attacked, EntityLivingBase wielder) {
		stack.damageItem(1, wielder);
		return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (state.getBlockHardness(world, pos) != 0) {
			stack.damageItem(2, entityLiving);
		}
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		if (slot != EntityEquipmentSlot.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}

		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, 0));
		multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.3D, 0));
		return multimap;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		if (player.isSneaking()) {
			//TODO openGUI
		} else {
			RayTraceResult pos = RayTraceUtils.getPlayerTarget(player, range, 0);
			if (pos != null && pos.typeOfHit == Type.ENTITY && pos.entityHit instanceof NpcBase) {
				NpcBase npc = (NpcBase) pos.entityHit;
				if (npc.hasCommandPermissions(player.getUniqueID(), player.getName())) {
					onNpcClicked(npc, stack);
				}
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		//noop ...or...??
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		if (altFunction == ItemAltFunction.ALT_FUNCTION_1) {
			switch (getMode(stack)) {
				case CLEAR_COMMAND: {
					RayTraceResult hit = new RayTraceResult(player);
					CommandType c = CommandType.CLEAR_COMMAND;
					NpcCommand.handleCommandClient(c, hit);
				}
				break;
				case ATTACK: {
					RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
					if (hit != null) {
						CommandType c = hit.typeOfHit == Type.ENTITY ? CommandType.ATTACK : CommandType.ATTACK_AREA;
						NpcCommand.handleCommandClient(c, hit);
					}
				}
				break;
				case MOVE: {
					RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
					if (hit != null) {
						CommandType c = hit.typeOfHit == Type.ENTITY ? CommandType.GUARD : CommandType.MOVE;
						NpcCommand.handleCommandClient(c, hit);
					}
				}
				break;
				case SET_HOME: {
					RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
					if (hit != null && hit.typeOfHit == Type.BLOCK) {
						CommandType c = CommandType.SET_HOME;
						NpcCommand.handleCommandClient(c, hit);
					}
				}
				break;
				case CLEAR_HOME: {
					RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
					CommandType c = CommandType.CLEAR_HOME;
					NpcCommand.handleCommandClient(c, hit);
				}
				break;
				case SET_UPKEEP: {
					RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
					if (hit != null && hit.typeOfHit == Type.BLOCK) {
						CommandType c = CommandType.SET_UPKEEP;
						NpcCommand.handleCommandClient(c, hit);
					}
				}
				break;
				case CLEAR_UPKEEP: {
					RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
					CommandType c = CommandType.CLEAR_UPKEEP;
					NpcCommand.handleCommandClient(c, hit);
				}
				break;
			}
		}
		return false;
	}

	private void onNpcClicked(NpcBase npc, ItemStack stack) {
		CommandSet.loadFromStack(stack).onNpcClicked(npc, stack);
	}

	public static List<Entity> getCommandedEntities(World world, ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof ItemCommandBaton)) {
			return new ArrayList<>();
		}
		return CommandSet.loadFromStack(stack).getEntities(world);
	}

	@Override
	public boolean onScrollUp(World world, EntityPlayer player, ItemStack stack) {
		if (!world.isRemote) {
			changeMode(getMode(stack).next(), player, stack);
		}
		return true;
	}

	@Override
	public boolean onScrollDown(World world, EntityPlayer player, ItemStack stack) {
		if (!world.isRemote) {
			changeMode(getMode(stack).previous(), player, stack);
		}
		return true;
	}

	/*
	 * relies on NPCs transmitting their unique entity-id to client-side<br>
	 *
	 * @author Shadowmage
	 */
	private static class CommandSet {

		private Set<UUID> ids = new HashSet<>();

		private CommandSet() {
		}

		static CommandSet loadFromStack(ItemStack stack) {
			CommandSet set = new CommandSet();
			//noinspection ConstantConditions
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("entityList")) {
				set.readFromNBT(stack.getTagCompound().getCompoundTag("entityList"));
			}
			return set;
		}

		private void writeToStack(ItemStack stack) {
			stack.setTagInfo("entityList", writeToNBT());
		}

		private void readFromNBT(NBTTagCompound tag) {
			NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
			NBTTagCompound idTag;
			for (int i = 0; i < entryList.tagCount(); i++) {
				idTag = entryList.getCompoundTagAt(i);
				ids.add(idTag.getUniqueId("uuid"));
			}
		}

		private NBTTagCompound writeToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList entryList = new NBTTagList();
			NBTTagCompound idTag;
			for (UUID id : ids) {
				idTag = new NBTTagCompound();
				idTag.setUniqueId("uuid", id);
				entryList.appendTag(idTag);
			}
			tag.setTag("entryList", entryList);

			return tag;
		}

		void onNpcClicked(NpcBase npc, ItemStack stack) {
			if (ids.contains(npc.getPersistentID())) {
				ids.remove(npc.getPersistentID());
			} else {
				ids.add(npc.getPersistentID());
			}
			validateEntities(npc.world);
			writeToStack(stack);
		}

		List<Entity> getEntities(World world) {
			List<Entity> in = Lists.newArrayList();
			if (world instanceof WorldServer) {
				WorldServer worldServer = (WorldServer) world;
				for (UUID id : ids) {
					Entity e = worldServer.getEntityFromUuid(id);
					if (e != null) {
						in.add(e);
					}
				}
			} else if (world instanceof WorldClient) {
				for (Entity entity : world.loadedEntityList) {
					for (UUID id : ids) {
						if (entity.getPersistentID().equals(id)) {
							in.add(entity);
						}
					}
				}
			}
			return in;
		}

		/*
		 * should be called server side to clear out any old un-findable entity references.<br>
		 * should probably only be called on-right click, as operation may be costly
		 */
		private void validateEntities(World world) {
			if (world instanceof WorldServer) {
				WorldServer worldServer = (WorldServer) world;
				Iterator<UUID> it = ids.iterator();
				UUID id;
				while (it.hasNext()) {
					id = it.next();
					if (id == null || worldServer.getEntityFromUuid(id) == null) {
						it.remove();
					}
				}
			}
		}
	}

	private void changeMode(BatonMode mode, EntityPlayer player, ItemStack stack) {
		player.sendMessage(new TextComponentTranslation(mode.getTranslationKey()));
		stack.setTagInfo("mode", new NBTTagString(mode.getName()));
	}

	private BatonMode getMode(ItemStack stack) {
		assert stack.getTagCompound() != null;
		return stack.hasTagCompound() ? BatonMode.fromName(stack.getTagCompound().getString("mode")) : BatonMode.CLEAR_COMMAND;
	}

	public enum BatonMode {
		CLEAR_COMMAND("clear", "guistrings.npc.baton.clear"),
		ATTACK("attack", "guistrings.npc.baton.attack"),
		MOVE("move", "guistrings.npc.baton.move"),
		SET_HOME("setHome", "guistrings.npc.baton.set_home"),
		CLEAR_HOME("clearHome", "guistrings.npc.baton.clear_home"),
		SET_UPKEEP("setUpkeep", "guistrings.npc.baton.set_upkeep"),
		CLEAR_UPKEEP("clearUpkeep", "guistrings.npc.baton.clear_upkeep");

		private String name;
		final String key;

		BatonMode(String name, String key) {
			this.name = name;
			this.key = key;
		}

		public String getTranslationKey() {
			return key;
		}

		public static BatonMode getDefault() {
			return CLEAR_COMMAND;
		}

		public BatonMode next() {
			int ordinal = ordinal() + 1;
			if (ordinal >= BatonMode.values().length) {
				ordinal = 0;
			}
			return BatonMode.values()[ordinal];
		}

		public BatonMode previous() {
			int ordinal = ordinal() - 1;
			if (ordinal < 0) {
				ordinal = BatonMode.values().length - 1;
			}
			return BatonMode.values()[ordinal];
		}

		private static final ImmutableMap<String, BatonMode> NAME_TO_MODE;

		static {
			ImmutableMap.Builder<String, BatonMode> builder = new ImmutableMap.Builder<>();
			for (BatonMode mode : values()) {
				builder.put(mode.name, mode);
			}
			NAME_TO_MODE = builder.build();
		}

		public static BatonMode fromName(String name) {
			return NAME_TO_MODE.getOrDefault(name, CLEAR_COMMAND);
		}

		public String getName() {
			return name;
		}
	}
}
