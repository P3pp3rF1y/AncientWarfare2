package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSoundTypes;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCoinStackCopper extends BlockBaseStructure {
    public BlockCoinStackCopper() {
        super(Material.GROUND, "coin_stack_copper");
        setHardness(0.3F);
        setHarvestLevel("hand", 0);
        setResistance(0.4F);
    }

    private static final AxisAlignedBB AABB_0 = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.10D, 1D);
    private static final AxisAlignedBB AABB_1 = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.25D, 1D);
    private static final AxisAlignedBB AABB_2 = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.37D, 1D);
    private static final AxisAlignedBB AABB_3 = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.57D, 1D);
    private static final AxisAlignedBB AABB_4 = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.81D, 1D);
    private static final AxisAlignedBB AABB_5 = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.81D, 1D);
    private static final AxisAlignedBB AABB_6 = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.95D, 1D);
    private static final AxisAlignedBB AABB_7 = new AxisAlignedBB(0D, 0D, 0D, 1D, 1.00D, 1D);


    public static final String STACKSIZE_TAG = "size";

    private static final PropertyInteger STACKSIZE = PropertyInteger.create(STACKSIZE_TAG, 0, 7);

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STACKSIZE);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        Item item = Item.getItemFromBlock(this);
        ItemStack stack = new ItemStack(item);

            for (int i = 0; i <= 7; i++) {
                stack.setTagCompound(new NBTBuilder().setInteger(STACKSIZE_TAG, i).build());
                items.add(stack);
                stack = new ItemStack(item);
            }
        }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STACKSIZE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STACKSIZE).intValue();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return;
        }
        //noinspection ConstantConditions
        state = state.withProperty(STACKSIZE, stack.getTagCompound().getInteger(STACKSIZE_TAG));
        worldIn.setBlockState(pos, state);
    }


    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack coins = new ItemStack(AWNPCItems.COIN, ((state.getValue(STACKSIZE)+1)*8));
                coins.setTagCompound(new NBTBuilder().setString("metal","copper").build());
        drops.add(coins);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
                return true;
        }
        if (isCoin(stack)) {
            if (coinMetalInHand(stack).equals("copper"))
            {
                int itemstacksize = stack.getCount();
                if (itemstacksize >= 8) {
                    int blockstacksize = state.getValue(STACKSIZE);
                    if (blockstacksize < 7) {
                        world.setBlockState(pos, state.withProperty(STACKSIZE, (blockstacksize + 1)));
                        if (!player.capabilities.isCreativeMode) {
                            stack.setCount(itemstacksize - 8);
                        }
                    }
                }
                world.playSound(null, pos, AWStructureSounds.COINSTACK_INTERACT, SoundCategory.PLAYERS, 0.5f, 1);
            }
            return true;
        }
        if (!isCoin(stack)) {
            int blockstacksize = state.getValue(STACKSIZE);

                ItemStack newStack = new ItemStack(AWNPCItems.COIN, 8);
                        newStack.setTagCompound(new NBTBuilder().setString("metal","copper").build());

                player.addItemStackToInventory(newStack);
                if (blockstacksize > 0) {
                world.setBlockState(pos, state.withProperty(STACKSIZE, (blockstacksize - 1)));
                }
                if (blockstacksize == 0) {
                    world.setBlockToAir(pos);
                }
                world.playSound(null, pos, AWStructureSounds.COINSTACK_INTERACT, SoundCategory.PLAYERS, 0.5f, 1);
            return true;
        }

        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    private boolean isCoin(ItemStack heldItem) {
        return heldItem.getItem() == AWNPCItems.COIN;
    }

    private String coinMetalInHand(ItemStack heldItem) {
        return heldItem.getTagCompound().getString("metal");
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return AWStructureSoundTypes.COINSTACK;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String text;
        int stacksize = ((stack.getTagCompound().getInteger(STACKSIZE_TAG)+1)*8) ;
        text = stacksize + " " + I18n.format("guistrings.structure.coins");
        tooltip.add(text);

    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(STACKSIZE)) {
            case 0:
                return AABB_0;
            case 1:
                return AABB_1;
            case 2:
                return AABB_2;
            case 3:
                return AABB_3;
            case 4:
                return AABB_4;
            case 5:
                return AABB_5;
            case 6:
                return AABB_6;
            case 7:
                return AABB_7;
        }
        return new AxisAlignedBB (0D, 0D, 0D, 1D, 1.00D, 1D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient() {
        //noinspection ConstantConditions
        ResourceLocation baseLocation = new ResourceLocation(AncientWarfareCore.MOD_ID, "structure/" + getRegistryName().getResourcePath());

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override
            @SideOnly(Side.CLIENT)
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(baseLocation, getPropertyString(state.getProperties()));
            }
        });

        String modelPropInteger = "size=%s";

        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), stack -> {
            if (!stack.hasTagCompound()) {
                return new ModelResourceLocation(baseLocation, String.format(modelPropInteger, "0"));
            }
            NBTTagCompound tag = stack.getTagCompound();
            //noinspection ConstantConditions
            int size = tag.getInteger(STACKSIZE_TAG);
            return new ModelResourceLocation(baseLocation, String.format(modelPropInteger, size));
        });

        ModelLoader.registerItemVariants(Item.getItemFromBlock(this),
                new ModelResourceLocation(baseLocation, String.format(modelPropInteger, "0")));
    }
}
