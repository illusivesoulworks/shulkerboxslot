package com.illusivesoulworks.shulkerboxslot.common.integration.reinfshulker;

import atonkish.reinfcore.util.ReinforcingMaterial;
import atonkish.reinfshulker.block.ReinforcedShulkerBoxBlock;
import atonkish.reinfshulker.client.render.ModTexturedRenderLayers;
import com.illusivesoulworks.shulkerboxslot.ShulkerBoxSlotQuiltMod;
import com.illusivesoulworks.shulkerboxslot.client.ShulkerBoxRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ReinfShulkerClientPlugin {

  public static void onInitialize() {

    for (Item shulkerBox : ReinfShulkerPlugin.REINF_SHULKERS) {
      TrinketRendererRegistry.registerRenderer(shulkerBox, new ReinfShulkerBoxRenderer());
    }
  }

  public static class ReinfShulkerBoxRenderer implements TrinketRenderer {

    @Override
    public void render(ItemStack stack, SlotReference slotReference,
                       EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int i, LivingEntity livingEntity,
                       float v,
                       float v1, float v2, float v3, float v4, float v5) {
      ShulkerBoxSlotQuiltMod.getShulkerBoxComponent(stack).ifPresent(component -> {

        if (livingEntity.isCrouching() && !entityModel.riding && !livingEntity.isSwimming()) {
          poseStack.translate(0.0F, 0.2F, 0.0F);

          if (entityModel instanceof HumanoidModel bipedEntityModel) {
            poseStack.mulPose(Axis.XP.rotation(bipedEntityModel.body.xRot));
          }
        }
        DyeColor color = ReinforcedShulkerBoxBlock.getColorFromItem(stack.getItem());
        ReinforcingMaterial material = null;

        if (stack.getItem() instanceof BlockItem blockItem &&
            blockItem.getBlock() instanceof ReinforcedShulkerBoxBlock reinforcedShulkerBoxBlock) {
          material = reinforcedShulkerBoxBlock.getMaterial();
        }

        if (material == null) {
          return;
        }
        Material spriteIdentifier2;

        if (color == null) {
          spriteIdentifier2 =
              ModTexturedRenderLayers.REINFORCED_SHULKER_TEXTURE_ID_MAP.get(material);
        } else {
          spriteIdentifier2 =
              ModTexturedRenderLayers.COLORED_REINFORCED_SHULKER_BOXES_TEXTURES_MAP.get(material)
                  .get(color.getId());
        }
        ShulkerBoxRenderer.render(poseStack, multiBufferSource, i, v2, spriteIdentifier2,
            livingEntity, component.getShulkerBoxAccessory(), stack);
      });
    }
  }
}
