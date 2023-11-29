package infrastructure.database

import infrastructure.ClassLoaderUtil
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

@Named("Database")
@Singleton
class Database : IDatabase {
    companion object {
        private var con: Connection? = null

        private fun getConnection(dbName: String): Connection {
            if (con == null) {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection("jdbc:sqlite:${dbName}")
                con?.autoCommit = true
            }

            return con ?: throw InternalError()
        }
    }

    private var dbName = "data.db"

    constructor()

    constructor(dbName: String) {
        this.dbName = dbName
    }

    override val con: Connection
        get() {
            return getConnection(this.dbName)
        }

    override fun query(path: String): ResultSet {
        val queryStr = ClassLoaderUtil.getResourceContent("sql/$path.sql")
        return con.createStatement().executeQuery(queryStr)
    }

    override fun query(path: String, vararg variables: Any): ResultSet {
        val queryStr = ClassLoaderUtil.getResourceContent("sql/$path.sql")
        return querySql(queryStr, *variables)
    }

    override fun querySql(sql: String, vararg variables: Any): ResultSet {
        val preparedStatement = con.prepareStatement(sql)

        variables.forEachIndexed { index, variable ->
            preparedStatement.set(index, variable)
        }

        return preparedStatement.executeQuery()!!
    }

    override fun updateSql(sql: String, vararg variables: Any): Boolean {
        val preparedStatement = con.prepareStatement(sql)

        variables.forEachIndexed { index, variable ->
            preparedStatement.set(index, variable)
        }

        return preparedStatement.execute()
    }

    override fun update(path: String, vararg variables: Any): Boolean {
        val updateStr = ClassLoaderUtil.getResourceContent("sql/$path.sql")
        return updateSql(updateStr, *variables)
    }

    override fun batch(path: String) {
        val queryStr = ClassLoaderUtil.getResourceContent("sql/$path.sql")

        val queries = queryStr
            .split(';')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { "$it;" }

        val statement = con.createStatement()

        for (query in queries) {
            statement.addBatch(query)
        }

        statement.executeBatch()
    }
}

fun PreparedStatement.set(index: Int, variable: Any) {
    val reassigned = index + 1
    when (variable) {
        is String -> this.setString(reassigned, variable)
        is Int -> this.setInt(reassigned, variable)
        is Double -> this.setDouble(reassigned, variable)
        is Boolean -> this.setBoolean(reassigned, variable)
        else -> throw NotImplementedError("Variable $variable is of type ${variable::class.java.typeName}")
    }
}