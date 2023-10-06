package domain.product

import domain.Slug

class Product(
    val sku: Int,
    val slug: Slug,
    var name: String,
    var description: String,
    var price: Double
) {
    override fun toString(): String {
        return name
    }
}