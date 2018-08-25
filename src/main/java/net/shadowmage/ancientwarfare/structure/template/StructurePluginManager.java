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
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginLookup;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.api.StructurePluginRegistrationEvent;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;
import net.shadowmage.ancientwarfare.structure.template.datafixes.DataFixManager;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateParser;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginAutomation;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginModDefault;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginNpcs;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginVanillaHandler;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginVehicles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StructurePluginManager implements IStructurePluginManager, IStructurePluginLookup {
	//TODO is there really a need for so much reflection or it can be rewritten using something like factory methods?

	private final List<StructureContentPlugin> loadedContentPlugins = new ArrayList<>();

	private final HashMap<Class<? extends Entity>, Class<? extends TemplateRuleEntity>> entityRules = new HashMap<>();
	private final HashMap<Block, Class<? extends TemplateRuleBlock>> blockRules = new HashMap<>();
	private final HashMap<Class<? extends TemplateRule>, String> idByRuleClass = new HashMap<>();
	private final HashMap<String, Class<? extends TemplateRule>> ruleByID = new HashMap<>();
	private final HashMap<Block, String> pluginByBlock = new HashMap<>();

	public static final StructurePluginManager INSTANCE = new StructurePluginManager();

	private StructurePluginManager() {
	}

	public void loadPlugins() {
		this.addPlugin(new StructurePluginVanillaHandler());

		for (ModContainer container : Loader.instance().getActiveModList()) {
			if (!isDefaultMods(container.getModId())) {
				if (!MinecraftForge.EVENT_BUS.post(new StructurePluginRegistrationEvent(this, container.getModId()))) {
					this.addPlugin(new StructurePluginModDefault(container.getModId()));
				}
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

	public String getPluginNameFor(Block block) {
		return pluginByBlock.get(block);
	}

	public String getPluginNameFor(Class<? extends TemplateRule> ruleClass) {
		return this.idByRuleClass.get(ruleClass);
	}

	private Class<? extends TemplateRule> getRuleByName(String name) {
		return this.ruleByID.get(name);
	}

	public TemplateRuleBlock getRuleForBlock(World world, Block block, int turns, BlockPos pos) {
		Class<? extends TemplateRuleBlock> clz = blockRules.get(block);
		if (clz != null) {
			IBlockState state = world.getBlockState(pos);
			int meta = state.getBlock().getMetaFromState(state);
			try {
				return clz.getConstructor(World.class, BlockPos.class, Block.class, int.class, int.class).newInstance(world, pos, block, meta, turns);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public TemplateRuleEntity getRuleForEntity(World world, Entity entity, int turns, int x, int y, int z) {
		Class<? extends Entity> entityClass = entity.getClass();
		if (this.entityRules.containsKey(entityClass)) {
			Class<? extends TemplateRuleEntity> entityRuleClass = this.entityRules.get(entityClass);
			if (entityRuleClass != null) {
				try {
					return entityRuleClass.getConstructor(World.class, Entity.class, int.class, int.class, int.class, int.class).newInstance(world, entity, turns, x, y, z);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;//TODO
	}

	public void registerEntityHandler(String pluginName, Class<? extends Entity> entityClass, Class<? extends TemplateRuleEntity> ruleClass) {
		if (ruleByID.containsKey(pluginName)) {
			if (!ruleByID.get(pluginName).equals(ruleClass)) {
				Class<? extends TemplateRule> clz = ruleByID.get(pluginName);
				throw new IllegalArgumentException("Attempt to overwrite " + clz + " with " + ruleClass + " by " + pluginName + " for entityClass: " + entityClass);
			}
		} else {
			ruleByID.put(pluginName, ruleClass);
		}
		entityRules.put(entityClass, ruleClass);
		if (!idByRuleClass.containsKey(ruleClass))
			idByRuleClass.put(ruleClass, pluginName);
	}

	public void registerBlockHandler(String pluginName, Block block, Class<? extends TemplateRuleBlock> ruleClass) {
		if (ruleByID.containsKey(pluginName)) {
			if (!ruleByID.get(pluginName).equals(ruleClass)) {
				Class<? extends TemplateRule> clz = ruleByID.get(pluginName);
				throw new IllegalArgumentException("Attempt to overwrite " + clz + " with " + ruleClass + " by " + pluginName + " for block: " + block);
			}
		} else {
			ruleByID.put(pluginName, ruleClass);
		}
		if (idByRuleClass.containsKey(ruleClass)) {
			pluginByBlock.put(block, idByRuleClass.get(ruleClass));
		} else {
			idByRuleClass.put(ruleClass, pluginName);
			pluginByBlock.put(block, pluginName);
		}
		blockRules.put(block, ruleClass);
	}

	@Override
	public void registerPlugin(StructureContentPlugin plugin) {
		addPlugin(plugin);
	}

	public static TemplateRule getRule(Version version, List<String> ruleData, String ruleType) throws TemplateRuleParsingException {
		Iterator<String> it = ruleData.iterator();
		String name = null;
		int ruleNumber = -1;
		String line;
		List<String> ruleDataPackage = new ArrayList<>();
		while (it.hasNext()) {
			TemplateParser.lineNumber++;
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

		Class<? extends TemplateRule> clz = INSTANCE.getRuleByName(name);
		if (clz == null) {
			throw new TemplateRuleParsingException("Not enough data to create template rule.\n" + "Missing plugin for name: " + name + "\n" + "name: " + name + "\n" + "number:" + ruleNumber + "\n" + "ruleDataPackage.size:" + ruleDataPackage.size() + "\n");
		} else if (name == null || ruleNumber < 0 || ruleDataPackage.isEmpty()) {
			throw new TemplateRuleParsingException("Not enough data to create template rule.\n" + "name: " + name + "\n" + "number:" + ruleNumber + "\n" + "ruleDataPackage.size:" + ruleDataPackage.size() + "\n" + "ruleClass: " + clz);
		}

		if (StructureTemplate.CURRENT_VERSION.isGreaterThan(version)) {
			ruleDataPackage = DataFixManager.fixRuleData(version, name, ruleDataPackage);
		}

		try {
			TemplateRule rule = clz.getConstructor().newInstance();
			rule.parseRule(ruleNumber, ruleDataPackage);
			return rule;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeRuleLines(TemplateRule rule, BufferedWriter out, String ruleType) throws IOException {
		if (rule == null) {
			return;
		}
		String id = INSTANCE.getPluginNameFor(rule.getClass());
		if (id == null) {
			return;
		}
		out.write(ruleType + ":");
		out.newLine();
		out.write("plugin=" + id);
		out.newLine();
		out.write("number=" + rule.ruleNumber);
		out.newLine();
		out.write("data:");
		out.newLine();
		rule.writeRule(out);
		out.write(":enddata");
		out.newLine();
		out.write(":end" + ruleType);
		out.newLine();
		out.newLine();
	}
}
