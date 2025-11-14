package net.mellow.emimo;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;

public class KeyBinds {

    public static final Lazy<KeyMapping> PULL_ITEMS = Lazy.of(() -> new KeyMapping("key.emimo.pullItems", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.emimo"));

    public static void registerBinds(RegisterKeyMappingsEvent event) {
        event.register(PULL_ITEMS.get());
    }

}
