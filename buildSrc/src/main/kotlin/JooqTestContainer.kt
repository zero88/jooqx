data class TestContainerJDBC(val driver: String, val url: String)

fun getTestContainer(dbVersion: String, schemaFile: String, params: Map<String, Any> = mapOf()): TestContainerJDBC =
    getTestContainer(dbVersion, params.plus("TC_INITSCRIPT" to "file:${schemaFile}"))

fun getTestContainerByFun(
    dbVersion: String,
    functionName: String,
    params: Map<String, Any> = mapOf()
): TestContainerJDBC = getTestContainer(dbVersion, params.plus("TC_INITFUNCTION" to functionName))

fun getTestContainer(dbVersion: String, params: Map<String, Any> = mapOf()): TestContainerJDBC {
    val param = optimizeDockerParams().plus(params).map { "${it.key}=${it.value}" }.joinToString("&")
    return TestContainerJDBC(
        driver = "org.testcontainers.jdbc.ContainerDatabaseDriver",
        url = "jdbc:tc:${dbVersion}:///jooqx?${param}"
    )
}

private fun optimizeDockerParams(): Map<String, Any> {
    val os = System.getProperty("os.name").toLowerCase()
    return when {
        // https://docs.docker.com/storage/tmpfs/
        arrayOf("nix", "nux", "aix").any { os.contains(it) } -> mapOf("TC_TMPFS" to "/testtmpfs:rw")
        else                                                 -> mapOf()
    }
}
