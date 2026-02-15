package com.alfred.firstperson.mixin;

import net.minecraft.client.render.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderer.class, remap = false)
public abstract class ItemRendererMixin {
	@Inject(method = "renderItemInFirstPerson", at = @At("HEAD"), cancellable = true)
	private void STOP(float partialTick, CallbackInfo ci) {
		ci.cancel();
	}
}
