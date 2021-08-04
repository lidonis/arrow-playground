import arrow.core.Nel
import arrow.core.Validated
import arrow.core.invalidNel
import arrow.core.valid
import arrow.core.zip
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

enum class Enum1 {
    A, B, C;

    companion object {
        fun validate(value :String) = Validated.catch { valueOf(value) }
            .mapLeft { ValidationError.InvalidEnum1Value }
    }
}

enum class Enum2 {
    X, Y, Z;

    companion object {
        fun validate(value :String) = Validated.catch { valueOf(value) }
            .mapLeft { ValidationError.InvalidEnum2Value }
    }
}

fun validateOffsetDateTime(timestamp :Long) = Validated.catch { OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC) }
    .mapLeft { ValidationError.InvalidDateValue }

data class Data(
    val enum1: Enum1,
    val enum2: Enum2,
    val date: OffsetDateTime,
)

sealed class ValidationError {
    object InvalidEnum1Value : ValidationError()
    object InvalidEnum2Value : ValidationError()
    object InvalidDateValue : ValidationError()
    object InvalidEnumCombination : ValidationError()
}

fun main() {
    println(validatedDataCombination("A", "Z", 1))
}

// this works fine
fun validatedData(enum1: String, enum2: String, timestamp: Long): Validated<Nel<ValidationError>, Data> {
    val validatedEnum1 = Enum1.validate(enum1)
    val validatedEnum2 = Enum2.validate(enum2)
    val validatedDate = validateOffsetDateTime(timestamp)


    return validatedEnum1.toValidatedNel().zip(
        validatedEnum2.toValidatedNel(),
        validatedDate.toValidatedNel()
    ) { validEnum1, validEnum2, validDate ->
        Data(
            validEnum1,
            validEnum2,
            validDate
        )
    }
}

// this need to check combination of enums
fun validatedDataCombination(enum1: String, enum2: String, timestamp: Long): Validated<Nel<ValidationError>, Data> {
    val validatedEnum1 = Enum1.validate(enum1)
    val validatedEnum2 = Enum2.validate(enum2)
    val validatedDate = validateOffsetDateTime(timestamp)
        .mapLeft { ValidationError.InvalidDateValue }

    val validatedEnums = checkCombination(validatedEnum1, validatedEnum2)

    return validatedEnums.zip(
        validatedDate.toValidatedNel()
    ) { (validEnum1, validEnum2), validDate ->
        Data(
            validEnum1,
            validEnum2,
            validDate
        )
    }
}

fun checkCombination(validatedEnum1: Validated<ValidationError, Enum1>, validatedEnum2: Validated<ValidationError, Enum2>): Validated<Nel<ValidationError>, Pair<Enum1, Enum2>> =
    validatedEnum1.toValidatedNel().zip(validatedEnum2.toValidatedNel()) { validEnum1, validEnum2 ->
        when(val res = validEnum1 to validEnum2) {
            Enum1.A to Enum2.Z -> ValidationError.InvalidEnumCombination.invalidNel()
            else -> res.valid()
        }
    }

