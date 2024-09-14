package net.examplemod.fabric

import net.examplemod.ExampleMod
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage

@Suppress("unused")
fun init() {
  ExampleMod.init()
  ItemStorage.SIDED.id
}
