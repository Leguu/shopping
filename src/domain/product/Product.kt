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

    fun toCsvLine(): String {
        return "${sku},\"${name.stripQuotes()}\",\"${description.stripQuotes()}\",${slug},${price}"
    }

    private fun String.stripQuotes(): String {
        return replace("\"", "\"\"")
    }

    companion object {
        const val CSV_HEADER = "sku,name,description,slug,price"
    }
}