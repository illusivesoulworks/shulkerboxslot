package com.illusivesoulworks.shulkerboxslot;

import com.illusivesoulworks.spectrelib.config.SpectreConfigInitializer;

public class ShulkerBoxSlotConfigInitializer implements SpectreConfigInitializer {

  @Override
  public void onInitializeConfig() {
    ShulkerBoxSlotConfig.setup();
  }
}
