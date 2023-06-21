package com.illusivesoulworks.shulkerboxslot;

import com.illusivesoulworks.spectrelib.config.SpectreConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigLoader;
import com.illusivesoulworks.spectrelib.config.SpectreConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ShulkerBoxSlotConfig {

  public static final SpectreConfigSpec SERVER_SPEC;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "gui." + ShulkerBoxSlotConstants.MOD_ID + ".config.";

  static {
    final Pair<Server, SpectreConfigSpec> specPairServer = new SpectreConfigSpec.Builder()
        .configure(Server::new);
    SERVER_SPEC = specPairServer.getRight();
    SERVER = specPairServer.getLeft();
  }

  public static class Server {
    public final SpectreConfigSpec.BooleanValue renderShulkerBox;

    public Server(SpectreConfigSpec.Builder builder) {
      renderShulkerBox = builder.comment("If enabled, renders the equipped shulker box on players.")
          .translation(CONFIG_PREFIX + "renderShulkerBox")
          .define("renderShulkerBox", true);
    }
  }

  public static void setup() {
    SpectreConfigLoader.add(SpectreConfig.Type.SERVER, SERVER_SPEC, ShulkerBoxSlotConstants.MOD_ID);
  }
}
