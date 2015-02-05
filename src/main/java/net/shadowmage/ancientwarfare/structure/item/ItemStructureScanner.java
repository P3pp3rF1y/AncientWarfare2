package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.template.save.TemplateExporter;
import net.shadowmage.ancientwarfare.structure.template.scan.TemplateScanner;

import java.io.File;
import java.util.List;

public class ItemStructureScanner extends Item implements IItemKeyInterface, IItemClickable, IBoxRenderer {

    public ItemStructureScanner(String localizationKey) {
        this.setUnlocalizedName(localizationKey);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setMaxStackSize(1);
        this.setTextureName("ancientwarfare:structure/" + localizationKey);
    }

    @Override
    public boolean cancelRightClick(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        if (par1ItemStack != null) {
            ItemStructureSettings viewSettings = ItemStructureSettings.getSettingsFor(par1ItemStack);
            String key = InputHandler.instance().getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
            if (viewSettings.hasPos1() && viewSettings.hasPos2() && viewSettings.hasBuildKey()) {
                list.add(key + " = " + StatCollector.translateToLocal("guistrings.structure.scanner.click_to_process"));
                list.add("(4/4)");
            } else if (!viewSettings.hasPos1()) {
                list.add(key + " = " + StatCollector.translateToLocal("guistrings.structure.scanner.select_first_pos"));
                list.add("(1/4)");
            } else if (!viewSettings.hasPos2()) {
                list.add(key + " = " + StatCollector.translateToLocal("guistrings.structure.scanner.select_second_pos"));
                list.add("(2/4)");
            } else if (!viewSettings.hasBuildKey()) {
                list.add(key + " = " + StatCollector.translateToLocal("guistrings.structure.scanner.select_offset"));
                list.add("(3/4)");
            }
        }
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        ItemStructureSettings scanSettings = ItemStructureSettings.getSettingsFor(stack);
        if (player.isSneaking()) {
            scanSettings.clearSettings();
            ItemStructureSettings.setSettingsFor(stack, scanSettings);
        } else if (scanSettings.hasPos1() && scanSettings.hasPos2() && scanSettings.hasBuildKey()) {
            BlockPosition key = scanSettings.key;
            if (player.getDistance(key.x + 0.5d, key.y, key.z + 0.5d) > 10) {
                player.addChatMessage(new ChatComponentText("You are too far away to scan that building, move closer to chosen build-key position"));
                return;
            }
            player.addChatMessage(new ChatComponentText("Initiating Scan"));
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SCANNER, 0, 0, 0);
        }
    }

    public static boolean scanStructure(World world, BlockPosition pos1, BlockPosition pos2, BlockPosition key, int face, String name, boolean include, NBTTagCompound tag) {
        BlockPosition min = BlockTools.getMin(pos1, pos2);
        BlockPosition max = BlockTools.getMax(pos1, pos2);
        TemplateScanner scanner = new TemplateScanner();
        int turns = face == 0 ? 2 : face == 1 ? 1 : face == 2 ? 0 : face == 3 ? 3 : 0; //because for some reason my mod math was off?
        StructureTemplate template = scanner.scan(world, min, max, key, turns, name);

        String validationType = tag.getString("validationType");
        StructureValidationType type = StructureValidationType.getTypeFromName(validationType);
        StructureValidator validator = type.getValidator();
        validator.readFromNBT(tag);
        template.setValidationSettings(validator);
        if (include) {
            StructureTemplateManager.instance().addTemplate(template);
        }
        TemplateExporter.exportTo(template, new File(include ? TemplateLoader.includeDirectory : TemplateLoader.outputDirectory));
        return true;
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        if (!MinecraftServer.getServer().getConfigurationManager().func_152607_e(player.getGameProfile())) {
            return;
        }
        BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
        if (hit == null) {
            return;
        }
        ItemStructureSettings scanSettings = ItemStructureSettings.getSettingsFor(stack);
        if (scanSettings.hasPos1() && scanSettings.hasPos2() && scanSettings.hasBuildKey()) {
            player.addChatMessage(new ChatComponentTranslation("guistrings.structure.scanner.click_to_process"));
        } else if (!scanSettings.hasPos1()) {
            scanSettings.setPos1(hit.x, hit.y, hit.z);
            player.addChatMessage(new ChatComponentTranslation("guistrings.structure.scanner.set_first_pos"));
        } else if (!scanSettings.hasPos2()) {
            scanSettings.setPos2(hit.x, hit.y, hit.z);
            player.addChatMessage(new ChatComponentTranslation("guistrings.structure.scanner.set_second_pos"));
        } else if (!scanSettings.hasBuildKey()) {
            scanSettings.setBuildKey(hit.x, hit.y, hit.z, BlockTools.getPlayerFacingFromYaw(player.rotationYaw));
            player.addChatMessage(new ChatComponentTranslation("guistrings.structure.scanner.set_offset_pos"));
        }
        ItemStructureSettings.setSettingsFor(stack, scanSettings);
    }

    @Override
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {

    }

    @Override
    public void renderBox(EntityPlayer player, ItemStack stack, float delta) {
        ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
        BlockPosition pos1, pos2, min, max;
        if (settings.hasPos1()) {
            pos1 = settings.pos1();
        } else {
            pos1 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
        }
        if (settings.hasPos2()) {
            pos2 = settings.pos2();
        } else {
            pos2 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
        }
        if (pos1 != null && pos2 != null) {
            min = BlockTools.getMin(pos1, pos2);
            max = BlockTools.getMax(pos1, pos2);
            Util.renderBoundingBox(player, min, max, delta);
        }
    }
}
