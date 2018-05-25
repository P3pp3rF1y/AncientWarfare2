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

package net.shadowmage.ancientwarfare.core.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/*
 * static-data configuration class.
 * Each mod will need to construct its own subclass of this, adding static fields for necessary config items
 *
 * @author Shadowmage
 */
public abstract class ModConfiguration {
	/*
	 * category names
	 */
	public static final String generalOptions = "01_shared_settings";
	public static final String serverOptions = "02_server_settings";
	public static final String clientOptions = "03_client_settings";
	public static final String configPathForFiles = "config/ancientwarfare/";
	protected final Configuration config;
	public boolean updatedVersion = false;
	public boolean autoExportOnUpdate = false;

	public ModConfiguration(Configuration config) {
		this.config = config;
		load();
	}

	public ModConfiguration(String modid) {
		this(getConfigFor(modid));
	}

	private void load() {
		initializeCategories();
		initializeValues();
		save();
	}

	protected abstract void initializeCategories();

	protected abstract void initializeValues();

	public Configuration getConfig() {
		return this.config;
	}

	public void save() {
		//TODO remove when statics moved to config annotations
		if (config.hasChanged())
			config.save();
	}

	public boolean updatedVersion() {
		return updatedVersion;
	}

	public boolean autoExportOnUpdate() {
		return autoExportOnUpdate;
	}

	public static Configuration getConfigFor(String modID) {
		return new Configuration(new File(configPathForFiles, modID + ".cfg"));
	}
}
