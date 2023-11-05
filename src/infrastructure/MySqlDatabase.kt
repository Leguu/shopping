package infrastructure

import java.io.Closeable
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class MySqlDatabase : Closeable {
    var con: Connection;

    init {
        Class.forName("com.mysql.cj.jdbc.Driver")
        val con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/shopping", "dbuser", "dbpass")
        con.autoCommit = true
        this.con = con
    }

    override fun close() {
        con.close()
    }

    fun query(path: String): ResultSet {
        val queryStr = ClassLoaderUtil.getResourceContent("sql/$path.sql")
        return con.createStatement().executeQuery(queryStr)
    }

    fun query(path: String, vararg variables: Any): ResultSet {
        val queryStr = ClassLoaderUtil.getResourceContent("sql/$path.sql")
        return querySql(queryStr, *variables)
    }

    fun querySql(sql: String, vararg variables: Any): ResultSet {
        val preparedStatement = con.prepareStatement(sql)

        variables.forEachIndexed { index, variable ->
            preparedStatement.set(index, variable)
        }

        return preparedStatement.executeQuery()!!
    }

    fun updateSql(sql: String, vararg variables: Any): Boolean {
        val preparedStatement = con.prepareStatement(sql)

        variables.forEachIndexed { index, variable ->
            preparedStatement.set(index, variable)
        }

        return preparedStatement.execute()
    }

    fun update(path: String, vararg variables: Any): Boolean {
        val updateStr = ClassLoaderUtil.getResourceContent("sql/$path.sql")
        return updateSql(updateStr, *variables)
    }

    fun batch(path: String) {
        val queryStr = ClassLoaderUtil.getResourceContent("sql/$path.sql")
        val queries = queryStr
            .split(';')
            .dropLast(1)
            .map { it.trim() }
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
        else -> throw NotImplementedError()
    }
}