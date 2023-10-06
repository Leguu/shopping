package domain.user

import domain.cart.Cart

class User(val id: Int, val name: String, val password: String, val isAdmin: Boolean)