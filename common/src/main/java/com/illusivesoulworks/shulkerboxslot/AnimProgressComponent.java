package com.illusivesoulworks.shulkerboxslot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

public class AnimProgressComponent {

  public static final Codec<AnimProgressComponent> CODEC = RecordCodecBuilder.create(instance ->
      instance.group(
          Codec.INT.fieldOf("status").forGetter(s -> s.status.ordinal()),
          Codec.FLOAT.fieldOf("progress").forGetter(s -> s.progress),
          Codec.FLOAT.fieldOf("oldProgress").forGetter(s -> s.oldProgress)
      ).apply(instance, AnimProgressComponent::new)
  );
  public static final StreamCodec<ByteBuf, AnimProgressComponent> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.INT, s -> s.status.ordinal(),
          ByteBufCodecs.FLOAT, s -> s.progress,
          ByteBufCodecs.FLOAT, s -> s.oldProgress,
          AnimProgressComponent::new
      );

  private ShulkerBoxBlockEntity.AnimationStatus status;
  private float progress;
  private float oldProgress;

  public AnimProgressComponent() {
    this.status = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
    this.progress = 0.0f;
    this.oldProgress = 0.0f;
  }

  public AnimProgressComponent(int status, float progress, float oldProgress) {
    this.status = ShulkerBoxBlockEntity.AnimationStatus.values()[status];
    this.progress = progress;
    this.oldProgress = oldProgress;
  }

  public float getProgress(float partialTicks) {
    return Mth.lerp(partialTicks, this.oldProgress, this.progress);
  }

  public void setStatus(ShulkerBoxBlockEntity.AnimationStatus status) {
    this.status = status;
  }

  public void tick() {
    this.oldProgress = this.progress;

    switch (this.status) {
      case CLOSED -> this.progress = 0.0F;
      case OPENING -> {
        this.progress += 0.1F;
        if (this.progress >= 1.0F) {
          this.status = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
          this.progress = 1.0F;
        }
      }
      case CLOSING -> {
        this.progress -= 0.1F;
        if (this.progress <= 0.0F) {
          this.status = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
          this.progress = 0.0F;
        }
      }
      case OPENED -> this.progress = 1.0F;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnimProgressComponent that = (AnimProgressComponent) o;
    return Float.compare(progress, that.progress) == 0 &&
        Float.compare(oldProgress, that.oldProgress) == 0 && status == that.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, progress, oldProgress);
  }
}
