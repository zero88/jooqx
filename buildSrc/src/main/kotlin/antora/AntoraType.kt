package antora

enum class AntoraType {
    COMPONENT, MODULE;

    fun isComponent(): Boolean {
        return this == COMPONENT
    }

    fun isModule(): Boolean {
        return this == MODULE
    }
}
