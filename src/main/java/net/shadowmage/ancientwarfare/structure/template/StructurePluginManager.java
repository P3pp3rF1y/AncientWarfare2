package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginRegister;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.api.StructurePluginRegistrationEvent;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;
import net.shadowmage.ancientwarfare.structure.template.datafixes.DataFixManager;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.StructurePluginAutomation;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.StructurePluginModDefault;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.StructurePluginNpcs;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.StructurePluginVanillaHandler;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.StructurePluginVehicles;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockInventory;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockTile;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleVanillaBlocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class StructurePluginManager implements IStructurePluginRegister {
	private final List<StructureContentPlugin> loadedContentPlugins = new ArrayList<>();

	private final List<RuleHandler<IBlockDataMatcher, IBlockRuleCreator, IRuleDeserializer<TemplateRuleBlock>>> blockRuleHandlers = new ArrayList<>();
	private final List<RuleHandler<Class<? extends Entity>, IEntityRuleCreator, IRuleDeserializer<TemplateRuleEntity>>> entityRuleHandlers = new ArrayList<>();

	public static final StructurePluginManager INSTANCE = new StructurePluginManager();

	private StructurePluginManager() {
	}

	public void loadPlugins() {
		addPlugin(new StructurePluginVanillaHandler());

		for (ModContainer container : Loader.instance().getActiveModList()) {
			if (!isDefaultMods(container.getModId()) && !MinecraftForge.EVENT_BUS.post(new StructurePluginRegistrationEvent(this, container.getModId()))) {
				addPlugin(new StructurePluginModDefault(container.getModId()));
			}
		}

		if (Loader.isModLoaded("ancientwarfarenpc")) {
			loadNpcPlugin();
		}
		if (Loader.isModLoaded("ancientwarfarevehicle")) {
			loadVehiclePlugin();
		}
		if (Loader.isModLoaded("ancientwarfareautomation")) {
			loadAutomationPlugin();
		}

		for (StructureContentPlugin plugin : this.loadedContentPlugins) {
			plugin.addHandledBlocks(this);
			plugin.addHandledEntities(this);
		}

		//noinspection ConstantConditions
		registerBlockHandler(TemplateRuleVanillaBlocks.PLUGIN_NAME, (world, pos, state) -> state.getBlock().hasTileEntity(state) && WorldTools.hasItemHandler(world, pos),
				TemplateRuleBlockInventory::new, TemplateRuleBlockInventory::new);
		registerBlockHandler(TemplateRuleVanillaBlocks.PLUGIN_NAME, (world, pos, state) -> state.getBlock().hasTileEntity(state) && !WorldTools.hasItemHandler(world, pos),
				TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		registerBlockHandler(TemplateRuleVanillaBlocks.PLUGIN_NAME, (world, pos, state) -> !state.getBlock().hasTileEntity(state),
				TemplateRuleVanillaBlocks::new, TemplateRuleVanillaBlocks::new);
	}

	private boolean isDefaultMods(String modid) {
		return modid.equals("minecraft") || modid.equals("mcp") || modid.equals("FML") || modid.equals("forge") || modid.startsWith(AncientWarfareCore.MOD_ID);
	}

	private void loadNpcPlugin() {
		addPlugin(new StructurePluginNpcs());
		AncientWarfareStructure.LOG.info("Loaded NPC Module Structure Plugin");
	}

	private void loadVehiclePlugin() {
		addPlugin(new StructurePluginVehicles());
		AncientWarfareStructure.LOG.info("Loaded Vehicle Module Structure Plugin");
	}

	private void loadAutomationPlugin() {
		addPlugin(new StructurePluginAutomation());
		AncientWarfareStructure.LOG.info("Loaded Automation Module Structure Plugin");
	}

	private void addPlugin(StructureContentPlugin plugin) {
		loadedContentPlugins.add(plugin);
	}

	public Optional<String> getPluginNameFor(World world, BlockPos pos, IBlockState state) {
		return getRuleHandler(world, pos, state).map(h -> h.pluginName);
	}

	private Optional<? extends IRuleDeserializer> getDeserializerByName(String name) {
		Optional<IRuleDeserializer<TemplateRuleBlock>> result = blockRuleHandlers.stream().filter(h -> h.pluginName.equals(name)).findFirst().map(h -> h.deserializer);
		if (result.isPresent()) {
			return result;
		}
		return entityRuleHandlers.stream().filter(h -> h.pluginName.equals(name)).findFirst().map(h -> h.deserializer);
	}

	public Optional<TemplateRuleBlock> getRuleForBlock(World world, IBlockState state, int turns, BlockPos pos) {
		Optional<IBlockRuleCreator> creator = getRuleHandler(world, pos, state).map(h -> h.ruleCreator);
		return creator.map(c -> c.create(world, pos, state, turns));
	}

	private Optional<RuleHandler<IBlockDataMatcher, IBlockRuleCreator, IRuleDeserializer<TemplateRuleBlock>>> getRuleHandler(World world, BlockPos pos, IBlockState state) {
		return blockRuleHandlers.stream().filter(h -> h.obj.matches(world, pos, state)).findFirst();
	}

	public Optional<TemplateRuleEntity> getRuleForEntity(World world, Entity entity, int turns, int x, int y, int z) {
		return entityRuleHandlers.stream().filter(h -> h.obj.isAssignableFrom(entity.getClass())).findFirst().map(h -> h.ruleCreator)
				.map(c -> c.create(world, entity, turns, x, y, z));
	}

	public void registerEntityHandler(String pluginName, Class<? extends Entity> entityClass, IEntityRuleCreator creator, IRuleDeserializer<TemplateRuleEntity> deserializer) {
		entityRuleHandlers.add(new RuleHandler<>(entityClass, pluginName, creator, deserializer));
	}

	public void registerBlockHandler(String pluginName, IBlockDataMatcher blockMatcher, IBlockRuleCreator creator, IRuleDeserializer<TemplateRuleBlock> deserializer) {
		blockRuleHandlers.add(new RuleHandler<>(blockMatcher, pluginName, creator, deserializer));
	}

	public void registerBlockHandler(String pluginName, Block block, IBlockRuleCreator creator, IRuleDeserializer<TemplateRuleBlock> deserializer) {
		registerBlockHandler(pluginName, (world, pos, state) -> state.getBlock() == block, creator, deserializer);
	}

	public void registerPlugin(StructureContentPlugin plugin) {
		addPlugin(plugin);
	}

	public static <T extends TemplateRule> FixResult<T> getRule(Version version, List<String> ruleData, String ruleType) throws TemplateRuleParsingException {
		Iterator<String> it = ruleData.iterator();
		String name = null;
		int ruleNumber = -1;
		String line;
		List<String> ruleDataPackage = new ArrayList<>();
		while (it.hasNext()) {
			line = it.next();
			if (line.startsWith(ruleType + ":")) {
				continue;
			}
			if (line.startsWith(":end" + ruleType)) {
				break;
			}
			if (line.startsWith("plugin=")) {
				name = StringTools.safeParseString("=", line);
			}
			if (line.startsWith("number=")) {
				ruleNumber = StringTools.safeParseInt("=", line);
			}
			if (line.startsWith("data:")) {
				while (it.hasNext()) {
					line = it.next();
					if (line.startsWith(":enddata")) {
						break;
					}
					ruleDataPackage.add(line);
				}
			}
		}

		if (name == null || ruleNumber < 0 || ruleDataPackage.isEmpty()) {
			throw new TemplateRuleParsingException("Not enough data to create template rule.\n" + "name: " + name + "\n" + "number:" + ruleNumber + "\n" + "ruleDataPackage.size:" + ruleDataPackage.size() + "\n");
		}

		FixResult.Builder<T> resultBuilder = new FixResult.Builder<>();

		name = resultBuilder.updateAndGetData(DataFixManager.fixRuleName(version, name));
		Optional<? extends IRuleDeserializer> deserializer = INSTANCE.getDeserializerByName(name);
		if (!deserializer.isPresent()) {
			throw new TemplateRuleParsingException("Not enough data to create template rule.\n" + "Missing plugin for name: " + name + "\n" + "name: " + name + "\n" + "number:" + ruleNumber + "\n" + "ruleDataPackage.size:" + ruleDataPackage.size() + "\n");
		}

		if (StructureTemplate.CURRENT_VERSION.isGreaterThan(version)) {
			ruleDataPackage = resultBuilder.updateAndGetData(DataFixManager.fixRuleData(version, name, ruleDataPackage));
		}

		TemplateRule rule = deserializer.get().deserialize(ruleNumber, ruleDataPackage);

		T actualRule;
		try {
			//noinspection unchecked
			actualRule = (T) rule;
		}
		catch (ClassCastException e) {
			throw new TemplateRuleParsingException("Incorrect rule type is being returned\n");
		}

		return resultBuilder.build(actualRule);
	}

	private class RuleHandler<T, U extends IRuleCreator, V extends IRuleDeserializer> {
		private final T obj;
		private final String pluginName;
		private U ruleCreator;
		private V deserializer;

		private RuleHandler(T obj, String pluginName, U creator, V deserializer) {
			this.obj = obj;
			this.pluginName = pluginName;
			this.ruleCreator = creator;
			this.deserializer = deserializer;
		}
	}

	interface IRuleCreator {}

	public interface IBlockRuleCreator extends IRuleCreator {
		TemplateRuleBlock create(World world, BlockPos pos, IBlockState state, int turns);
	}

	public interface IEntityRuleCreator extends IRuleCreator {
		TemplateRuleEntity create(World world, Entity entity, int turns, int x, int y, int z);
	}

	public interface IRuleDeserializer<T extends TemplateRule> {
		T deserialize(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException;
	}

	private interface IBlockDataMatcher {
		boolean matches(World world, BlockPos pos, IBlockState state);
	}
}
