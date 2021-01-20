/*
 * Copyright (c) 2019-2020 C4
 *
 * This file is part of Curious Shulker Boxes, a mod made for Minecraft.
 *
 * Curious Shulker Boxes is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curious Shulker Boxes is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curious Shulker Boxes.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.curiousshulkerboxes.common;

import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosComponent;
import top.theillusivec4.curios.api.SlotTypeInfo.BuildScheme;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curiousshulkerboxes.common.integration.enderite.EnderiteIntegration;
import top.theillusivec4.curiousshulkerboxes.common.network.NetworkHandler;

public class CuriousShulkerBoxesCommon implements ModInitializer {

  public static final String MODID = "curiousshulkerboxes";
  public static final Logger LOGGER = LogManager.getLogger();

  public static boolean isEnderiteLoaded = false;

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

    if (FabricLoader.getInstance().isModLoaded("enderitemod")) {
      isEnderiteLoaded = true;
      EnderiteIntegration.setup();
    }
    CuriosApi.enqueueSlotType(BuildScheme.REGISTER, SlotTypePreset.BACK.getInfoBuilder().build());
    NetworkHandler.register();
  }
}
