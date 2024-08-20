package utils

import java.awt.Component
import java.awt.Container

/**
 * Recurses through all child components, returning an ArrayList of all children of the appropriate
 * type
 */
inline fun <reified T> Container.getAllChildComponentsForType() =
    getAllChildComponentsForType(T::class.java)

fun <T> Container.getAllChildComponentsForType(clazz: Class<T>): List<T> {
    val ret = mutableListOf<T>()

    val components = components
    addComponents(ret, components, clazz)

    return ret
}

@Suppress("UNCHECKED_CAST")
fun <T> addComponents(ret: MutableList<T>, components: Array<Component>, desiredClazz: Class<T>) {
    for (comp in components) {
        if (desiredClazz.isInstance(comp)) {
            ret.add(comp as T)
        }

        if (comp is Container) {
            val subComponents = comp.components
            addComponents(ret, subComponents, desiredClazz)
        }
    }
}
