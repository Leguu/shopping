package domain.product

interface ProductRepository {
    fun createProduct(slugString: String, name: String, description: String, price: Double)
    fun updateProduct(sku: Int, name: String, description: String, price: Double)
    fun getProduct(sku: Int): Product
    fun getProductBySlug(slug: String): Product
    fun getAllProducts(): List<Product>
    fun getProductCatalog() : String
}

