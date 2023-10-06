package domain

import kotlin.Exception

class AlreadyExists : Exception()

class NotFound: Exception()

class InvalidOperation : Exception()

class InternalServerError: Exception()

class Unauthorized : Exception()