package domain.product

import domain.Slug
import infrastructure.database.IDatabase
import infrastructure.InvalidOperation
import infrastructure.NotFound
import infrastructure.ResourceDatabaseTableInitializer
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.sql.ResultSet

@Named("MySqlProductRepository")
@Singleton
class DatabaseProductRepository : ProductRepository,
    ResourceDatabaseTableInitializer(listOf("products/ProductsTableMigration")) {
    @Inject
    private lateinit var db: IDatabase

    override fun createProduct(slugString: String, name: String, description: String, price: Double) {
        Slug.fromString(slugString) ?: throw InvalidOperation("Provided slug is invalid.")
        db.update("products/InsertProductMutation", slugString, name, description, price)
    }

    override fun updateProduct(sku: Int, name: String, description: String, price: Double) {
        db.updateSql(
            "update products " +
                    "set name = ?, description = ?, price = ? " +
                    "where id = ?;", name, description, price, sku
        )
    }

    override fun getProduct(sku: Int): Product {
        val result = db.query("products/ProductBySkuQuery", sku)
        if (!result.next()) throw NotFound()
        return Product.fromResultSet(result)
    }

    override fun getProductBySlug(slug: String): Product {
        val result = db.query("products/ProductBySlugQuery", slug)
        if (!result.next()) throw NotFound()
        return Product.fromResultSet(result)
    }

    override fun getAllProducts(): List<Product> {
        val results = db.query("products/ProductsQuery")
        val products = mutableListOf<Product>()
        while (results.next()) {
            products.add(Product.fromResultSet(results))
        }
        return products
    }

    override fun getProductCatalog(): String {
        val products = getAllProducts()

        val csvLines = products.map { p -> p.toCsvLine() }.toMutableList()

        csvLines.add(0, Product.CSV_HEADER)

        return csvLines.joinToString("\n")
    }

    private fun Product.Companion.fromResultSet(rs: ResultSet): Product {
        return Product(
            rs.getInt("id"),
            Slug.fromString(rs.getString("slug"))!!,
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price")
        )
    }
}