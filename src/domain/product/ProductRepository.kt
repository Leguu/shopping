package domain.product

import domain.Slug
import infrastructure.AlreadyExists
import infrastructure.InvalidOperation
import infrastructure.NotFound
import jakarta.enterprise.context.ApplicationScoped

interface ProductRepository {
    fun createProduct(slugString: String, name: String, description: String, price: Double)
    fun updateProduct(sku: Int, input: Product)
    fun getProduct(sku: Int): Product
    fun getProductBySlug(slug: String): Product
    fun getAllProducts(): List<Product>
}

@ApplicationScoped
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
            throw AlreadyExists()
        }

        val slug = Slug.fromString(slugString) ?: throw InvalidOperation("The slug isn't valid")

        products.add(Product(skuIndex, slug, name, description, price))
        skuIndex += 1
    }

    override fun updateProduct(sku: Int, input: Product) {
        val exitingProduct = products.find { p -> p.sku == sku } ?: throw NotFound()

        exitingProduct.name = input.name
        exitingProduct.description = input.description
        exitingProduct.price = input.price
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
}