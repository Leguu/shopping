package domain.product

import domain.Slug
import infrastructure.InvalidOperation
import infrastructure.NotFound
import jakarta.inject.Named
import jakarta.inject.Singleton

interface ProductRepository {
    fun createProduct(slugString: String, name: String, description: String, price: Double)
    fun updateProduct(sku: Int, name: String, description: String, price: Double)
    fun getProduct(sku: Int): Product
    fun getProductBySlug(slug: String): Product
    fun getAllProducts(): List<Product>
    fun getProductCatalog() : String
}

@Named("InMemoryProductRepository")
@Singleton
class InMemoryProductRepository : ProductRepository {

    private var skuIndex: Int = 1
    private val products: MutableList<Product> = mutableListOf(
        Product(
            0, Slug.fromString("food")!!, "Food items", "Burgers, etc.", 10.0
        )
    )

    override fun createProduct(slugString: String, name: String, description: String, price: Double) {
        val exists = this.products.any { p -> p.slug.inner == slugString }
        if (exists) {
            throw InvalidOperation("A product with that slug already exists")
        }

        val slug = Slug.fromString(slugString) ?: throw InvalidOperation("The slug isn't valid")

        products.add(Product(skuIndex, slug, name, description, price))
        skuIndex += 1
    }

    override fun updateProduct(sku: Int, name: String, description: String, price: Double) {
        val exitingProduct = products.find { p -> p.sku == sku } ?: throw NotFound()

        exitingProduct.name = name
        exitingProduct.description = description
        exitingProduct.price = price
    }

    override fun getProduct(sku: Int): Product {
        return products.find { p -> p.sku == sku } ?: throw NotFound()
    }

    override fun getProductBySlug(slug: String): Product {
        return products.find { p -> p.slug.inner == slug } ?: throw NotFound()
    }

    override fun getAllProducts(): List<Product> {
        return products
    }

    override fun getProductCatalog(): String {
        val products = getAllProducts()

        val csvLines = products.map { p -> p.toCsvLine() }.toMutableList()

        csvLines.add(0, Product.CSV_HEADER)

        return csvLines.joinToString("\n")
    }
}