package net.shadowmage.ancientwarfare.vehicle.proxy;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.ConfigElement;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.InputHandler.InputCallback;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.MissileBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleCatapult;
import net.shadowmage.ancientwarfare.vehicle.input.VehicleInputKey;
import net.shadowmage.ancientwarfare.vehicle.render.RenderCatapult;
import net.shadowmage.ancientwarfare.vehicle.render.RenderMissile;
import net.shadowmage.ancientwarfare.vehicle.render.VehicleBBRender;

import java.util.ArrayList;
import java.util.List;


public class VehicleClientProxy extends VehicleCommonProxy {

    @Override
    public void registerClient() {
        RenderingRegistry.registerEntityRenderingHandler(VehicleBase.class, new VehicleBBRender());
        RenderingRegistry.registerEntityRenderingHandler(MissileBase.class, new RenderMissile());
        RenderingRegistry.registerEntityRenderingHandler(VehicleCatapult.class, new RenderCatapult());
        registerClientOptions();
    }

    public void registerClientOptions() {
        InputHandler.instance().registerKeybind(AWVehicleStatics.KEY_VEHICLE_FORWARD, AWVehicleStatics.keybindForward.getInt(), new VehicleInputCallback(VehicleInputKey.FORWARD));
        InputHandler.instance().registerKeybind(AWVehicleStatics.KEY_VEHICLE_REVERSE, AWVehicleStatics.keybindReverse.getInt(), new VehicleInputCallback(VehicleInputKey.REVERSE));
        InputHandler.instance().registerKeybind(AWVehicleStatics.KEY_VEHICLE_LEFT, AWVehicleStatics.keybindLeft.getInt(), new VehicleInputCallback(VehicleInputKey.LEFT));
        InputHandler.instance().registerKeybind(AWVehicleStatics.KEY_VEHICLE_RIGHT, AWVehicleStatics.keybindRight.getInt(), new VehicleInputCallback(VehicleInputKey.RIGHT));
        InputHandler.instance().registerKeybind(AWVehicleStatics.KEY_VEHICLE_FIRE, AWVehicleStatics.keybindFire.getInt(), new VehicleInputCallback(VehicleInputKey.FIRE));
        InputHandler.instance().registerKeybind(AWVehicleStatics.KEY_VEHICLE_ASCEND, AWVehicleStatics.keybindAscend.getInt(), new VehicleInputCallback(VehicleInputKey.ASCEND));
        InputHandler.instance().registerKeybind(AWVehicleStatics.KEY_VEHICLE_DESCEND, AWVehicleStatics.keybindDescend.getInt(), new VehicleInputCallback(VehicleInputKey.DESCEND));
        ConfigManager.registerConfigCategory(new VehicleCategory("awconfig.vehicle_keybinds", "awconfig.vehicle_keybinds"));
    }

    public static final class VehicleInputCallback extends InputCallback {
        VehicleInputKey key;

        public VehicleInputCallback(VehicleInputKey key) {
            this.key = key;
        }

        @Override
        public void onKeyPressed() {
            onKeyAction(true);
        }

        @Override
        public void onKeyReleased() {
            onKeyAction(false);
        }

        private void onKeyAction(boolean state) {
            EntityPlayer player = AncientWarfareCore.proxy.getClientPlayer();
            if (player.ridingEntity instanceof VehicleBase) {
                ((VehicleBase) player.ridingEntity).inputHandler.onKeyChanged(key, state);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static final class VehicleCategory extends DummyCategoryElement {

        @SuppressWarnings("unchecked")
        public VehicleCategory(String name, String tooltipkey) {
            super(name, tooltipkey, getElementList());
        }

        private static List<IConfigElement> getElementList() {
            ArrayList<IConfigElement> list = new ArrayList<IConfigElement>();
            list.add(new ConfigElement(AWVehicleStatics.keybindForward));
            list.add(new ConfigElement(AWVehicleStatics.keybindReverse));
            list.add(new ConfigElement(AWVehicleStatics.keybindLeft));
            list.add(new ConfigElement(AWVehicleStatics.keybindRight));
            list.add(new ConfigElement(AWVehicleStatics.keybindFire));
            list.add(new ConfigElement(AWVehicleStatics.keybindAscend));
            list.add(new ConfigElement(AWVehicleStatics.keybindDescend));
            return list;
        }
    }

}
