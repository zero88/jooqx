data class DependencyBridge(
    val source: String,
    val sourceDir: String,
    val versionConstraint: Boolean,
    val excludes: Set<String>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DependencyBridge

        return source == other.source
    }

    override fun hashCode(): Int {
        return source.hashCode()
    }
}
