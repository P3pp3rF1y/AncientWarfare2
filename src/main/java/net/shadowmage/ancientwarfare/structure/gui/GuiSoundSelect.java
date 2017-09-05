/*
 Copyright 2015 Olivier Sylvain (aka GotoLink)
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
package net.shadowmage.ancientwarfare.structure.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.RegistrySimple;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;

import java.lang.reflect.Field;
import java.util.Iterator;

public class GuiSoundSelect extends GuiContainerBase{
    private final GuiContainerBase parent;
    private final SongPlayData.SongEntry songEntry;
    private Field sndRegistry;
    private CompositeScrolled area;
    private Text selectionLabel;
    protected GuiSoundSelect(GuiContainerBase parent, SongPlayData.SongEntry entry) {
        super(parent.getContainer());
        this.parent = parent;
        this.songEntry = entry;
        try {
            sndRegistry = ReflectionHelper.findField(SoundHandler.class, "sndRegistry", "field_147697_e");//TODO check reflection status
        }catch (Exception ignored){
        }
    }

    @Override
    public void initElements() {
        Button button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done") {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(parent);
                parent.refreshGui();
            }
        };
        addGuiElement(button);
        selectionLabel = new Text(8, 30, 240, songEntry.name(), this) {
            @Override
            protected void handleKeyInput(int keyCode, char ch) {
                String old = getText();
                super.handleKeyInput(keyCode, ch);
                String text = getText();
                if (!text.equals(old)) {
                    refreshGui();
                }
            }
        };
        addGuiElement(selectionLabel);
        area = new CompositeScrolled(this, 0, 40, 256, 200);
        addGuiElement(area);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        Iterator itr;
        try {
            itr = Iterators.filter(((RegistrySimple) sndRegistry.get(Minecraft.getMinecraft().getSoundHandler())).getKeys().iterator(), new Predicate(){

                @Override
                public boolean apply(Object input) {
                    return input.toString().contains(selectionLabel.getText());
                }
            });
        }catch (Exception e){
            return;
        }

        int totalHeight = 8;
        Button button;
        while (itr.hasNext()) {
            final String name = itr.next().toString();
            button = new Button(8, totalHeight, 256 - 8 - 16, 12, name) {
                @Override
                protected void onPressed() {
                    songEntry.setName(name);
                    selectionLabel.setText(name);
                    refreshGui();
                }
            };
            area.addGuiElement(button);
            totalHeight += 12;
        }
        area.setAreaSize(totalHeight);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        return false;
    }
}
