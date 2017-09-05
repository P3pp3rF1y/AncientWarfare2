/*
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.structure.template;

import cpw.mods.fml.common.ModContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.api.*;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateParser;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginModDefault;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginVanillaHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StructurePluginManager implements IStructurePluginManager, IStructurePluginLookup {

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

        for(ModContainer container : Loader.instance().getActiveModList()) {
            if(!isDefaultMods(container.getModId())) {
                if(!MinecraftForge.EVENT_BUS.post(new StructurePluginRegistrationEvent(this, container.getModId()))){
                    this.addPlugin(new StructurePluginModDefault(container.getModId()));
                }
            }
        }

        if (ModuleStatus.npcsLoaded) {
            loadNpcPlugin();
        }
        if (ModuleStatus.vehiclesLoaded) {
            loadVehiclePlugin();
        }
        if (ModuleStatus.automationLoaded) {
            loadAutomationPlugin();
        }

        for (StructureContentPlugin plugin : this.loadedContentPlugins) {
            plugin.addHandledBlocks(this);
            plugin.addHandledEntities(this);
        }
    }

    private boolean isDefaultMods(String modid){
        return modid.equals("mcp") || modid.equals("FML") || modid.equals("Forge") || modid.startsWith(AncientWarfareCore.modID);
    }

    private void loadNpcPlugin() {
        try {
            Class<?> clz = Class.forName("net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginNpcs");
            if (clz != null) {
                Object e = clz.getDeclaredConstructor().newInstance();
                if (e instanceof StructureContentPlugin) {
                    addPlugin((StructureContentPlugin) e);
                    AWLog.log("Loaded NPC Module Structure Plugin");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadVehiclePlugin() {
        try {
            Class<?> clz = Class.forName("net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginVehicles");
            if (clz != null) {
                Object e = clz.getDeclaredConstructor().newInstance();
                if (e instanceof StructureContentPlugin) {
                    addPlugin((StructureContentPlugin) e);
                    AWLog.log("Loaded Vehicle Module Structure Plugin");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadAutomationPlugin() {
        try {
            Class<?> clz = Class.forName("net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginAutomation");
            if (clz != null) {
                Object e = clz.getDeclaredConstructor().newInstance();
                if (e instanceof StructureContentPlugin) {
                    addPlugin((StructureContentPlugin) e);
                    AWLog.log("Loaded Automation Module Structure Plugin");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addPlugin(StructureContentPlugin plugin) {
        loadedContentPlugins.add(plugin);
    }

    public String getPluginNameFor(Block block) {
        return pluginByBlock.get(block);
    }

    public String getPluginNameFor(Class<? extends TemplateRule> ruleClass) {
        return this.idByRuleClass.get(ruleClass);
    }

    public Class<? extends TemplateRule> getRuleByName(String name) {
        return this.ruleByID.get(name);
    }

    public TemplateRuleBlock getRuleForBlock(World world, Block block, int turns, int x, int y, int z) {
        Class<? extends TemplateRuleBlock> clz = blockRules.get(block);
        if (clz != null) {
            int meta = world.getBlockMetadata(x, y, z);
            try {
                return clz.getConstructor(World.class, int.class, int.class, int.class, Block.class, int.class, int.class).newInstance(world, x, y, z, block, meta, turns);
            } catch (Exception e) {
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
                } catch (Exception e) {
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
        }else{
            ruleByID.put(pluginName, ruleClass);
        }
        entityRules.put(entityClass, ruleClass);
        if(!idByRuleClass.containsKey(ruleClass))
            idByRuleClass.put(ruleClass, pluginName);
    }

    public void registerBlockHandler(String pluginName, Block block, Class<? extends TemplateRuleBlock> ruleClass) {
        if (ruleByID.containsKey(pluginName)) {
            if (!ruleByID.get(pluginName).equals(ruleClass)) {
                Class<? extends TemplateRule> clz = ruleByID.get(pluginName);
                throw new IllegalArgumentException("Attempt to overwrite " + clz + " with " + ruleClass + " by " + pluginName + " for block: " + block);
            }
        }else {
            ruleByID.put(pluginName, ruleClass);
        }
        if(idByRuleClass.containsKey(ruleClass)){
            pluginByBlock.put(block, idByRuleClass.get(ruleClass));
        }else{
            idByRuleClass.put(ruleClass, pluginName);
            pluginByBlock.put(block, pluginName);
        }
        blockRules.put(block, ruleClass);
    }

    @Override
    public void registerPlugin(StructureContentPlugin plugin) {
        addPlugin(plugin);
    }

    public static final TemplateRule getRule(List<String> ruleData, String ruleType) throws TemplateRuleParsingException {
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
            throw new TemplateRuleParsingException("Not enough data to create template rule.\n" +
                    "Missing plugin for name: " + name + "\n" +
                    "name: " + name + "\n" +
                    "number:" + ruleNumber + "\n" +
                    "ruleDataPackage.size:" + ruleDataPackage.size() + "\n");
        } else if (name == null || ruleNumber < 0 || ruleDataPackage.isEmpty()) {
            throw new TemplateRuleParsingException("Not enough data to create template rule.\n" +
                    "name: " + name + "\n" +
                    "number:" + ruleNumber + "\n" +
                    "ruleDataPackage.size:" + ruleDataPackage.size() + "\n" +
                    "ruleClass: " + clz);
        }
        try {
            TemplateRule rule = clz.getConstructor().newInstance();
            rule.parseRule(ruleNumber, ruleDataPackage);
            return rule;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public final static void writeRuleLines(TemplateRule rule, BufferedWriter out, String ruleType) throws IOException {
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
