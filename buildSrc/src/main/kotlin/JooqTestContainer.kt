data class TestContainerJDBC(val driver: String, val url: String)

fun getTestContainer(dbVersion: String, schemaFile: String, dbUser: String? = null): TestContainerJDBC {
    val param = if (dbUser.isNullOrBlank()) "" else "user=${dbUser}&"
    return TestContainerJDBC(
        driver = "org.testcontainers.jdbc.ContainerDatabaseDriver",
        url = "jdbc:tc:${dbVersion}:///jooqx?${param}TC_TMPFS=/testtmpfs:rw&TC_INITSCRIPT=file:${schemaFile}"
    )
}
