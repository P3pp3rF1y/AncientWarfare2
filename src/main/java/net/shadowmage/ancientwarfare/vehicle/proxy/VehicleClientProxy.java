package net.shadowmage.ancientwarfare.vehicle.proxy;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
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
import org.lwjgl.input.Keyboard;

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
        InputHandler.instance.registerKeybind(AWVehicleStatics.KEY_VEHICLE_FORWARD, Keyboard.KEY_W, new VehicleInputCallback(VehicleInputKey.FORWARD));
        InputHandler.instance.registerKeybind(AWVehicleStatics.KEY_VEHICLE_REVERSE, Keyboard.KEY_S, new VehicleInputCallback(VehicleInputKey.REVERSE));
        InputHandler.instance.registerKeybind(AWVehicleStatics.KEY_VEHICLE_LEFT, Keyboard.KEY_A, new VehicleInputCallback(VehicleInputKey.LEFT));
        InputHandler.instance.registerKeybind(AWVehicleStatics.KEY_VEHICLE_RIGHT, Keyboard.KEY_D, new VehicleInputCallback(VehicleInputKey.RIGHT));
        InputHandler.instance.registerKeybind(AWVehicleStatics.KEY_VEHICLE_FIRE, Keyboard.KEY_SPACE, new VehicleInputCallback(VehicleInputKey.FIRE));
        InputHandler.instance.registerKeybind(AWVehicleStatics.KEY_VEHICLE_ASCEND, Keyboard.KEY_R, new VehicleInputCallback(VehicleInputKey.ASCEND));
        InputHandler.instance.registerKeybind(AWVehicleStatics.KEY_VEHICLE_DESCEND, Keyboard.KEY_F, new VehicleInputCallback(VehicleInputKey.DESCEND));
        ConfigManager.registerConfigCategory(new VehicleCategory("awconfig.vehicle_keybinds"));
    }

    public static final class VehicleInputCallback implements InputCallback {
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
            if (player.getRidingEntity() instanceof VehicleBase) {
                ((VehicleBase) player.getRidingEntity()).inputHandler.onKeyChanged(key, state);
            }
        }
    }

    public static final class VehicleCategory extends DummyCategoryElement {

        public VehicleCategory(String name) {
            super(name, name, getElementList());
        }

        private static List<IConfigElement> getElementList() {
            List<Property> props = InputHandler.instance.getKeyConfig("vehicle");
            List<IConfigElement> list = new ArrayList<>();
            for(Property property : props) {
                list.add(new ConfigElement(property));
            }
            return list;
        }
    }

}
