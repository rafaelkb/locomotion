package com.trainguy9512.locomotion.animation.pose;

import com.trainguy9512.locomotion.access.LegacyHumanoidModelAccess;
import com.trainguy9512.locomotion.access.LegacyPlayerModelAccess;
import com.trainguy9512.locomotion.access.MatrixModelPart;
import com.trainguy9512.locomotion.animation.joint.skeleton.JointSkeleton;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.util.Map;
import java.util.function.Function;

public class ModelPartSpacePose extends Pose {

    protected ModelPartSpacePose(Pose pose) {
        super(pose);
    }

    static ModelPartSpacePose of(Pose pose) {
        return new ModelPartSpacePose(pose);
    }

    public void setupAnimOnModel(Object modelObject) {
        // The generic Model root API was added in 1.21.2. The legacy renderer only has a
        // registered third-person player animator, whose model parts are exposed by accessors.
        //? if >= 1.21.2 {
        /*Model<?> model = (Model<?>) modelObject;
        model.resetPose();

        JointSkeleton jointSkeleton = this.getJointSkeleton();
        Function<String, ModelPart> partLookup = model.root().createPartLookup();
        jointSkeleton.getJoints().forEach(joint -> {
            String modelPartResourceLocation = jointSkeleton.getJointConfiguration(joint).modelPartResourceLocation();
            if (modelPartResourceLocation != null) {
                ModelPart modelPart = partLookup.apply(modelPartResourceLocation);
                if (modelPart != null) {
                    ((MatrixModelPart)(Object) modelPart).locomotion$setMatrix(this.getJointChannel(joint).getTransform());
                }
            }
        });
        *///?} else {
        Map<String, ModelPart> partLookup = legacyPlayerModelParts(modelObject);
        if (!partLookup.isEmpty()) {
            JointSkeleton jointSkeleton = this.getJointSkeleton();
            jointSkeleton.getJoints().forEach(joint -> {
                String modelPartResourceLocation = jointSkeleton.getJointConfiguration(joint).modelPartResourceLocation();
                ModelPart modelPart = modelPartResourceLocation == null ? null : partLookup.get(modelPartResourceLocation);
                if (modelPart != null) {
                    ((MatrixModelPart)(Object) modelPart).locomotion$setMatrix(this.getJointChannel(joint).getTransform());
                }
            });
        }
        //?}
    }

    // No-op on 1.21.2+: modern model rendering resets poses before applying animation transforms.
    public static void clearModelPartMatrices(Object modelObject) {
        //? if < 1.21.2 {
        legacyPlayerModelParts(modelObject).values().forEach(modelPart ->
                ((MatrixModelPart)(Object) modelPart).locomotion$setMatrix(null)
        );
        //?}
    }

    private static Map<String, ModelPart> legacyPlayerModelParts(Object modelObject) {
        //? if >= 1.21.2 {
        /*return Map.of();
        *///?} else {
        if (!(modelObject instanceof LegacyHumanoidModelAccess humanoidParts)
                || !(modelObject instanceof LegacyPlayerModelAccess playerParts)) {
            return Map.of();
        }
        return Map.ofEntries(
                Map.entry("body", humanoidParts.locomotion$getBody()),
                Map.entry("head", humanoidParts.locomotion$getHead()),
                Map.entry("hat", humanoidParts.locomotion$getHat()),
                Map.entry("left_arm", humanoidParts.locomotion$getLeftArm()),
                Map.entry("right_arm", humanoidParts.locomotion$getRightArm()),
                Map.entry("left_leg", humanoidParts.locomotion$getLeftLeg()),
                Map.entry("right_leg", humanoidParts.locomotion$getRightLeg()),
                Map.entry("jacket", playerParts.locomotion$getJacket()),
                Map.entry("left_sleeve", playerParts.locomotion$getLeftSleeve()),
                Map.entry("right_sleeve", playerParts.locomotion$getRightSleeve()),
                Map.entry("left_pants", playerParts.locomotion$getLeftPants()),
                Map.entry("right_pants", playerParts.locomotion$getRightPants())
        );
        //?}
    }
}
