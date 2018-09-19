package net.shadowmage.ancientwarfare.structure.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import net.shadowmage.ancientwarfare.core.util.FileUtils;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import org.apache.commons.io.FilenameUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class SoundLoader implements ISelectiveResourceReloadListener {
	private static final String DEFAULT_SOUNDS_DIRECTORY = "assets/ancientwarfarestructure/sounds/auto_load";

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		if (resourcePredicate.test(VanillaResourceType.SOUNDS)) {
			reloadSounds();
		}
	}

	private static final Method LOAD_SOUND_RESOURCE = ReflectionHelper.findMethod(SoundHandler.class, "loadSoundResource", "func_147693_a", ResourceLocation.class, SoundList.class);

	private void loadSoundResource(SoundHandler soundHandler, ResourceLocation location, SoundList sounds) {
		try {
			LOAD_SOUND_RESOURCE.invoke(soundHandler, location, sounds);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			AncientWarfareStructure.LOG.error("Error loading sound {}:\n", location, e);
		}
	}

	private void reloadSounds() {
		SoundHandler sndHandler = Minecraft.getMinecraft().getSoundHandler();

		Set<String> sounds = new HashSet<>();
		//noinspection ConstantConditions
		FileUtils.findFiles(Loader.instance().activeModContainer().getSource(), DEFAULT_SOUNDS_DIRECTORY, (root, file) -> {
			String extension = FilenameUtils.getExtension(file.toString());
			if (extension.equals("ogg")) {
				String relative = root.relativize(file).toString();
				sounds.add(AncientWarfareStructure.MOD_ID + ":auto_load/" + FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/"));
			}
		});

		ProgressManager.ProgressBar resourcesBar = ProgressManager.push("Loading AW sounds", sounds.size());
		sounds.forEach(s ->
				{
					ResourceLocation location = new ResourceLocation(s);

					//noinspection ConstantConditions
					loadSoundResource(sndHandler, location, new SoundList(Collections.singletonList(
							new Sound(s, 1, 1, 1, Sound.Type.FILE, false)), false, null));

					if (!((IForgeRegistryModifiable)ForgeRegistries.SOUND_EVENTS).isLocked()) {
						ForgeRegistries.SOUND_EVENTS.register(new SoundEvent(location).setRegistryName(location));
					}

					resourcesBar.step(s);				}
		);
		ProgressManager.pop(resourcesBar);
	}

}
