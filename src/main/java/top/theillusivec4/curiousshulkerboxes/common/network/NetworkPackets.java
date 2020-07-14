package top.theillusivec4.curiousshulkerboxes.common.network;

import net.minecraft.util.Identifier;
import top.theillusivec4.curiousshulkerboxes.common.CuriousShulkerBoxesCommon;

public class NetworkPackets {

  public static final Identifier OPEN_SHULKER_BOX = new Identifier(CuriousShulkerBoxesCommon.MODID,
      "open_shulker_box");
  public static final Identifier OPENING_BOX = new Identifier(CuriousShulkerBoxesCommon.MODID,
      "opening_box");
  public static final Identifier CLOSING_BOX = new Identifier(CuriousShulkerBoxesCommon.MODID,
      "closing_box");
}
