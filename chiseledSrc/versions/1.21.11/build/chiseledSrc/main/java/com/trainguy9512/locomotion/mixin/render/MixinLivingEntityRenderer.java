package com.trainguy9512.locomotion.mixin.render;

//? if >= 1.21.2 {

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.trainguy9512.locomotion.access.EntityRenderStateAccess;
import com.trainguy9512.locomotion.animation.animator.JointAnimatorDispatcher;
import com.trainguy9512.locomotion.animation.animator.entity.EntityJointAnimator;
import com.trainguy9512.locomotion.animation.animator.entity.LivingEntityJointAnimator;
import com.trainguy9512.locomotion.animation.data.AnimationDataContainer;
import com.trainguy9512.locomotion.animation.pose.ModelPartSpacePose;
import com.trainguy9512.locomotion.animation.pose.Pose;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<S extends LivingEntityRenderState, R extends LivingEntityRenderState, T extends LivingEntity, M extends EntityModel<S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {
    @Shadow protected M model;

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }



    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V")
    )
    private void poseModel(LivingEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci, @Local RenderLayer<?, ?> renderLayer) {
        Optional<ModelPartSpacePose> potentialPose = ((EntityRenderStateAccess)renderState).animationOverhaul$getInterpolatedAnimationPose();
        potentialPose.ifPresent(pose -> pose.setupAnimOnModel(this.model));
    }

    @Inject(
            method = "setupRotations",
            at = @At("HEAD"),
            cancellable = true
    )
    public void transformModel(S renderState, PoseStack poseStack, float bodyRot, float scale, CallbackInfo ci) {
        Optional<ModelPartSpacePose> potentialPose = ((EntityRenderStateAccess)renderState).animationOverhaul$getInterpolatedAnimationPose();
        if (potentialPose.isPresent()) {
            ModelPartSpacePose pose = potentialPose.get();
            float rotation = pose.getCustomAttributeValueOrDefault(LivingEntityJointAnimator.ENTITY_ROTATION_ATTRIBUTE, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotation));
            ci.cancel();
        }
    }

}
//?} else {
/*
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.trainguy9512.locomotion.animation.animator.JointAnimatorDispatcher;
import com.trainguy9512.locomotion.animation.animator.entity.LivingEntityJointAnimator;
import com.trainguy9512.locomotion.animation.pose.ModelPartSpacePose;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    @Shadow protected M model;

    @Unique
    private ModelPartSpacePose locomotion$currentPose;

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void locomotion$preparePose(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        ModelPartSpacePose.clearModelPartMatrices(this.model);
        this.locomotion$currentPose = JointAnimatorDispatcher.getInstance()
                .getEntityAnimationDataContainer(entity)
                .map(dataContainer -> dataContainer.getInterpolatedAnimationPose(partialTicks))
                .orElse(null);
        if (this.locomotion$currentPose != null) {
            this.locomotion$currentPose.setupAnimOnModel(this.model);
        }
    }

    @Inject(method = "setupRotations", at = @At("HEAD"), cancellable = true)
    private void locomotion$transformEntity(T entity, PoseStack poseStack, float bob, float bodyYaw, float partialTicks, float scale, CallbackInfo ci) {
        if (this.locomotion$currentPose != null) {
            float rotation = this.locomotion$currentPose.getCustomAttributeValueOrDefault(LivingEntityJointAnimator.ENTITY_ROTATION_ATTRIBUTE, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotation));
            String rootJoint = this.locomotion$currentPose.getJointSkeleton().getRootJoint();
            this.locomotion$currentPose.getJointChannel(rootJoint).transformPoseStack(poseStack, 16f);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void locomotion$clearPose(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        ModelPartSpacePose.clearModelPartMatrices(this.model);
        this.locomotion$currentPose = null;
    }
}
*///?}
