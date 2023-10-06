package infrastructure

class AlreadyExists : Exception()

class NotFound: Exception()

class InvalidOperation(message: String) : Exception(message)

class InternalServerError(message: String): Exception(message)

class Unauthorized : Exception()