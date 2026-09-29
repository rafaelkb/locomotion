package com.trainguy9512.locomotion.mixin.render.block_entity;

//? if >= 1.21.11 {

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.trainguy9512.locomotion.animation.animator.JointAnimatorDispatcher;
import com.trainguy9512.locomotion.animation.data.AnimationDataContainer;
import com.trainguy9512.locomotion.render.LocomotionWrappedRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ChestRenderer.class)
public class MixinChestRenderer {

    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
    )
    public void setChestModelDataContainers(
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
            Operation<Void> original,
            @Local(argsOnly = true)
            ChestRenderState renderState
    ) {
        Optional<AnimationDataContainer> potentialContainer = JointAnimatorDispatcher.getInstance().getBlockEntityAnimationDataContainer(renderState.blockPos, renderState.blockEntityType);
        if (potentialContainer.isPresent()) {
            LocomotionWrappedRenderState<?> wrappedRenderState = LocomotionWrappedRenderState.of(o, potentialContainer.get());
            original.call(instance, model, wrappedRenderState, poseStack, renderType, i, j, k, textureAtlasSprite, f, crumblingOverlay);
        } else {
            original.call(instance, model, o, poseStack, renderType, i, j, k, textureAtlasSprite, f, crumblingOverlay);
        }
    }
}
//?} else {
/*
import com.trainguy9512.locomotion.animation.animator.JointAnimatorDispatcher;
import com.trainguy9512.locomotion.animation.data.AnimationDataContainer;
import com.trainguy9512.locomotion.animation.joint.skeleton.JointSkeleton;
import com.trainguy9512.locomotion.animation.pose.ModelPartSpacePose;
import com.trainguy9512.locomotion.access.MatrixModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ChestRenderer.class)
public abstract class MixinChestRenderer {
    @Shadow @Final private ModelPart bottom;
    @Shadow @Final private ModelPart lid;
    @Shadow @Final private ModelPart lock;
    @Shadow @Final private ModelPart doubleLeftBottom;
    @Shadow @Final private ModelPart doubleLeftLid;
    @Shadow @Final private ModelPart doubleLeftLock;
    @Shadow @Final private ModelPart doubleRightBottom;
    @Shadow @Final private ModelPart doubleRightLid;
    @Shadow @Final private ModelPart doubleRightLock;

    @Inject(method = "render", at = @At("HEAD"))
    private void locomotion$applyAnimationPose(BlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci) {
        Map<String, List<ModelPart>> modelParts = this.locomotion$getModelParts();
        locomotion$clearMatrices(modelParts);
        AnimationDataContainer dataContainer = JointAnimatorDispatcher.getInstance()
                .getBlockEntityAnimationDataContainer(blockEntity.getBlockPos(), blockEntity.getType())
                .orElse(null);
        if (dataContainer != null) {
            ModelPartSpacePose pose = dataContainer.getInterpolatedAnimationPose(partialTicks);
            JointSkeleton skeleton = pose.getJointSkeleton();
            skeleton.getJoints().forEach(joint -> {
                String identifier = skeleton.getJointConfiguration(joint).modelPartIdentifier();
                List<ModelPart> parts = identifier == null ? null : modelParts.get(identifier);
                if (parts != null) {
                    parts.forEach(part -> ((MatrixModelPart) (Object) part).locomotion$setMatrix(pose.getJointChannel(joint).getTransform()));
                }
            });
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void locomotion$clearAnimationPose(BlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci) {
        locomotion$clearMatrices(this.locomotion$getModelParts());
    }

    private Map<String, List<ModelPart>> locomotion$getModelParts() {
        return Map.of(
                "bottom", List.of(this.bottom, this.doubleLeftBottom, this.doubleRightBottom),
                "lid", List.of(this.lid, this.doubleLeftLid, this.doubleRightLid),
                "lock", List.of(this.lock, this.doubleLeftLock, this.doubleRightLock)
        );
    }

    private static void locomotion$clearMatrices(Map<String, List<ModelPart>> modelParts) {
        modelParts.values().forEach(parts -> parts.forEach(part ->
                ((MatrixModelPart) (Object) part).locomotion$setMatrix(null)
        ));
    }
}
*///?}
