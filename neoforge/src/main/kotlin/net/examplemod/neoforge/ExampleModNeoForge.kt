package net.examplemod.neoforge

import net.examplemod.ExampleMod
import net.examplemod.ExampleModInit
import net.neoforged.fml.common.Mod

@OptIn(ExampleModInit::class)
@Mod(ExampleMod.MOD_ID)
internal object ExampleModNeoForge {
  init {
    ExampleMod.init()
  }
}
