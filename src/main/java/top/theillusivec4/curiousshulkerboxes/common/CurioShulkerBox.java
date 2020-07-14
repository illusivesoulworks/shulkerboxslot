package top.theillusivec4.curiousshulkerboxes.common;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity.AnimationStage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import top.theillusivec4.curios.api.type.component.ICurio;

public class CurioShulkerBox implements ICurio {

  private static final String ANIMATION_TAG = "Animation";
  private static final String PROGRESS_TAG = "Progress";
  private static final String OLD_PROGRESS_TAG = "OldProgress";

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
  public void curioTick(String identifier, int index, LivingEntity livingEntity) {
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
        this.animationProgress -= 1.0F;

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
  public void fromTag(CompoundTag tag) {
    int stage = tag.getInt(ANIMATION_TAG);

    switch (stage) {
      case 0:
        this.animationStage = AnimationStage.CLOSED;
        break;
      case 1:
        this.animationStage = AnimationStage.OPENING;
        break;
      case 2:
        this.animationStage = AnimationStage.CLOSING;
        break;
      case 3:
        this.animationStage = AnimationStage.OPENED;
    }
    this.animationProgress = tag.getFloat(PROGRESS_TAG);
    this.prevAnimationProgress = tag.getFloat(OLD_PROGRESS_TAG);
  }

  @Override
  public CompoundTag toTag(CompoundTag tag) {
    int stage = 0;

    switch (this.animationStage) {
      case OPENING:
        stage = 1;
        break;
      case CLOSING:
        stage = 2;
        break;
      case OPENED:
        stage = 3;
    }
    tag.putInt(ANIMATION_TAG, stage);
    tag.putFloat(PROGRESS_TAG, this.animationProgress);
    tag.putFloat(OLD_PROGRESS_TAG, this.prevAnimationProgress);
    return tag;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CurioShulkerBox that = (CurioShulkerBox) o;
    return this.stack.getItem() == that.stack.getItem()
        && Float.compare(that.animationProgress, animationProgress) == 0
        && Float.compare(that.prevAnimationProgress, prevAnimationProgress) == 0
        && animationStage == that.animationStage;
  }
}
