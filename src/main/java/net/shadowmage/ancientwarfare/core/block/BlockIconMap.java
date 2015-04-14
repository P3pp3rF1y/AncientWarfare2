package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import java.util.HashMap;
import java.util.Map;

public class BlockIconMap {

    private final HashMap<Integer, IIcon> iconMap = new HashMap<Integer, IIcon>();
    private final HashMap<Integer, String> iconTexMap = new HashMap<Integer, String>();

    public void setIconTexture(int side, int meta, String texName) {
        iconTexMap.put(side + meta * 16, texName);
    }

    public void registerIcons(IIconRegister reg) {
        HashMap<String, IIcon> temp = new HashMap<String, IIcon>();
        IIcon icon;
        for (Map.Entry<Integer,String> entry : iconTexMap.entrySet()) {
            String tex = entry.getValue();
            if(temp.containsKey(tex)){
                icon = temp.get(tex);
            }else {
                icon = reg.registerIcon(tex);
                temp.put(tex, icon);
            }
            iconMap.put(entry.getKey(), icon);
        }
    }

    public IIcon getIconFor(int mcSide, int meta) {
        return iconMap.get(mcSide + meta * 16);
    }

}
