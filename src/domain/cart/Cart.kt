package domain.cart

import domain.user.User
import domain.product.Product

class Cart(val userId: Int, val productIds: MutableList<Int>)