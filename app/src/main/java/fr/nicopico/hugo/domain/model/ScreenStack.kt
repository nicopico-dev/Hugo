package fr.nicopico.hugo.domain.model

import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.warn

data class ScreenStack(
        private val stack: List<Screen>,
        val popped: Boolean = false
) : HugoLogger {

    val current: Screen? = stack.lastOrNull()

    constructor(rootScreen: Screen) : this(listOf(rootScreen))

    fun push(screen: Screen): ScreenStack {
        return if (screen == current) {
            warn { "Ignore duplicate screen push on $screen"}
            this
        } else {
            ScreenStack(stack + screen)
        }
    }

    fun pop(): ScreenStack {
        if (stack.isNotEmpty()) {
            return ScreenStack(stack.subList(0, stack.lastIndex), true)
        }
        return this
    }

    fun popUntil(screen: Screen): ScreenStack {
        val newStack = stack.toMutableList()
        while (newStack.size > 1 && newStack.last() != screen) {
            newStack.removeAt(newStack.lastIndex)
        }

        if (newStack.last() != screen) {
            newStack.add(screen)
        }

        return ScreenStack(stack, true)
    }
}