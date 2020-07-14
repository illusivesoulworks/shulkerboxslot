package top.theillusivec4.curiousshulkerboxes.common;

import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosComponent;
import top.theillusivec4.curios.api.SlotTypeInfo.BuildScheme;
import top.theillusivec4.curios.api.SlotTypePreset;

public class CuriousShulkerBoxesCommon implements ModInitializer {

  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onInitialize() {
    Item[] shulkerBoxes = new Item[]{Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
        Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX,
        Items.RED_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
        Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX,
        Items.LIME_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX,
        Items.PURPLE_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX};

    for (Item item : shulkerBoxes) {
      ItemComponentCallbackV2.event(item).register(((item1, stack, components) -> components
          .put(CuriosComponent.ITEM, new CurioShulkerBox(stack))));
    }
    CuriosApi.enqueueSlotType(BuildScheme.REGISTER, SlotTypePreset.BACK.getInfoBuilder().build());
    NetworkHandler.register();
  }
}
