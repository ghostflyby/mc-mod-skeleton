package net.examplemod

import com.google.auto.service.AutoService
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import net.fabricmc.fabric.api.event.Event
import java.io.BufferedWriter

@Suppress("unused")
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class EventFunctionalInterface<T>

@AutoService(SymbolProcessorProvider::class)
internal class FabricEventProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        FabricEventProcessor(environment.codeGenerator, environment.logger)
}

private class FabricEventProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private val writers = mutableMapOf<String, BufferedWriter>()

    private fun getWriter(
        packageName: String,
        name: String,
    ) = writers.getOrPut(name) {
        codeGenerator
            .createNewFile(
                Dependencies(false),
                packageName,
                name,
            ).bufferedWriter()
            .apply {
                write("package $packageName\n\n")
            }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val files =
            resolver.getSymbolsWithAnnotation(EventFunctionalInterface::class.qualifiedName!!, false)

        val eventClass = resolver.getClassDeclarationByName(resolver.getKSNameFromString(Event::class.qualifiedName!!))

        files
            .flatMap { it.annotations }
            .filter {
                it.annotationType
                    .resolve()
                    .declaration.qualifiedName
                    ?.asString() == EventFunctionalInterface::class.qualifiedName
            }.map { it.annotationType.resolve().arguments }
            .map { it[0].type?.resolve()?.declaration }
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.classKind == ClassKind.INTERFACE
            }.flatMap { functionInterface ->
                functionInterface
                    .staticProperties
                    .ifEmpty {
                        val declaration = functionInterface.parentDeclaration
                        if (declaration is KSClassDeclaration) {
                            declaration.staticProperties
                        } else {
                            emptySequence()
                        }
                    }.filter {
                        it.type.resolve().declaration == eventClass
                    }.filter {
                        val args = it.type.resolve().arguments
                        args.isNotEmpty() && args[0].type?.resolve()?.declaration == functionInterface
                    }.map {
                        it to
                            functionInterface.getDeclaredFunctions().first()
                    }
            }.forEach { (p, f) ->
                val packageName = p.packageName.asString()
                getWriter(packageName, p.parentDeclaration!!.simpleName.asString()).run {
                    write(genCode(p, f))
                }
            }
        writers.values.forEach { it.close() }
        return emptyList()
    }
}

private fun genCode(
    property: KSPropertyDeclaration,
    function: KSFunctionDeclaration,
): String {
    val functionSimpleName = function.parentDeclaration!!.simpleName.asString()
    val functionParameters = function.parameters.joinToString(", ") { it.name!!.asString() }
    val interfaceName = function.parentDeclaration?.qualifiedName!!.asString()

    return """
${function.toArgsClass()}

@JvmName("${property.simpleName.asString()}_invoke")
operator fun ${property.type.resolve().declaration.qualifiedName!!.asString()}<$interfaceName>.invoke(block: ${functionSimpleName}Arg.() -> ${function.returnType!!.resolve().declaration.qualifiedName!!.asString()}) =
    ${property.qualifiedName!!.asString()}.register { $functionParameters->
        ${functionSimpleName}Arg($functionParameters).block()
    }


        """.trimIndent()
}

private fun KSFunctionDeclaration.toArgsClass(): String =
    """
class ${parentDeclaration!!.simpleName.asString()}Arg(
    ${
        parameters.joinToString(",\n\t") {
            val nullable = if (it.type.resolve().nullability == Nullability.NULLABLE) "?" else ""
            "val ${it.toParamName()}: ${declaration.qualifiedName!!.asString()}$nullable"
        }
    }
)
    """.trimIndent()

private class KSValueJoinParameter(
    val it: KSValueParameter,
    val declaration: KSDeclaration,
)

private fun List<KSValueParameter>.joinToString(
    separator: String,
    transForm: KSValueJoinParameter.() -> String,
) = joinToString(separator, "") {
    val declaration = it.type.resolve().declaration
    KSValueJoinParameter(it, declaration).transForm()
}

private val KSClassDeclaration.staticProperties: Sequence<KSPropertyDeclaration>
    get() =
        getDeclaredProperties().filter {
            Modifier.JAVA_STATIC in it.modifiers
        }

fun KSValueParameter.toParamName(): String {
    val n =
        type
            .resolve()
            .declaration.simpleName
            .asString()
    return when (n) {
        "MinecraftClient" -> "client"
        "PlayerEntity" -> "player"
        else -> n.replaceFirstChar { it.lowercase() }
    }
}
