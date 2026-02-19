package com.alfred.firstperson.mixin;

import com.alfred.firstperson.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.camera.EntityCamera;
import net.minecraft.client.render.camera.EntityCameraFirstPerson;
import net.minecraft.client.render.camera.EntityCameraSleeping;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.MobRendererPlayer;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.client.render.model.ModelBiped;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.vehicle.EntityBoat;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MobRendererPlayer.class, remap = false)
public abstract class PlayerEntityRendererMixin extends MobRenderer<Player> {
	@Shadow private ModelBiped modelBipedMain;
	@Shadow @Final private ModelBiped modelArmorChestplate;
	@Unique public Vector3f offset = new Vector3f(0, 0, 0);
	@Unique public boolean gui;

	public PlayerEntityRendererMixin(ModelBase model, float shadowSize) {
		super(model, shadowSize);
	}

	// SURELY this would never ACTUALLY occur
	// this is yucky though there has to be a better way
	@Unique
	private static boolean isGui(double x, double y, double z, float yaw, float partialTick) {
		return x == 0 && y == 0 && z == 0 && yaw == 0 && partialTick == 1;
	}

	@Inject(method = "render(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/entity/player/Player;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/MobRenderer;render(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/entity/Mob;DDDFF)V"))
	private void hideHead(Tessellator tessellator, Player entity, double x, double y, double z, float yaw, float partialTick, CallbackInfo ci) {
		Minecraft mc = Minecraft.getMinecraft();
		if (entity == mc.thePlayer && mc.activeCamera instanceof EntityCameraFirstPerson && !gui) {
			this.modelBipedMain.head.visible = false;
			this.modelBipedMain.hair.visible = false;
		}
	}

	@Inject(method = "prepareArmor(Lnet/minecraft/core/entity/player/Player;IF)Z", at = @At("HEAD"), cancellable = true)
	private void hideHead(Player entity, int layer, float partialTick, CallbackInfoReturnable<Boolean> cir) {
		Minecraft mc = Minecraft.getMinecraft();
		if (entity == mc.thePlayer && mc.activeCamera instanceof EntityCameraFirstPerson && !gui && layer == 0)
			cir.setReturnValue(false);
	}

	@Inject(method = "render(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/entity/player/Player;DDDFF)V", at = @At("HEAD"))
	private void updateOffset(Tessellator tessellator, Player entity, double x, double y, double z, float yaw, float partialTick, CallbackInfo ci) {
		gui = isGui(x, y, z, yaw, partialTick);
		updatePositionOffset(entity, partialTick);
	}

	@ModifyVariable(
		method = "render(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/entity/player/Player;DDDFF)V",
		at = @At("HEAD"),
		ordinal = 0,
		argsOnly = true
	)
	private double modifyX(double value, Tessellator tessellator, Player player, double x, double y, double z, float yaw, float partialTick) {
		Minecraft mc = Minecraft.getMinecraft();
		if (player == mc.thePlayer && mc.activeCamera instanceof EntityCameraFirstPerson && !gui)
			value += offset.x;

		return value;
	}

	@ModifyVariable(
		method = "render(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/entity/player/Player;DDDFF)V",
		at = @At("HEAD"),
		ordinal = 1,
		argsOnly = true
	)
	private double modifyY(double value, Tessellator tessellator, Player player, double x, double y, double z, float yaw, float partialTick) {
		Minecraft mc = Minecraft.getMinecraft();
		if (player == mc.thePlayer && mc.activeCamera instanceof EntityCameraFirstPerson && !gui)
			value += offset.y;

		return value;
	}

	@ModifyVariable(
		method = "render(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/entity/player/Player;DDDFF)V",
		at = @At("HEAD"),
		ordinal = 2,
		argsOnly = true
	)
	private double modifyZ(double value, Tessellator tessellator, Player player, double x, double y, double z, float yaw, float partialTick) {
		Minecraft mc = Minecraft.getMinecraft();
		if (player == mc.thePlayer && mc.activeCamera instanceof EntityCameraFirstPerson && !gui)
			value += offset.z;

		return value;
	}

	// might cause issues with other mods? idk
	@Inject(method = "postRender(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/entity/player/Player;DDDFF)V", at = @At("HEAD"))
	private void unhide(Tessellator tessellator, Player entity, double x, double y, double z, float yaw, float partialTick, CallbackInfo ci) {
		Minecraft mc = Minecraft.getMinecraft();
		if (entity == mc.thePlayer) {
			this.modelBipedMain.head.visible = true;
			this.modelBipedMain.hair.visible = true;
		}
	}

