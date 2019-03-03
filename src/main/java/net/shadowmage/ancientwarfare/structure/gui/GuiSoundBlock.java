package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.util.SongPlayData.SongEntry;
import net.shadowmage.ancientwarfare.structure.container.ContainerSoundBlock;
import net.shadowmage.ancientwarfare.structure.util.BlockSongPlayData;

public class GuiSoundBlock extends GuiContainerBase<ContainerSoundBlock> {

	private CompositeScrolled area;
	private Checkbox playerEntry;
	private Checkbox loop;
	private Checkbox anyTime;
	private Checkbox day;
	private Checkbox night;

	public GuiSoundBlock(ContainerBase container) {
		super(container, 320, 240);
	}

	@Override
	public void initElements() {
		//noop
	}

	@Override
	public void setupElements() {
		clearElements();

		int totalHeight = 8;
		BlockSongPlayData data = getContainer().data;

		playerEntry = new Checkbox(8, totalHeight, 12, 12, "guistrings.play_on_player_entry") {
			@Override
			public void onToggled() {
				data.setPlayOnPlayerEntry(checked());
				loop.setChecked(!checked());
				refreshGui();
			}
		};
		playerEntry.setChecked(data.getPlayOnPlayerEntry());
		addGuiElement(playerEntry);
		totalHeight += 16;

		if (playerEntry.checked()) {
			totalHeight = addPlayerEntryElements(totalHeight);
		}

		loop = new Checkbox(8, totalHeight, 12, 12, "guistrings.sound_block.loop") {
			@Override
			public void onToggled() {
				data.setPlayOnPlayerEntry(!checked());
				playerEntry.setChecked(!checked());
				refreshGui();
			}
		};
		loop.setChecked(!data.getPlayOnPlayerEntry());
		addGuiElement(loop);
		totalHeight += 16;

		if (loop.checked()) {
			totalHeight = addLoopElements(totalHeight);
		}

		totalHeight += 4;

		anyTime = new Checkbox(10, totalHeight, 12, 12, "guistrings.sound_block.any_time") {
			@Override
			public void onToggled() {
				if (checked()) {
					data.setTimeOfDay(BlockSongPlayData.TimeOfDay.ANY);
					day.setChecked(false);
					night.setChecked(false);
				}
			}
		};
		anyTime.setChecked(data.getTimeOfDay() == BlockSongPlayData.TimeOfDay.ANY);
		addGuiElement(anyTime);

		day = new Checkbox(74, totalHeight, 12, 12, "guistrings.sound_block.day") {
			@Override
			public void onToggled() {
				if (checked()) {
					data.setTimeOfDay(BlockSongPlayData.TimeOfDay.DAY);
					anyTime.setChecked(false);
					night.setChecked(false);
				}
			}
		};
		day.setChecked(data.getTimeOfDay() == BlockSongPlayData.TimeOfDay.DAY);
		addGuiElement(day);

		night = new Checkbox(122, totalHeight, 12, 12, "guistrings.sound_block.night") {
			@Override
			public void onToggled() {
				if (checked()) {
					data.setTimeOfDay(BlockSongPlayData.TimeOfDay.NIGHT);
					anyTime.setChecked(false);
					day.setChecked(false);
				}
			}
		};
		night.setChecked(data.getTimeOfDay() == BlockSongPlayData.TimeOfDay.NIGHT);
		addGuiElement(night);
		totalHeight += 14;

		Checkbox protectionFlagTurnOff = new Checkbox(8, totalHeight, 16, 16, "guistrings.sound_block.turned_off_by_protection_flag") {
			@Override
			public void onToggled() {
				data.setProtectionFlagTurnOff(checked());
			}
		};
		protectionFlagTurnOff.setChecked(data.getProtectionFlagTurnOff());
		addGuiElement(protectionFlagTurnOff);

		totalHeight += 18;

		Checkbox random = new Checkbox(8, totalHeight, 16, 16, "guistrings.sound_block.play_in_random_order") {
			@Override
			public void onToggled() {
				data.setRandom(checked());
			}
		};
		random.setChecked(data.getIsRandom());
		addGuiElement(random);

		totalHeight += 20;

		addGuiElement(new Label(32, totalHeight + 1, "guistrings.sound_block.sound_range").setShadow(true));

		NumberInput soundRange = new NumberInput(8, totalHeight, 22, data.getSoundRange(), this) {
			@Override
			public void onValueUpdated(float value) {
				data.setSoundRange((int) value);
			}
		};
		soundRange.setIntegerValue();
		addGuiElement(soundRange);
		totalHeight += 14;

		int areaHeight = addTuneEntries(data, totalHeight);

		Button newTuneButton = new Button(8, areaHeight, 120, 12, "guistrings.new_tune") {
			@Override
			protected void onPressed() {
				data.addNewEntry();
				refreshGui();
			}
		};
		area.addGuiElement(newTuneButton);
		areaHeight += 16;

		area.setAreaSize(areaHeight);
	}

