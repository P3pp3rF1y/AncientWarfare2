package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

import java.util.*;

public class ItemCommandBaton extends Item implements IItemKeyInterface {

    private final double attackDamage;
    int range = 120;//TODO set range from config;
    private final ToolMaterial material;

    public ItemCommandBaton(String name, ToolMaterial material) {
        this.setUnlocalizedName(name);
        this.setCreativeTab(AWNpcItemLoader.npcTab);
        this.setTextureName("ancientwarfare:npc/" + name);
        this.attackDamage = 4 + material.getDamageVsEntity();
        this.material = material;
        this.setMaxStackSize(1);
        this.setMaxDamage(material.getMaxUses());
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        String keyText, text;
        text = "RMB" + " = " + StatCollector.translateToLocal("guistrings.npc.baton.add_remove");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.npc.baton.clear");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_1);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.npc.baton.attack");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_2);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.npc.baton.move");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_3);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.npc.baton.home");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_4);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.npc.baton.upkeep");
        list.add(text);
    }

    /**
     * Return the enchantability factor of the item.
     */
    @Override
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return this.material.func_150995_f() == par2ItemStack.getItem();
    }

    /**
     * Raise the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase attacked, EntityLivingBase wielder) {
        stack.damageItem(1, wielder);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase wielder) {
        if (block.getBlockHardness(world, x, y, z) != 0) {
            stack.damageItem(2, wielder);
        }
        return true;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap multimap = super.getAttributeModifiers(stack);
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", this.attackDamage, 0));
        return multimap;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(world.isRemote){
            return stack;
        }
        if (player.isSneaking()) {
            //TODO openGUI
        } else {
            MovingObjectPosition pos = RayTraceUtils.getPlayerTarget(player, range, 0);
            if (pos != null && pos.typeOfHit == MovingObjectType.ENTITY && pos.entityHit instanceof NpcBase) {
                NpcBase npc = (NpcBase) pos.entityHit;
                if (npc.canBeCommandedBy(player.getCommandSenderName())) {
                    onNpcClicked(player, npc, stack);
                }
            }
        }
        return stack;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        //noop ...or...??
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        switch (key) {
            case KEY_0: {
                MovingObjectPosition hit = new MovingObjectPosition(player);
                NpcCommand.handleCommandClient(CommandType.CLEAR_COMMAND, hit);
            }
            break;
            case KEY_1://attack
            {
                MovingObjectPosition hit = RayTraceUtils.getPlayerTarget(player, range, 0);
                if (hit != null) {
                    CommandType c = hit.typeOfHit == MovingObjectType.ENTITY ? CommandType.ATTACK : CommandType.ATTACK_AREA;
                    NpcCommand.handleCommandClient(c, hit);
                }
            }
            break;
            case KEY_2://move/guard
            {
                MovingObjectPosition hit = RayTraceUtils.getPlayerTarget(player, range, 0);
                if (hit != null) {
                    CommandType c = hit.typeOfHit == MovingObjectType.ENTITY ? CommandType.GUARD : CommandType.MOVE;
                    NpcCommand.handleCommandClient(c, hit);
                }
            }
            break;
            case KEY_3: {
                MovingObjectPosition hit = RayTraceUtils.getPlayerTarget(player, range, 0);
                if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
                    CommandType c = player.isSneaking() ? CommandType.CLEAR_HOME : CommandType.SET_HOME;
                    NpcCommand.handleCommandClient(c, hit);
                }
            }
            break;
            case KEY_4: {
                MovingObjectPosition hit = RayTraceUtils.getPlayerTarget(player, range, 0);
                if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
                    CommandType c = player.isSneaking() ? CommandType.CLEAR_UPKEEP : CommandType.SET_UPKEEP;
                    NpcCommand.handleCommandClient(c, hit);
                }
            }
            break;
        }
        return false;
    }

    private void onNpcClicked(EntityPlayer player, NpcBase npc, ItemStack stack) {
        if (player == null || npc == null || stack == null || stack.getItem() != this) {
            return;
        }
        CommandSet.loadFromStack(stack).onNpcClicked(npc, stack);
    }

    public static List<Entity> getCommandedEntities(World world, ItemStack stack) {
        if (world == null || stack == null || !(stack.getItem() instanceof ItemCommandBaton)) {
            return new ArrayList<Entity>();
        }
        return CommandSet.loadFromStack(stack).getEntities(world);
    }

    /**
     * relies on NPCs transmitting their unique entity-id to client-side<br>
     *
     * @author Shadowmage
     */
    private static class CommandSet {
        private Set<UUID> ids = new HashSet<UUID>();

        private CommandSet() {
        }

        public static CommandSet loadFromStack(ItemStack stack) {
            CommandSet set = new CommandSet();
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
                ids.add(new UUID(idTag.getLong("idmsb"), idTag.getLong("idlsb")));
            }
        }

        private NBTTagCompound writeToNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList entryList = new NBTTagList();
            NBTTagCompound idTag;
            for (UUID id : ids) {
                idTag = new NBTTagCompound();
                idTag.setLong("idmsb", id.getMostSignificantBits());
                idTag.setLong("idlsb", id.getLeastSignificantBits());
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
            validateEntities(npc.worldObj);
            writeToStack(stack);
        }

        public List<Entity> getEntities(World world) {
            List<Entity> in = new ArrayList<Entity>();
            Entity e;
            for (UUID id : ids) {
                e = WorldTools.getEntityByUUID(world, id);
                if (e != null) {
                    in.add(e);
                }
            }
            return in;
        }

        /**
         * should be called server side to clear out any old un-findable entity references.<br>
         * should probably only be called on-right click, as operation may be costly
         */
        private void validateEntities(World world) {
            Iterator<UUID> it = ids.iterator();
            UUID id;
            while (it.hasNext()) {
                id = it.next();
                if (id == null || WorldTools.getEntityByUUID(world, id) == null) {
                    it.remove();
                }
            }
        }

    }

}