/*
MIT License

Copyright (c) 2019 tr7zw

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

	/**
	 * Calculates the X/Z offset applied to the player model to get it relative to
	 * the vanilla camera position
	 */
	@Unique
	public void updatePositionOffset(Entity entity, float delta) {
		Minecraft mc = Minecraft.getMinecraft();
		// handle sleeping
		if (mc.activeCamera instanceof EntityCamera cam && cam.mob == entity && mc.thePlayer.isPlayerSleeping())
			return;

		double x = 0;
		double y = 0;
		double z = 0;
		double realYaw;
		if (!(mc.activeCamera instanceof EntityCameraFirstPerson) && !(mc.activeCamera instanceof EntityCameraSleeping))
			return;

		EntityCamera camera = (EntityCamera)mc.activeCamera;

		if (entity instanceof Mob player) {
			realYaw = rotLerp(delta, player.yBodyRotO, player.yBodyRot);
			if (camera.mob == player) {
				float bodyOffset;
				if (player.isSneaking()) {
					bodyOffset = Constants.SNEAK_BODY_OFFSET;
				} else if (player.isPassenger()) {
					if (player.vehicle instanceof EntityBoat || player.vehicle instanceof EntityMinecart)
						realYaw = rotLerp(delta, player.yBodyRotO, player.yBodyRot);
					else if (player.vehicle instanceof Mob living)
						realYaw = calculateBodyRot(rotLerp(delta, living.yBodyRotO, living.yBodyRot),
							player.yBodyRot);
					bodyOffset = Constants.IN_VEHICLE_BODY_OFFSET;
				} else {
					bodyOffset = 0.25f;
				}
				x += bodyOffset * Math.sin(Math.toRadians(realYaw));
				z -= bodyOffset * Math.cos(Math.toRadians(realYaw));
			}
		}
		if (entity instanceof Player realPlayer && realPlayer.isDwarf()) {
			x *= 0.666f;
			y = (y - 0.2f) * 0.666f;
			z *= 0.666f;
		}
		offset = new Vector3f((float)x, (float)y, (float)z);
	}

	@Unique
	private static float calculateBodyRot(float entityBodyRot, float riderHeadRot) {
		float wrappedHeadRot = wrapDegrees(riderHeadRot);
		float rotDiff = wrapDegrees(wrappedHeadRot - entityBodyRot);

		if (abs(rotDiff) > 50.0F)
			entityBodyRot = wrappedHeadRot - 50.0F * Math.signum(rotDiff);

		entityBodyRot = wrapDegrees(entityBodyRot);

		return entityBodyRot;
	}

	/**
	 * Linearly interpolates between two rotations, taking the shortest path.
	 */
	@Unique
	private static float rotLerp(float delta, float start, float end) {
		float diff = wrapDegrees(end - start);
		return start + delta * diff;
	}

	/**
	 * Wraps an angle in degrees to the range [-180, 180).
	 */
	@Unique
	private static float wrapDegrees(float degrees) {
		degrees = degrees % 360.0F;

		if (degrees >= 180.0F)
			degrees -= 360.0F;

		if (degrees < -180.0F)
			degrees += 360.0F;

		return degrees;
	}

	/**
	 * Absolute value for float.
	 */
	@Unique
	private static float abs(float value) {
		return value >= 0.0F ? value : -value;
	}
}