	private int addLoopElements(int totalHeight) {
		totalHeight += 2;

		BlockSongPlayData data = getContainer().data;

		NumberInput minDelay = new NumberInput(74, totalHeight, 28, data.getMinDelay(), this) {
			@Override
			public void onValueUpdated(float value) {
				data.setMinDelay((int) value);
			}
		};
		minDelay.setIntegerValue();
		addGuiElement(minDelay);
		addGuiElement(new Label(24, totalHeight + 1, "guistrings.min_delay").setShadow(true));

		NumberInput maxDelay = new NumberInput(170, totalHeight, 28, data.getMaxDelay(), this) {
			@Override
			public void onValueUpdated(float value) {
				data.setMaxDelay((int) value);
			}
		};
		maxDelay.setIntegerValue();
		addGuiElement(maxDelay);
		addGuiElement(new Label(116, totalHeight + 1, "guistrings.max_delay").setShadow(true));
		totalHeight += 14;

		NumberInput repetitions = new NumberInput(100, totalHeight + 2, 22, data.getRepetitions(), this) {
			@Override
			public void onValueUpdated(float value) {
				data.setRepetitions((int) value);
			}
		};
		repetitions.setIntegerValue();
		repetitions.setEnabled(data.getLimitedRepetitions());
		addGuiElement(repetitions);

		Checkbox limitedRepetitions = new Checkbox(24, totalHeight, 16, 16, "guistrings.sound_block.stop_after") {
			@Override
			public void onToggled() {
				data.setLimitedRepetitions(checked());
				repetitions.setEnabled(checked());
			}
		};
		limitedRepetitions.setChecked(data.getLimitedRepetitions());
		addGuiElement(limitedRepetitions);
		addGuiElement(new Label(128, totalHeight + 4, "guistrings.sound_block.repetitions").setShadow(true));
		totalHeight += 18;

		NumberInput limitRange = new NumberInput(184, totalHeight + 2, 22, data.getPlayerRange(), this) {
			@Override
			public void onValueUpdated(float value) {
				data.setPlayerRange((int) value);
			}
		};
		limitRange.setIntegerValue();
		limitRange.setEnabled(data.getWhenInRange());
		addGuiElement(limitRange);

		Checkbox whenInRange = new Checkbox(24, totalHeight, 16, 16, "guistrings.sound_block.play_when_player_less_than") {
			@Override
			public void onToggled() {
				data.setWhenInRange(checked());
				limitRange.setEnabled(checked());
			}
		};
		whenInRange.setChecked(data.getWhenInRange());
		addGuiElement(whenInRange);
		addGuiElement(new Label(210, totalHeight + 4, "guistrings.sound_block.blocks_away").setShadow(true));
		totalHeight += 18;

		return totalHeight;
	}

	private int addPlayerEntryElements(int totalHeight) {
		totalHeight += 2;

		BlockSongPlayData data = getContainer().data;

		addGuiElement(new Label(24, totalHeight + 1, "guistrings.sound_block.range").setShadow(true));
		NumberInput playerEntryRange = new NumberInput(60, totalHeight, 22, data.getPlayerRange(), this) {
			@Override
			public void onValueUpdated(float value) {
				data.setPlayerRange((int) value);
			}
		};
		playerEntryRange.setIntegerValue();
		addGuiElement(playerEntryRange);

		Checkbox onlyOnce = new Checkbox(100, totalHeight - 2, 16, 16, "guistrings.sound_block.only_once") {
			@Override
			public void onToggled() {
				data.setPlayOnce(checked());
			}
		};
		onlyOnce.setChecked(data.getPlayOnce());
		addGuiElement(onlyOnce);
		totalHeight += 16;

		totalHeight += 2;

		return totalHeight;
	}

	private int addTuneEntries(final BlockSongPlayData data, int areaStartY) {
		area = new CompositeScrolled(this, 0, areaStartY, xSize, ySize - areaStartY);
		addGuiElement(area);

		int areaHeight = 4;
		for (int i = 0; i < data.size(); i++) {
			areaHeight = addTuneEntry(data.get(i), i, areaHeight);
		}
		return areaHeight;
	}

	private int addTuneEntry(final SongEntry entry, final int index, int startHeight) {
		Button input = new Button(8, startHeight, 294, 12, getShortenedName(entry.name())) {
			@Override
			public void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSoundSelect(GuiSoundBlock.this, entry));
				refreshGui();
			}
		};
		area.addGuiElement(input);
		startHeight += 14;

		area.addGuiElement(new Label(8, startHeight + 1, "guistrings.volume").setShadow(true));
		NumberInput volume = new NumberInput(50, startHeight, 22, entry.volume(), this) {
			@Override
			public void onValueUpdated(float value) {
				entry.setVolume((int) value);
			}
		};
		volume.setIntegerValue();
		area.addGuiElement(volume);

		area.addGuiElement(new Button(135, startHeight, 20, 12, "/\\") {
			@Override
			protected void onPressed() {
				final BlockSongPlayData data = getContainer().data;
				data.decrementEntry(index);
				refreshGui();
			}
		});

		area.addGuiElement(new Button(155, startHeight, 20, 12, "\\/") {
			@Override
			protected void onPressed() {
				final BlockSongPlayData data = getContainer().data;
				data.incrementEntry(index);
				refreshGui();
			}
		});

		area.addGuiElement(new Button(247, startHeight, 55, 12, "guistrings.delete") {
			@Override
			protected void onPressed() {
				final BlockSongPlayData data = getContainer().data;
				data.deleteEntry(index);
				refreshGui();
			}
		});

		startHeight += 16;

		area.addGuiElement(new Line(0, startHeight + 2, xSize, startHeight + 2, 1, 0x000000ff));
		startHeight += 5;
		return startHeight;
	}

	private String getShortenedName(String name) {
		return name.replace("ancientwarfarestructure:auto_load/", "auto:");
	}

	@Override
	protected boolean onGuiCloseRequested() {
		getContainer().sendTuneDataToServer(player);
		return true;
	}

}
