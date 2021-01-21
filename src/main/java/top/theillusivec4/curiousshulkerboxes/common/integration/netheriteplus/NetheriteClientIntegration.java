package top.theillusivec4.curiousshulkerboxes.common.integration.netheriteplus;

import com.oroarmor.netherite_plus.block.NetheriteShulkerBoxBlock;
import com.oroarmor.netherite_plus.client.NetheritePlusTextures;
import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosComponent;
import top.theillusivec4.curios.api.type.component.IRenderableCurio;
import top.theillusivec4.curiousshulkerboxes.common.CurioShulkerBox;

public class NetheriteClientIntegration {

  public static void setup() {
    for (Item item : Registry.ITEM) {

      if (item instanceof BlockItem &&
          NetheriteIntegration.isNetheriteShulkerBox(((BlockItem) item).getBlock())) {
        registerRenderComponent(item);
      }
    }
    RegistryEntryAddedCallback.event(Registry.ITEM).register((rawId, id, item) -> {

      if (item instanceof BlockItem &&
          NetheriteIntegration.isNetheriteShulkerBox(((BlockItem) item).getBlock())) {
        registerRenderComponent(item);
      }
    });
  }

  private static void registerRenderComponent(Item item) {
    ItemComponentCallbackV2.event(item).register(((item1, stack, components) -> components
        .put(CuriosComponent.ITEM_RENDER, new IRenderableCurio() {
          ShulkerEntityModel<?> model = new ShulkerEntityModel<>();

          @Override
          public void render(String identifier, int index, MatrixStack matrixStack,
                             VertexConsumerProvider vertexConsumerProvider, int light,
                             LivingEntity livingEntity, float limbSwing, float limbSwingAmount,
                             float partialTicks, float ageInTicks, float netHeadYaw,
                             float headPitch) {
            CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> {

              if (curio instanceof CurioShulkerBox) {
                CurioShulkerBox curioShulkerBox = (CurioShulkerBox) curio;
                Direction direction = Direction.SOUTH;
                DyeColor color = NetheriteShulkerBoxBlock.getColor(stack.getItem());
                SpriteIdentifier spriteIdentifier;

                if (color == null) {
                  spriteIdentifier =
                      new SpriteIdentifier(NetheritePlusTextures.SHULKER_BOXES_ATLAS_TEXTURE,
                          new Identifier(
                              "netherite_plus:entity/netherite_shulker/netherite_shulker"));
                } else {
                  spriteIdentifier =
                      new SpriteIdentifier(NetheritePlusTextures.SHULKER_BOXES_ATLAS_TEXTURE,
                          new Identifier(
                              "netherite_plus:entity/netherite_shulker/netherite_shulker_" +
                                  color.getName()));
                }
                IRenderableCurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
                IRenderableCurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
                matrixStack.push();
                matrixStack.translate(0.5D, 0.5D, 0.5D);
                float f = 0.45F;
                matrixStack.scale(f, f, f);
                matrixStack.multiply(direction.getRotationQuaternion());
                matrixStack.scale(1.0F, -1.0F, -1.0F);
                matrixStack.translate(-1.1125D, -0.675D, -0.5D);
                VertexConsumer vertexConsumer = spriteIdentifier
                    .getVertexConsumer(vertexConsumerProvider,
                        RenderLayer::getEntityCutoutNoCull);
                model.getBottomShell()
                    .render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                matrixStack
                    .translate(0.0D, (-curioShulkerBox.getProgress(partialTicks) * 0.5F), 0.0D);
                matrixStack.multiply(Vector3f.POSITIVE_Y
                    .getDegreesQuaternion(270.0F * curioShulkerBox.getProgress(partialTicks)));
                model.getTopShell()
                    .render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
                matrixStack.pop();
              }
            });
          }
        })));
  }
}
