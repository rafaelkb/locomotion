package com.trainguy9512.locomotion.access;

import net.minecraft.client.model.geom.ModelPart;

/** Access to the package-private body parts on legacy humanoid models. */
public interface LegacyHumanoidModelAccess {
    ModelPart locomotion$getBody();
    ModelPart locomotion$getHead();
    ModelPart locomotion$getHat();
    ModelPart locomotion$getLeftArm();
    ModelPart locomotion$getRightArm();
    ModelPart locomotion$getLeftLeg();
    ModelPart locomotion$getRightLeg();
}
