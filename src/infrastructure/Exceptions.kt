package infrastructure

class AlreadyExists : Exception()

class NotFound: Exception()

class InvalidOperation : Exception()

class InternalServerError(message: String): Exception(message)

class Unauthorized : Exception()