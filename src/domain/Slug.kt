package domain

class Slug private constructor(val inner: String) {
    override fun toString(): String {
        return inner
    }

    companion object {
        fun fromString(value: String): Slug? {
            if (value.length > 100) {
                return null;
            }

            if (!Regex("[a-z0-9-]+").matches(value)) {
                return null
            }

            return Slug(value)
        }
    }
}
