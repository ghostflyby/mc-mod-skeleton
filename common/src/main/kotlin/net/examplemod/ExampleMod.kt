package net.examplemod

@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
annotation class ExampleModInit

object ExampleMod {
    const val MOD_ID = "examplemod"

    @ExampleModInit
    fun init() {
    }
}
