package top.theillusivec4.curiousshulkerboxes.common;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity.AnimationStage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import top.theillusivec4.curios.api.type.component.ICurio;

public class CurioShulkerBox implements ICurio {

  protected final ItemStack stack;

  private ShulkerBoxBlockEntity.AnimationStage animationStage = AnimationStage.CLOSED;
  private float animationProgress;
  private float prevAnimationProgress;

  public CurioShulkerBox(ItemStack stack) {
    this.stack = stack;
    this.stack.getOrCreateSubTag("BlockEntityTag");
  }

  public void setAnimationStage(AnimationStage stage) {
    this.animationStage = stage;
  }

  public float getProgress(float delta) {
    return this.prevAnimationProgress
        + (this.animationProgress - this.prevAnimationProgress) * delta;
  }

  @Override
  public void curioAnimate(String identifier, int index, LivingEntity livingEntity) {
    this.prevAnimationProgress = this.animationProgress;

    switch (this.animationStage) {
      case CLOSED:
        this.animationProgress = 0.0F;
        break;
      case OPENING:
        this.animationProgress += 0.1F;

        if (this.animationProgress >= 1.0F) {
          this.animationStage = AnimationStage.OPENED;
          this.animationProgress = 1.0F;
        }
        break;
      case CLOSING:
        this.animationProgress -= 0.1F;

        if (this.animationProgress <= 0.0F) {
          this.animationStage = AnimationStage.CLOSED;
          this.animationProgress = 0.0F;
        }
        break;
      case OPENED:
        this.animationProgress = 1.0F;
    }
  }

  @Override
  public void playRightClickEquipSound(LivingEntity livingEntity) {
    livingEntity.world
        .playSound(null, livingEntity.getBlockPos(), SoundEvents.BLOCK_SHULKER_BOX_CLOSE,
            SoundCategory.NEUTRAL, 1.0F, 1.0F);
  }

  @Override
  public boolean canRightClickEquip() {
    return true;
  }
}
