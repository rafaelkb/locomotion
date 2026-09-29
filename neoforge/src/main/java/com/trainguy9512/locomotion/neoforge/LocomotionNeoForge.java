package com.trainguy9512.locomotion.neoforge;

import com.trainguy9512.locomotion.LocomotionMain;
//? if >= 1.21.11 {
/*import com.trainguy9512.locomotion.debug.LocomotionDebugScreenEntries;
*///?}
import com.trainguy9512.locomotion.resource.LocomotionResources;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//? if >= 1.21.11 {
/*import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
*///?} else {
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
//?}
//? if >= 1.21.11 {
/*import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
*///?}
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = LocomotionMain.MOD_ID, dist = Dist.CLIENT)
public class LocomotionNeoForge {

    public LocomotionNeoForge(ModContainer modContainer, IEventBus modEventBus) {
        modEventBus.addListener(this::onClientInitialize);
        modEventBus.addListener(this::onResourceReload);
        //? if >= 1.21.11 {
        /*modEventBus.addListener(this::onRegisterDebugScreenEntries);
        *///?}

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) -> LocomotionMain.CONFIG.getConfigScreen(ModList.get()::isLoaded).apply(parent));
    }

    public void onClientInitialize(FMLClientSetupEvent event) {
        LocomotionMain.initialize();

    }

    //? if >= 1.21.11 {
    /*public void onResourceReload(AddClientReloadListenersEvent event) {
        event.addListener(LocomotionResources.RELOADER_IDENTIFIER, new LocomotionResources());
    }
    *///?} else {
    public void onResourceReload(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new LocomotionResources());
    }
    //?}

    //? if >= 1.21.11 {
    /*public void onRegisterDebugScreenEntries(RegisterDebugEntriesEvent event) {
        LocomotionDebugScreenEntries.register(event::register);
    }
    *///?}

}
