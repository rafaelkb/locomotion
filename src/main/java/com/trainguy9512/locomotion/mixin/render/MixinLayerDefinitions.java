package com.trainguy9512.locomotion.mixin.render;

import com.trainguy9512.locomotion.LocomotionMain;
import com.trainguy9512.locomotion.resource.json.GsonConfiguration;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.*;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Mixin(LayerDefinitions.class)
public class MixinLayerDefinitions {

    @Inject(
            method = "createRoots",
            at = @At(value = "RETURN")
    )
    private static void getCreatedModels(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        Logger logger = LocomotionMain.DEBUG_LOGGER;

        // This hook is intentionally disabled. Keep the mixin as a no-op so
        // the debug exporter does not tie compilation to ModelLayerLocation's
        // version-specific accessors.
        return;
    }
}
