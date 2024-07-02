@file:EventFunctionalInterface<ClientTickEvents.EndTick>
@file:EventFunctionalInterface<ClientTickEvents.StartTick>
@file:EventFunctionalInterface<AttackEntityCallback>
@file:EventFunctionalInterface<AttackBlockCallback>

package net.examplemod.fabric

import net.examplemod.EventFunctionalInterface
import net.examplemod.ExampleMod
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.AttackEntityCallback

@Suppress("unused")
fun init() {
    ExampleMod.init()
}
