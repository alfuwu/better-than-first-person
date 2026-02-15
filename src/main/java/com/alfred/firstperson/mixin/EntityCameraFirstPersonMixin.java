package com.alfred.firstperson.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.camera.EntityCamera;
import net.minecraft.client.render.camera.EntityCameraFirstPerson;
import net.minecraft.core.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EntityCameraFirstPerson.class, remap = false)
public abstract class EntityCameraFirstPersonMixin extends EntityCamera {
	public EntityCameraFirstPersonMixin(Minecraft mc, Mob mob) {
		super(mc, mob);
	}

	@Override
	public boolean showPlayer() {
		return true;
	}
}
