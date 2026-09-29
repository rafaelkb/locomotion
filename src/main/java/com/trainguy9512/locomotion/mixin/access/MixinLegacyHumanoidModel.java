package com.trainguy9512.locomotion.mixin.access;

//? if >= 1.21.2 {
//?} else {

import com.trainguy9512.locomotion.access.LegacyHumanoidModelAccess;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HumanoidModel.class)
public interface MixinLegacyHumanoidModel extends LegacyHumanoidModelAccess {
    @Accessor("body") ModelPart locomotion$getBody();
    @Accessor("head") ModelPart locomotion$getHead();
    @Accessor("hat") ModelPart locomotion$getHat();
    @Accessor("leftArm") ModelPart locomotion$getLeftArm();
    @Accessor("rightArm") ModelPart locomotion$getRightArm();
    @Accessor("leftLeg") ModelPart locomotion$getLeftLeg();
    @Accessor("rightLeg") ModelPart locomotion$getRightLeg();
}
//?}
