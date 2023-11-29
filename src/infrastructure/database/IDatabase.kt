package infrastructure.database

import java.sql.Connection
import java.sql.ResultSet

interface IDatabase {
    val con: Connection

    fun query(path: String): ResultSet
    fun query(path: String, vararg variables: Any): ResultSet
    fun querySql(sql: String, vararg variables: Any): ResultSet
    fun updateSql(sql: String, vararg variables: Any): Boolean
    fun update(path: String, vararg variables: Any): Boolean
    fun batch(path: String)
}