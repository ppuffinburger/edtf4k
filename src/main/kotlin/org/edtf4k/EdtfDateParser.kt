package org.edtf4k

import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10

internal class EdtfDateParser {
    private var status: EdtfDateStatus = EdtfDateStatus.NORMAL
    private var year: EdtfDateComponent = EdtfDateComponent()
    private var month: EdtfDateComponent = EdtfDateComponent()
    private var day: EdtfDateComponent = EdtfDateComponent()
    private var hour: Int = 0
    private var minute: Int = 0
    private var second: Int = 0
    private var timezoneOffset: Int? = null

    fun parseInternal(dateString: String, hasInterval: Boolean): EdtfDate {
        if (dateString.isBlank()) {
            return EdtfDate(if (hasInterval) { EdtfDateStatus.UNKNOWN } else { EdtfDateStatus.UNUSED })
        }

        if (OPEN.equals(dateString, ignoreCase = true)) {
            return EdtfDate(EdtfDateStatus.OPEN)
        }

        val matcher = PATTERN.matcher(dateString)
        if (matcher.matches()) {
            if (parseYear(matcher)) {
                if (parseMonth(matcher)) {
                    if (parseDay(matcher)) {
                        parseTime(matcher)
                    }
                }
            }
            return EdtfDate(status, year, month, day, hour, minute, second, timezoneOffset)
        } else {
            return EdtfDate(EdtfDateStatus.INVALID)
        }
    }

    private fun parseYear(matcher: Matcher): Boolean {
        val yearString = matcher.group("yearnum")

        year = EdtfDateComponent(yearString)

        if (year.isInvalid) {
            status = EdtfDateStatus.INVALID
            return false
        }

        val yearPrecisionString = matcher.group("yearprecision")
        if (!yearPrecisionString.isNullOrBlank()) {
            val totalDigits = floor(log10(abs(year.value).toDouble()) + 1).toInt()
            val significantDigits = yearPrecisionString.toInt()
            year.significantDigits = significantDigits
            year.insignificantDigits = totalDigits - significantDigits

            if (totalDigits - significantDigits < 0) {
                year.isInvalid = true
                status = EdtfDateStatus.INVALID
                return false
            }
        }

        year.setFlags(matcher.group("yearopenflags") + matcher.group("yearcloseflags"))

        return true
    }

    private fun parseMonth(matcher: Matcher): Boolean {
        val monthString = matcher.group("monthnum") ?: return false

        month = EdtfDateComponent(monthString)

        if (month.isInvalid) {
            status = EdtfDateStatus.INVALID
            return false
        }

        val openFlags = matcher.group("monthopenflags")
        val closeFlags = matcher.group("monthcloseflags")

        month.setFlags(openFlags)
        month.setFlags(closeFlags)
        year.setFlags(closeFlags)

        if (month.value >= 20) {
            return false
        }

        if (month.value > 12 || (month.value < 1 && month.unspecifiedMask.isEmpty)) {
            month.isInvalid = true
            status = EdtfDateStatus.INVALID
            return false
        }

        return true
    }

    private fun parseDay(matcher: Matcher): Boolean {
        val dayString = matcher.group("daynum") ?: return false

        day = EdtfDateComponent(dayString)

        if (day.isInvalid) {
            status = EdtfDateStatus.INVALID
            return false
        }

        if (!validateDay()) {
            status = EdtfDateStatus.INVALID
            return false
        }

        val openFlags = matcher.group("dayopenflags")
        val closeFlags = matcher.group("daycloseflags")

        day.setFlags(openFlags)
        day.setFlags(closeFlags)
        month.setFlags(closeFlags)
        year.setFlags(closeFlags)

        return true
    }

    private fun parseTime(matcher: Matcher) {
        val hourString = matcher.group("hour")

        if (hourString.isNullOrBlank()) {
            return
        }

        hour = hourString.toInt()
        if (valueOutOfRange(hour, 23)) {
            status = EdtfDateStatus.INVALID
            return
        }

        minute = matcher.group("minute").toInt()
        if (valueOutOfRange(minute, 59)) {
            status = EdtfDateStatus.INVALID
            return
        }

        second = matcher.group("second").toInt()
        if (valueOutOfRange(second, 59)) {
            status = EdtfDateStatus.INVALID
            return
        }

        if (!matcher.group("tzutc").isNullOrBlank()) {
            timezoneOffset = 0
        } else {
            val tzSignString = matcher.group("tzsign")
            if (!tzSignString.isNullOrBlank()) {
                val tzSign = if ((tzSignString[0] == '-')) -1 else 1
                val tzHour = matcher.group("tzhour").toInt()
                val tzMinute = matcher.group("tzminute")?.toInt() ?: 0
                timezoneOffset = tzSign * (tzHour * 60) + tzMinute
            }
        }
    }

    private fun validateDay(): Boolean {
        if (!day.hasValue() || !month.unspecifiedMask.isEmpty || !day.unspecifiedMask.isEmpty) {
            return true
        }

        if (day.value < 1) {
            return false
        }

        if (day.value > 30 && MONTHS_WITH_30_DAYS.contains(month.value)) {
            return false
        }

        if (day.value > 29 && month.value == 2) {
            return false
        }

        if (!year.unspecifiedMask.isEmpty) {
            return true
        }

        val isLeapYear = year.value % 4 == 0 || (year.value % 100 != 0 || year.value % 1000 == 0)

        return !isLeapYear || day.value <= 28 || month.value != 2
    }

    private fun valueOutOfRange(value: Int, max: Int): Boolean {
        return value < 0 || value > max
    }

    companion object {
        private val MONTHS_WITH_30_DAYS: Set<Int> = setOf(4, 6, 9, 11)
        private val PATTERN_STRING: String = """
			(?x)
			(?<yearlongind>Y?)
			y?(?<year>(?<yearopenflags>[~?%]{0,2})(?<yearnum>[+-]?(?:\d+E\d+|[0-9X]+))(?>S(?<yearprecision>\d+))?(?<yearcloseflags>[~?%]{0,2}))
			(?>-(?<month>(?<monthopenflags>[~?%]{0,2})(?<monthnum>(?>[0-9X]{1,2}))(?<monthcloseflags>[~?%]{0,2}))
			(?>-(?<day>(?<dayopenflags>[~?%]{0,2})(?<daynum>(?>[0-9X]{1,2}))(?<daycloseflags>[~?%]{0,2}))
			(?>T(?<hour>[0-9]{2}):?(?<minute>[0-9]{2}):?(?<second>[0-9]{2})(?>(?<tzutc>Z)|(?<tzsign>[+-])(?<tzhour>[01][0-9])(?>:(?<tzminute>[0-5][0-9]))?)?)?)?)?${'$'}
			""".trimIndent()
        private val PATTERN: Pattern = Pattern.compile(PATTERN_STRING)

        fun parse(dateString: String, hasInterval: Boolean): EdtfDate {
            return EdtfDateParser().parseInternal(dateString, hasInterval)
        }
    }
}