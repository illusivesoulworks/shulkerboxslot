/*
 * Copyright (c) 2019-2021 C4
 *
 * This file is part of Trinket Shulker Boxes, a mod made for Minecraft.
 *
 * Trinket Shulker Boxes is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Trinket Shulker Boxes is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Trinket Shulker Boxes. If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.trinketshulkerboxes.common;

import dev.emi.trinkets.api.TrinketsApi;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.trinketshulkerboxes.common.network.TSBNetwork;

public class TrinketShulkerBoxesMod implements ModInitializer, ItemComponentInitializer {

  public static final String MOD_ID = "trinketshulkerboxes";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final ComponentKey<TrinketShulkerBoxComponent> TRINKET_SHULKER_BOX_COMPONENT =
      ComponentRegistry
          .getOrCreate(new Identifier(MOD_ID, "shulker_box"), TrinketShulkerBoxComponent.class);

  public static boolean isEnderiteLoaded = false;
  public static boolean isNetheriteLoaded = false;

  @Override
  public void onInitialize() {
    Item[] shulkerBoxes = new Item[] {Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
        Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX,
        Items.RED_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
        Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX,
        Items.LIME_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX,
        Items.PURPLE_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX};

    for (Item item : shulkerBoxes) {
      TrinketsApi.registerTrinket(item, new TrinketShulkerBox());
    }
    TSBNetwork.register();

    // Integrations
//    FabricLoader loader = FabricLoader.getInstance();
//
//    if (loader.isModLoaded("enderitemod")) {
//      isEnderiteLoaded = true;
//      EnderiteIntegration.setup();
//    }
//
//    if (loader.isModLoaded("netherite_plus")) {
//      isNetheriteLoaded = true;
//      NetheriteIntegration.setup();
//    }
  }

  @Override
  public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
    Item[] shulkerBoxes = new Item[] {Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
        Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX,
        Items.RED_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX,
        Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX,
        Items.LIME_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX,
        Items.PURPLE_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX};

    for (Item item : shulkerBoxes) {
      registry.register(item, TRINKET_SHULKER_BOX_COMPONENT, TrinketShulkerBoxComponent::new);
    }
  }
}
