package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

import java.util.HashMap;
import java.util.Map;

public class IconRotationMap {
    private final HashMap<RelativeSide, String> texNames = new HashMap<RelativeSide, String>();
    private final HashMap<RelativeSide, IIcon> icons = new HashMap<RelativeSide, IIcon>();

    public void setIcon(IRotatableBlock block, RelativeSide side, String texName) {
        RotationType t = block.getRotationType();
        if (t == RotationType.NONE) {
            //TODO throw error message about improper block-rotation type, perhaps just register the string as ALL_SIDES
        } else if (t == RotationType.SIX_WAY) {
            if (side != RelativeSide.TOP && side != RelativeSide.BOTTOM && side != RelativeSide.ANY_SIDE) {
                //TODO throw error message about improper block-rotation / cannot map specific sides on a six-way
            }
        }
        texNames.put(side, texName);
    }

    public void registerIcons(IIconRegister register) {
        HashMap<String, IIcon> temp = new HashMap<String, IIcon>();
        IIcon icon;
        for (Map.Entry<RelativeSide,String> entry : texNames.entrySet()) {
            String name = entry.getValue();
            if(temp.containsKey(name)){
                icon = temp.get(name);
            }else{
                icon = register.registerIcon(name);
                temp.put(name, icon);
            }
            icons.put(entry.getKey(), icon);
        }
    }

    public IIcon getIcon(IRotatableBlock block, int meta, int side) {
        return getIcon(RelativeSide.getSideViewed(block.getRotationType(), meta, side));
    }

    public IIcon getIcon(RelativeSide side) {
        return icons.get(side);
    }
}
