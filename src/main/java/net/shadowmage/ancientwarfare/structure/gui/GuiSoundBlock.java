package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.core.util.SongPlayData.SongEntry;
import net.shadowmage.ancientwarfare.structure.container.ContainerSoundBlock;

public class GuiSoundBlock extends GuiContainerBase<ContainerSoundBlock> {

    CompositeScrolled area;

    public GuiSoundBlock(ContainerBase container) {
        super(container);
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 0, xSize, ySize);
    }

    @Override
    public void setupElements() {
        clearElements();
        area.clearElements();
        addGuiElement(area);

        int totalHeight = 8;
        final SongPlayData data = getContainer().data;

        Checkbox playerEntry = new Checkbox(8, totalHeight, 16, 16, "guistrings.play_on_player_entry") {
            @Override
            public void onToggled() {
                data.setPlayOnPlayerEntry(checked());
            }
        };
        playerEntry.setChecked(data.getPlayOnPlayerEntry());
        area.addGuiElement(playerEntry);
        totalHeight += 16;

        Checkbox random = new Checkbox(8, totalHeight, 16, 16, "guistrings.random") {
            @Override
            public void onToggled() {
                data.setRandom(checked());
            }
        };
        random.setChecked(data.getIsRandom());
        area.addGuiElement(random);

        Checkbox redstone = new Checkbox(128, totalHeight, 16, 16, "guistrings.redstone") {
            @Override
            public void onToggled() {
                getContainer().redstoneInteraction = checked();
            }
        };
        redstone.setChecked(getContainer().redstoneInteraction);
        area.addGuiElement(redstone);
        totalHeight += 16;

        NumberInput minDelay = new NumberInput(88, totalHeight, 55, data.getMinDelay(), this) {
            @Override
            public void onValueUpdated(float value) {
                data.setMinDelay((int) value);
            }
        };
        minDelay.setIntegerValue();
        area.addGuiElement(minDelay);
        area.addGuiElement(new Label(8, totalHeight + 1, "guistrings.min_delay"));
        totalHeight += 12;

        NumberInput maxDelay = new NumberInput(88, totalHeight, 55, data.getMaxDelay(), this) {
            @Override
            public void onValueUpdated(float value) {
                data.setMaxDelay((int) value);
            }
        };
        maxDelay.setIntegerValue();
        area.addGuiElement(maxDelay);
        area.addGuiElement(new Label(8, totalHeight + 1, "guistrings.max_delay"));
        totalHeight += 12;

        area.addGuiElement(new Line(0, totalHeight + 2, xSize, totalHeight + 2, 1, 0x000000ff));
        totalHeight += 5;

        totalHeight = addTuneEntries(data, totalHeight);

        Button newTuneButton = new Button(8, totalHeight, 120, 12, "guistrings.new_tune") {
            @Override
            protected void onPressed() {
                data.addNewEntry();
                refreshGui();
            }
        };
        area.addGuiElement(newTuneButton);
        totalHeight += 12;

        area.setAreaSize(totalHeight);
    }

    private int addTuneEntries(final SongPlayData data, int startHeight) {
        for (int i = 0; i < data.size(); i++) {
            startHeight = addTuneEntry(data.get(i), i, startHeight);
        }
        return startHeight;
    }

    private int addTuneEntry(final SongEntry entry, final int index, int startHeight) {
        int y = startHeight;
        Text input = new Text(8, startHeight, 120, entry.name(), this) {
            @Override
            public void onTextUpdated(String oldText, String newText) {
                super.onTextUpdated(oldText, newText);
                entry.setName(newText);
            }
        };
        area.addGuiElement(input);
        startHeight += 12;

        area.addGuiElement(new Label(8, startHeight + 1, "guistrings.length"));
        NumberInput length = new NumberInput(88, startHeight, 60, entry.length(), this) {
            @Override
            public void onValueUpdated(float value) {
                entry.setLength(value);
            }
        };
        area.addGuiElement(length);
        startHeight += 12;

        area.addGuiElement(new Label(8, startHeight + 1, "guistrings.volume"));
        NumberInput volume = new NumberInput(88, startHeight, 60, entry.volume(), this) {
            @Override
            public void onValueUpdated(float value) {
                entry.setVolume((int) value);
            }
        };
        area.addGuiElement(volume);
        startHeight += 12;

        area.addGuiElement(new Button(160, y, 55, 12, "guistrings.up") {
            @Override
            protected void onPressed() {
                final SongPlayData data = getContainer().data;
                data.decrementEntry(index);
                refreshGui();
            }
        });

        area.addGuiElement(new Button(160, y + 12, 55, 12, "guistrings.delete") {
            @Override
            protected void onPressed() {
                final SongPlayData data = getContainer().data;
                data.deleteEntry(index);
                refreshGui();
            }
        });

        area.addGuiElement(new Button(160, y + 24, 55, 12, "guistrings.down") {
            @Override
            protected void onPressed() {
                final SongPlayData data = getContainer().data;
                data.incrementEntry(index);
                refreshGui();
            }
        });

        area.addGuiElement(new Line(0, startHeight + 2, xSize, startHeight + 2, 1, 0x000000ff));
        startHeight += 5;
        return startHeight;
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().sendTuneDataToServer(player);
        return true;
    }

}
