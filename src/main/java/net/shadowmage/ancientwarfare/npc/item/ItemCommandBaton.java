package net.shadowmage.ancientwarfare.npc.item;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
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

public class ItemCommandBaton extends ItemBaseNPC implements IItemKeyInterface {

    private final double attackDamage;
    int range = 120;//TODO set range from config;
    private final ToolMaterial material;

    public ItemCommandBaton(String name, ToolMaterial material) {
        super(name);
        this.attackDamage = 4 + material.getAttackDamage();
        this.material = material;
        this.setMaxStackSize(1);
        this.setMaxDamage(material.getMaxUses());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String keyText, text;
        text = "RMB" + " = " + I18n.format("guistrings.npc.baton.add_remove");
        tooltip.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
        text = keyText + " = " + I18n.format("guistrings.npc.baton.clear");
        tooltip.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_1);
        text = keyText + " = " + I18n.format("guistrings.npc.baton.attack");
        tooltip.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_2);
        text = keyText + " = " + I18n.format("guistrings.npc.baton.move");
        tooltip.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_3);
        text = keyText + " = " + I18n.format("guistrings.npc.baton.home");
        tooltip.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_4);
        text = keyText + " = " + I18n.format("guistrings.npc.baton.upkeep");
        tooltip.add(text);
    }

    /*
     * Return the enchantability factor of the item.
     */
    @Override
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    /*
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = this.material.getRepairItemStack();
        if (!mat.isEmpty() && OreDictionary.itemMatches(mat,repair,false)) return true;
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

        Multimap multimap = super.getAttributeModifiers(slot, stack);
        multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, 0));
        return multimap;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(world.isRemote){
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if (player.isSneaking()) {
            //TODO openGUI
        } else {
            RayTraceResult pos = RayTraceUtils.getPlayerTarget(player, range, 0);
            if (pos != null && pos.typeOfHit == Type.ENTITY && pos.entityHit instanceof NpcBase) {
                NpcBase npc = (NpcBase) pos.entityHit;
                if (npc.hasCommandPermissions(player.getName())) {
                    onNpcClicked(player, npc, stack);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        //noop ...or...??
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        switch (key) {
            case KEY_0: {
                RayTraceResult hit = new RayTraceResult(player);
                NpcCommand.handleCommandClient(CommandType.CLEAR_COMMAND, hit);
            }
            break;
            case KEY_1://attack
            {
                RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
                if (hit != null) {
                    CommandType c = hit.typeOfHit == Type.ENTITY ? CommandType.ATTACK : CommandType.ATTACK_AREA;
                    NpcCommand.handleCommandClient(c, hit);
                }
            }
            break;
            case KEY_2://move/guard
            {
                RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
                if (hit != null) {
                    CommandType c = hit.typeOfHit == Type.ENTITY ? CommandType.GUARD : CommandType.MOVE;
                    NpcCommand.handleCommandClient(c, hit);
                }
            }
            break;
            case KEY_3: {
                RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
                if (hit != null && hit.typeOfHit == Type.BLOCK) {
                    CommandType c = player.isSneaking() ? CommandType.CLEAR_HOME : CommandType.SET_HOME;
                    NpcCommand.handleCommandClient(c, hit);
                }
            }
            break;
            case KEY_4: {
                RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, range, 0);
                if (hit != null && hit.typeOfHit == Type.BLOCK) {
                    CommandType c = player.isSneaking() ? CommandType.CLEAR_UPKEEP : CommandType.SET_UPKEEP;
                    NpcCommand.handleCommandClient(c, hit);
                }
            }
            break;
        }
        return false;
    }

    private void onNpcClicked(EntityPlayer player, NpcBase npc, ItemStack stack) {
        if (player == null || npc == null || stack.isEmpty() || stack.getItem() != this) {
            return;
        }
        CommandSet.loadFromStack(stack, false).onNpcClicked(npc, stack);
    }

    public static List<Entity> getCommandedEntities(World world, ItemStack stack) {
        if (world == null || stack.isEmpty() || !(stack.getItem() instanceof ItemCommandBaton)) {
            return new ArrayList<>();
        }
        return CommandSet.loadFromStack(stack, world.isRemote).getEntities(world);
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

        public static CommandSet loadFromStack(ItemStack stack, boolean client) {
            CommandSet set = new CommandSet();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("entityList")) {
                set.readFromNBT(stack.getTagCompound().getCompoundTag("entityList"), client);
            }
            return set;
        }

        private void writeToStack(ItemStack stack) {
            stack.setTagInfo("entityList", writeToNBT());
        }

        private void readFromNBT(NBTTagCompound tag, boolean client) {
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

        public void onNpcClicked(NpcBase npc, ItemStack stack) {
            if (ids.contains(npc.getPersistentID())) {
                ids.remove(npc.getPersistentID());
            } else {
                ids.add(npc.getPersistentID());
            }
            validateEntities(npc.world);
            writeToStack(stack);
        }

        public List<Entity> getEntities(World world) {
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
                for(Entity entity: world.loadedEntityList) {
                    for(UUID id : ids) {
                        if(entity.getPersistentID().equals(id)) {
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

}
