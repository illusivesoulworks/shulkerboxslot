package com.illusivesoulworks.shulkerboxslot;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;

public class ShulkerBoxSlotConfigInitializer implements SpectreLibInitializer {

  @Override
  public void onInitializeConfig() {
    ShulkerBoxSlotConfig.setup();
  }
}
