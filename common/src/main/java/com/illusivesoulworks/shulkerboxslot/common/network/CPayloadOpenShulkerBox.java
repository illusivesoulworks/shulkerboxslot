package com.illusivesoulworks.shulkerboxslot.common.network;

import com.illusivesoulworks.shulkerboxslot.ShulkerBoxAccessoryInventory;
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotConstants;
import com.illusivesoulworks.shulkerboxslot.platform.Services;
import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

public record CPayloadOpenShulkerBox() implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<CPayloadOpenShulkerBox> TYPE =
      new CustomPacketPayload.Type<>(
          ResourceLocation.fromNamespaceAndPath(ShulkerBoxSlotConstants.MOD_ID,
              "open_shulker_box"));

  public static final CPayloadOpenShulkerBox INSTANCE = new CPayloadOpenShulkerBox();

  public static final StreamCodec<FriendlyByteBuf, CPayloadOpenShulkerBox> STREAM_CODEC =
      StreamCodec.unit(INSTANCE);

  @Nonnull
  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(ServerPlayer player) {
    player.awardStat(Stats.OPEN_SHULKER_BOX);
    Triple<ItemStack, String, Integer> accessory =
        Services.INSTANCE.findShulkerBoxAccessory(player);
    ItemStack stack = accessory.getLeft();

    if (!stack.isEmpty()) {
      String identifier = accessory.getMiddle();
      int index = accessory.getRight();
      MenuProvider container = new ShulkerBoxAccessoryInventory(stack, identifier, index);
      player.openMenu(container);
    }
  }
}
