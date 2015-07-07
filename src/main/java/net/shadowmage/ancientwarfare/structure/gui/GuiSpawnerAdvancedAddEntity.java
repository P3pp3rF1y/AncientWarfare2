package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.entity.WatchedData;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnGroup;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GuiSpawnerAdvancedAddEntity extends GuiContainerBase {

    CompositeScrolled area;
    GuiContainerBase parent;
    EntitySpawnGroup group;
    EntitySpawnSettings settings = new EntitySpawnSettings();
    boolean showAddButton;

    List<String> tagInput = new ArrayList<String>();
    boolean showAddTagButton = true;

    HashMap<Button, Integer> buttonToLineMap = new HashMap<Button, Integer>();
    HashMap<Text, Integer> textToLineMap = new HashMap<Text, Integer>();

    WatchedData.Type[] dataType;
    int[] dataKey;
    String[] dataValue;

    public GuiSpawnerAdvancedAddEntity(GuiContainerBase parent, EntitySpawnGroup group, EntitySpawnSettings settings) {
        super(parent.getContainer());
        this.parent = parent;
        this.group = group;
        this.settings = settings;
        if (this.settings == null) {
            showAddButton = true;
            this.settings = new EntitySpawnSettings();
            this.settings.setEntityToSpawn("Pig");
            this.settings.setSpawnCountMin(2);
            this.settings.setSpawnCountMax(4);
            this.settings.setSpawnLimitTotal(-1);
        }
        loadData();
    }

    private void loadData(){
        List<WatchedData> data = this.settings.getCustomData();
        int size = data.size();
        dataType = new WatchedData.Type[size];
        dataKey = new int[size];
        dataValue = new String[size];
        int i = 0;
        for(WatchedData d : data){
            dataType[i] = d.getType();
            dataKey[i] = d.getDataValueId();
            dataValue[i] = dataType[i].toString(d.getObject());
            i++;
        }
        NBTTagCompound tag = this.settings.getCustomTag();
        if(tag!=null){
            String[] splits = tag.toString().split("}");
            for(String t : splits){
                tagInput.add(t+"}");
            }
            showAddTagButton = false;
        }
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        return false;
    }

    @Override
    public void initElements() {
        Button button;

        if (showAddButton) {
            button = new Button(8, 8, 160, 12, "guistrings.spawner.add_entity") {
                @Override
                protected void onPressed() {
                    group.addSpawnSetting(settings);
                    Minecraft.getMinecraft().displayGuiScreen(parent);
                    parent.refreshGui();
                }
            };
            addGuiElement(button);
        }

        button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done") {
            @Override
            protected void onPressed() {
                saveData();
                saveTag();
                Minecraft.getMinecraft().displayGuiScreen(parent);
                parent.refreshGui();
            }
        };
        addGuiElement(button);

        Label label = new Label(8, 40 - 14, "guistrings.spawner.set_entity_properties");
        addGuiElement(label);

        area = new CompositeScrolled(this, 0, 40, 256, 200);
        addGuiElement(area);
    }

    private void saveData(){
        List<WatchedData> dataList = new ArrayList<WatchedData>();
        for(int i = 0; i < dataType.length; i++){
            try{
                WatchedData data = new WatchedData(dataType[i], dataKey[i], dataValue[i]);
                if(data.isValid() && !dataList.contains(data)){
                    dataList.add(data);
                }
            }catch (Throwable ignored){

            }
        }
        Collections.sort(dataList, WatchedData.IndexSorter.INSTANCE);
        settings.getCustomData().clear();
        for(WatchedData data: dataList){
            settings.addCustomData(data);
        }
    }

    private void saveTag(){
        if(!tagInput.isEmpty()){
            String tag = String.join("", tagInput);
            try {
                NBTBase base = JsonToNBT.func_150315_a(tag);
                if(base instanceof NBTTagCompound && !((NBTTagCompound) base).hasNoTags()){
                    settings.setCustomSpawnTag((NBTTagCompound) base);
                }
            }catch (Throwable t){
                t.printStackTrace();
            }
        }else{
            settings.setCustomSpawnTag(null);
        }
    }

    @Override
    public void setupElements() {
        area.clearElements();
        buttonToLineMap.clear();

        Label label;
        NumberInput input;
        Button button;
        Text text;

        int lineNumber = 0;
        int totalHeight = 8;

        label = new Label(8, totalHeight, "guistrings.spawner.select_entity");
        area.addGuiElement(label);

        button = new Button(100, totalHeight, 120, 12, settings.getEntityName()) {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(new GuiSpawnerAdvancedEntitySelection(GuiSpawnerAdvancedAddEntity.this, settings));
            }
        };
        area.addGuiElement(button);
        totalHeight += 12;

        label = new Label(8, totalHeight, "guistrings.spawner.min");
        area.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 30, settings.getSpawnMin(), this) {
            @Override
            public void onValueUpdated(float value) {
                settings.setSpawnCountMin((int)value);
            }
        };
        input.setIntegerValue();
        area.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, "guistrings.spawner.max");
        area.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 30, settings.getSpawnMax(), this) {
            @Override
            public void onValueUpdated(float value) {
                settings.setSpawnCountMax((int)value);
            }
        };
        input.setIntegerValue();
        area.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, "guistrings.spawner.total");
        area.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 30, settings.getSpawnTotal(), this) {
            @Override
            public void onValueUpdated(float value) {
                settings.setSpawnLimitTotal((int)value);
            }
        };
        input.setIntegerValue();
        input.setAllowNegative();
        input.setValue(settings.getSpawnTotal());
        area.addGuiElement(input);
        totalHeight += 20;

        label = new Label(8, totalHeight, "guistrings.spawner.custom_tag");
        area.addGuiElement(label);
        totalHeight += 12;

        Tooltip tip;
        if (showAddTagButton) {
            button = new Button(8, totalHeight, 120, 12, "guistrings.spawner.add_custom_tag") {
                @Override
                protected void onPressed() {
                    tagInput.add("{");
                    tagInput.add("");
                    tagInput.add("}");
                    refreshGui();
                    showAddTagButton = false;
                }
            };
            tip = new Tooltip(50, 20);
            tip.addTooltipElement(new Label(0, 0, "guistrings.spawner.custom_tag_tip"));
            button.setTooltip(tip);
            area.addGuiElement(button);
            totalHeight += 12;
        }

        for (String line : tagInput) {
            text = new Text(8, totalHeight, 200, line, this) {
                @Override
                protected void handleKeyInput(int keyCode, char ch) {
                    super.handleKeyInput(keyCode, ch);
                    int lineNumber = textToLineMap.get(this);
                    tagInput.set(lineNumber, getText());
                }
            };
            textToLineMap.put(text, lineNumber);
            area.addGuiElement(text);

            button = new Button(208, totalHeight, 12, 12, "guistrings.spawner.add") {
                @Override
                protected void onPressed() {
                    int lineNumber = buttonToLineMap.get(this);
                    tagInput.add(lineNumber, "");
                    refreshGui();
                }
            };
            buttonToLineMap.put(button, lineNumber);
            area.addGuiElement(button);

            button = new Button(220, totalHeight, 12, 12, "guistrings.spawner.remove") {
                @Override
                protected void onPressed() {
                    int lineNumber = buttonToLineMap.get(this);
                    tagInput.remove(lineNumber);
                    if (tagInput.isEmpty()) {
                        showAddTagButton = true;
                    }
                    refreshGui();
                }
            };
            buttonToLineMap.put(button, lineNumber);
            area.addGuiElement(button);

            totalHeight += 12;
            lineNumber++;
        }

        label = new Label(8, totalHeight, "guistrings.spawner.custom_data");
        area.addGuiElement(label);
        totalHeight += 12;
        if(dataType.length<32) {
            button = new Button(8, totalHeight, 120, 12, "guistrings.spawner.add_custom_data") {
                @Override
                protected void onPressed() {
                    WatchedData.Type[] t = new WatchedData.Type[dataType.length+1];
                    int[] k = new int[t.length];
                    String[] v = new String[t.length];
                    System.arraycopy(dataType, 0, t, 0, dataType.length);
                    System.arraycopy(dataKey, 0, k, 0, dataType.length);
                    System.arraycopy(dataValue, 0, v, 0, dataType.length);
                    t[t.length-1] = WatchedData.Type.BYTE;
                    k[k.length-1] = 19;
                    v[v.length-1] = "0";
                    dataType = t;
                    dataKey = k;
                    dataValue = v;
                    refreshGui();
                }
            };
            tip = new Tooltip(50, 20);
            tip.addTooltipElement(new Label(0, 0, "guistrings.spawner.custom_data_tip0"));
            button.setTooltip(tip);
            area.addGuiElement(button);
            totalHeight += 12;
        }
        for(int i = 0; i < dataType.length; i++) {
            final int j = i;
            text = new Text(8, totalHeight, 140, dataValue[j], this) {
                @Override
                public void onTextUpdated(String oldText, String newText) {
                    dataValue[j] = newText;
                }
            };
            tip = new Tooltip(50, 20);
            tip.addTooltipElement(new Label(0, 0, "guistrings.spawner.custom_data_tip1"));
            text.setTooltip(tip);
            area.addGuiElement(text);

            input = new NumberInput(150, totalHeight, 15, dataKey[j], this) {
                @Override
                public void onValueUpdated(float value) {
                    if(value > 31){
                        value = 31;
                    }
                    dataKey[j] = (int) value;
                }
            };
            input.setIntegerValue();
            tip = new Tooltip(50, 20);
            tip.addTooltipElement(new Label(0, 0, "guistrings.spawner.custom_data_tip2"));
            input.setTooltip(tip);
            area.addGuiElement(input);

            button = new Button(170, totalHeight, 50, 12, dataType[j].name()) {
                @Override
                protected void onPressed() {
                    dataType[j] = dataType[j].next();
                    setText(dataType[j].name());
                }
            };
            tip = new Tooltip(50, 20);
            tip.addTooltipElement(new Label(0, 0, "guistrings.spawner.custom_data_tip3"));
            button.setTooltip(tip);
            area.addGuiElement(button);

            totalHeight += 12;
        }
        area.setAreaSize(totalHeight);
    }

}
