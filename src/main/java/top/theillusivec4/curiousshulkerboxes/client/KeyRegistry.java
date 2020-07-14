package top.theillusivec4.curiousshulkerboxes.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyRegistry {

  public static final String CONFIG_OPEN_DESC = "key.curiousshulkerboxes.open.desc";
  public static final String CONFIG_CATEGORY = "key.curiousshulkerboxes.category";

  public static KeyBinding openShulkerBox;

  public static void registerKeys() {
    openShulkerBox = registerKeybinding(
        new KeyBinding(CONFIG_OPEN_DESC, GLFW.GLFW_KEY_X, CONFIG_CATEGORY));
  }

  private static KeyBinding registerKeybinding(KeyBinding key) {
    KeyBindingHelper.registerKeyBinding(key);
    return key;
  }
}
