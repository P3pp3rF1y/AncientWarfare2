package net.shadowmage.ancientwarfare.npc.container;

import net.shadowmage.ancientwarfare.npc.skin.NpcSkinSettings;

public interface ISkinSettingsContainer {
	void handleNpcSkinUpdate();

	NpcSkinSettings getSkinSettings();

	void setSkinSettings(NpcSkinSettings skinSettings);
}
