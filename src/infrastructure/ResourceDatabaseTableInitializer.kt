package infrastructure

open class ResourceDatabaseTableInitializer(resourcePaths: List<String>) {
    init {
        resourcePaths.forEach {
            MySqlDatabase().use { db ->
                db.batch(it)
            }
            println("Applied migration $it")
        }
    }
}