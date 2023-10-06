package infrastructure

class NotFound: Exception()

class InvalidOperation(message: String) : Exception(message)

class Unauthorized : Exception()