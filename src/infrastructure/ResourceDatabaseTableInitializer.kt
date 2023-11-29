package infrastructure


import infrastructure.database.Database

open class ResourceDatabaseTableInitializer(resourcePaths: List<String>) {
    init {
        if (System.getenv("env") != "testing") {
            resourcePaths.forEach {
                Database().batch(it)
                println("Applied migration $it")
            }
        }
    }
}