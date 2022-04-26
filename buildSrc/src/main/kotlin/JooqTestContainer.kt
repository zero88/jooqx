data class TestContainerJDBC(val driver: String, val url: String)

fun getTestContainer(dbVersion: String, schemaFile: String): TestContainerJDBC {
    return TestContainerJDBC(
        driver = "org.testcontainers.jdbc.ContainerDatabaseDriver",
        url = "jdbc:tc:${dbVersion}:///jooqx?TC_TMPFS=/testtmpfs:rw&TC_INITSCRIPT=file:${schemaFile}"
    )
}
