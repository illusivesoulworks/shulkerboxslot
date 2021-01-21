package top.theillusivec4.curiousshulkerboxes.common.integration.netherite_plus;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.oroarmor.netherite_plus.block.NetheriteShulkerBoxBlock;
import com.oroarmor.netherite_plus.client.NetheritePlusTextures;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curiousshulkerboxes.common.capability.CurioShulkerBox;

public class CurioNetheriteShulkerBox extends CurioShulkerBox {

  public CurioNetheriteShulkerBox(ItemStack stack) {
    super(stack);
  }

  @Override
  public void render(String identifier, int index, MatrixStack matrixStack,
                     IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity,
                     float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                     float netHeadYaw, float headPitch) {
    Direction direction = Direction.SOUTH;
    ICurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
    ICurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
    DyeColor color = NetheriteShulkerBoxBlock.getColor(stack.getItem());
    RenderMaterial material;

    if (color == null) {
      material =
          new RenderMaterial(NetheritePlusTextures.SHULKER_BOXES_ATLAS_TEXTURE,
              new ResourceLocation("netherite_plus:entity/netherite_shulker/netherite_shulker"));
    } else {
      material = new RenderMaterial(NetheritePlusTextures.SHULKER_BOXES_ATLAS_TEXTURE,
          new ResourceLocation(
              "netherite_plus:entity/netherite_shulker/netherite_shulker_" + color.getString()));
    }

    if (!(this.model instanceof ShulkerModel)) {
      this.model = new ShulkerModel<>();
    }
    ShulkerModel<?> model = (ShulkerModel<?>) this.model;
    matrixStack.push();
    matrixStack.translate(0.5D, 0.5D, 0.5D);
    float f = 0.45F;
    matrixStack.scale(f, f, f);
    matrixStack.rotate(direction.getRotation());
    matrixStack.scale(1.0F, -1.0F, -1.0F);
    matrixStack.translate(-1.1125D, -0.675D, -0.5D);
    IVertexBuilder ivertexbuilder = material
        .getBuffer(renderTypeBuffer, RenderType::getEntityCutoutNoCull);
    model.getBase().render(matrixStack, ivertexbuilder, light, OverlayTexture.NO_OVERLAY);
    matrixStack.translate(0.0D, -this.getProgress(partialTicks) * 0.5F, 0.0D);
    matrixStack.rotate(Vector3f.YP.rotationDegrees(270.0F * this.getProgress(partialTicks)));
    model.getLid().render(matrixStack, ivertexbuilder, light, OverlayTexture.NO_OVERLAY);
    matrixStack.pop();
  }
}
