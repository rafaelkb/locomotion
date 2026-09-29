package com.trainguy9512.locomotion.mixin.render.block_entity;

//? if >= 1.21.11 {

/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.trainguy9512.locomotion.LocomotionMain;
import com.trainguy9512.locomotion.animation.animator.JointAnimatorDispatcher;
import com.trainguy9512.locomotion.animation.data.AnimationDataContainer;
import com.trainguy9512.locomotion.render.LocomotionWrappedRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ShulkerBoxRenderer.class)
public class MixinShulkerBoxRenderer {


    private ShulkerBoxRenderState locomotion$currentRenderState = null;

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ShulkerBoxRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At("HEAD")
    )
    public void setCurrentRenderState(ShulkerBoxRenderState shulkerBoxRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo ci) {
        this.locomotion$currentRenderState = shulkerBoxRenderState;
    }

    @WrapOperation(
            method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IILnet/minecraft/core/Direction;FLnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;Lnet/minecraft/client/resources/model/Material;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    public void wrapDataContainerInRenderState(
            SubmitNodeCollector instance,
            Model<?> model,
            Object o,
            PoseStack poseStack,
            RenderType renderType,
            int i,
            int j,
            int k,
            TextureAtlasSprite textureAtlasSprite,
            int f,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
            Operation<Void> original
    ) {
        ShulkerBoxRenderState renderState = this.locomotion$currentRenderState;
        if (renderState != null) {
            Optional<AnimationDataContainer> potentialContainer = JointAnimatorDispatcher.getInstance().getBlockEntityAnimationDataContainer(renderState.blockPos, renderState.blockEntityType);
            if (potentialContainer.isPresent()) {
                LocomotionWrappedRenderState<?> wrappedRenderState = LocomotionWrappedRenderState.of(o, potentialContainer.get());
                original.call(instance, model, wrappedRenderState, poseStack, renderType, i, j, k, textureAtlasSprite, f, crumblingOverlay);
            } else {
                original.call(instance, model, o, poseStack, renderType, i, j, k, textureAtlasSprite, f, crumblingOverlay);
            }
        } else {
            original.call(instance, model, o, poseStack, renderType, i, j, k, textureAtlasSprite, f, crumblingOverlay);
        }
    }
}
*///?} else {

import com.mojang.blaze3d.vertex.PoseStack;
import com.trainguy9512.locomotion.access.LegacyShulkerModelAccess;
import com.trainguy9512.locomotion.access.MatrixModelPart;
import com.trainguy9512.locomotion.animation.animator.JointAnimatorDispatcher;
import com.trainguy9512.locomotion.animation.data.AnimationDataContainer;
import com.trainguy9512.locomotion.animation.joint.skeleton.JointSkeleton;
import com.trainguy9512.locomotion.animation.pose.ModelPartSpacePose;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ShulkerBoxRenderer.class)
public abstract class MixinShulkerBoxRenderer {
    @Shadow @Final private ShulkerModel<?> model;

    @Inject(method = "render", at = @At("HEAD"))
    private void locomotion$applyAnimationPose(ShulkerBoxBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci) {
        ModelPart base = ((LegacyShulkerModelAccess) (Object) this.model).locomotion$getBase();
        ModelPart lid = this.model.getLid();
        ((MatrixModelPart) (Object) base).locomotion$setMatrix(null);
        ((MatrixModelPart) (Object) lid).locomotion$setMatrix(null);

        AnimationDataContainer dataContainer = JointAnimatorDispatcher.getInstance()
                .getBlockEntityAnimationDataContainer(blockEntity.getBlockPos(), blockEntity.getType())
                .orElse(null);
        if (dataContainer != null) {
            ModelPartSpacePose pose = dataContainer.getInterpolatedAnimationPose(partialTicks);
            JointSkeleton skeleton = pose.getJointSkeleton();
            skeleton.getJoints().forEach(joint -> {
                String identifier = skeleton.getJointConfiguration(joint).modelPartResourceLocation();
                ModelPart modelPart = switch (identifier == null ? "" : identifier) {
                    case "base" -> base;
                    case "lid" -> lid;
                    default -> null;
                };
                if (modelPart != null) {
                    ((MatrixModelPart) (Object) modelPart).locomotion$setMatrix(pose.getJointChannel(joint).getTransform());
                }
            });
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void locomotion$clearAnimationPose(ShulkerBoxBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci) {
        LegacyShulkerModelAccess parts = (LegacyShulkerModelAccess) (Object) this.model;
        ((MatrixModelPart) (Object) parts.locomotion$getBase()).locomotion$setMatrix(null);
        ((MatrixModelPart) (Object) this.model.getLid()).locomotion$setMatrix(null);
    }
}
//?}
