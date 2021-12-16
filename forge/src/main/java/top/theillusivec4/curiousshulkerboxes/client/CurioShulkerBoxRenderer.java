package top.theillusivec4.curiousshulkerboxes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;

public class CurioShulkerBoxRenderer implements ICurioRenderer {

  private ShulkerModel<?> model;

  @Override
  public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack,
                                                                        SlotContext slotContext,
                                                                        PoseStack matrixStack,
                                                                        RenderLayerParent<T, M> renderLayerParent,
                                                                        MultiBufferSource renderTypeBuffer,
                                                                        int light, float limbSwing,
                                                                        float limbSwingAmount,
                                                                        float partialTicks,
                                                                        float ageInTicks,
                                                                        float netHeadYaw,
                                                                        float headPitch) {
    CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> {

      if (curio instanceof CurioShulkerBox curioShulkerBox) {
        LivingEntity livingEntity = slotContext.entity();
        Direction direction = Direction.SOUTH;
        DyeColor color = ShulkerBoxBlock.getColorFromItem(stack.getItem());
        Material material;

        if (color == null) {
          material = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
        } else {
          material = Sheets.SHULKER_TEXTURE_LOCATION.get(color.getId());
        }
        ICurioRenderer.translateIfSneaking(matrixStack, livingEntity);
        ICurioRenderer.rotateIfSneaking(matrixStack, livingEntity);
        matrixStack.pushPose();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        float f = 0.45F;
        matrixStack.scale(f, f, f);
        matrixStack.mulPose(direction.getRotation());
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        matrixStack.translate(-1.1125D, -0.675D, -0.5D);
        VertexConsumer ivertexbuilder = material
            .buffer(renderTypeBuffer, RenderType::entityCutoutNoCull);

        if (this.model == null) {
          this.model = new ShulkerModel<>(
              Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHULKER));
        }
        ModelPart modelpart = this.model.getLid();
        modelpart.setPos(0.0F, 24.0F - curioShulkerBox.getProgress(partialTicks) * 0.5F * 16.0F,
            0.0F);
        modelpart.yRot =
            270.0F * curioShulkerBox.getProgress(partialTicks) * ((float) Math.PI / 180F);
        this.model.renderToBuffer(matrixStack, ivertexbuilder, light, OverlayTexture.NO_OVERLAY,
            1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
      }
    });
  }
}
