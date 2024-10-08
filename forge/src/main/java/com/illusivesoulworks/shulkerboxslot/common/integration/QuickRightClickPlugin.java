package com.illusivesoulworks.shulkerboxslot.common.integration;

import com.natamus.quickrightclick_common_forge.config.ConfigHandler;

public class QuickRightClickPlugin {

  public static boolean checkQuickRightClick() {
    return ConfigHandler.enableQuickShulkerBoxes;
  }
}
