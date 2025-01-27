package org.edtf4k

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

        REGEX.matchEntire(dateString)?.let {
            if (parseYear(it)) {
                if (parseMonth(it)) {
                    if (parseDay(it)) {
                        parseTime(it)
                    }
                }
            }
            return EdtfDate(status, year, month, day, hour, minute, second, timezoneOffset)
        } ?: return EdtfDate(EdtfDateStatus.INVALID)
    }

    private fun parseYear(matchResult: MatchResult): Boolean {
        val yearString = matchResult.groups[YEAR_NUM_PATTERN_NAME]?.value ?: ""

        year = EdtfDateComponent(yearString)

        if (year.isInvalid) {
            status = EdtfDateStatus.INVALID
            return false
        }

        val yearPrecisionString = matchResult.groups[YEAR_PRECISION_PATTERN_NAME]?.value
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

        year.setFlags(matchResult.groups[YEAR_OPEN_FLAGS_PATTERN_NAME]?.value + matchResult.groups[YEAR_CLOSE_FLAGS_PATTERN_NAME]?.value)

        return true
    }

    private fun parseMonth(matchResult: MatchResult): Boolean {
        val monthString = matchResult.groups[MONTH_NUM_PATTERN_NAME]?.value ?: return false

        month = EdtfDateComponent(monthString)

        if (month.isInvalid) {
            status = EdtfDateStatus.INVALID
            return false
        }

        val openFlags = matchResult.groups[MONTH_OPEN_FLAGS_PATTERN_NAME]?.value ?: ""
        val closeFlags = matchResult.groups[MONTH_CLOSE_FLAGS_PATTERN_NAME]?.value ?: ""

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

    private fun parseDay(matchResult: MatchResult): Boolean {
        val dayString = matchResult.groups[DAY_NUM_PATTERN_NAME]?.value ?: return false

        day = EdtfDateComponent(dayString)

        if (day.isInvalid) {
            status = EdtfDateStatus.INVALID
            return false
        }

        if (!validateDay()) {
            status = EdtfDateStatus.INVALID
            return false
        }

        val openFlags = matchResult.groups[DAY_OPEN_FLAGS_PATTERN_NAME]?.value ?: ""
        val closeFlags = matchResult.groups[DAY_CLOSE_FLAGS_PATTERN_NAME]?.value ?: ""

        day.setFlags(openFlags)
        day.setFlags(closeFlags)
        month.setFlags(closeFlags)
        year.setFlags(closeFlags)

        return true
    }

    private fun parseTime(matchResult: MatchResult) {
        val hourString = matchResult.groups[HOUR_PATTERN_NAME]?.value

        if (hourString.isNullOrBlank()) {
            return
        }

        hour = hourString.toInt()
        if (valueOutOfRange(hour, 23)) {
            status = EdtfDateStatus.INVALID
            return
        }

        minute = matchResult.groups[MINUTE_PATTERN_NAME]!!.value.toInt()
        if (valueOutOfRange(minute, 59)) {
            status = EdtfDateStatus.INVALID
            return
        }

        second = matchResult.groups[SECOND_PATTERN_NAME]!!.value.toInt()
        if (valueOutOfRange(second, 59)) {
            status = EdtfDateStatus.INVALID
            return
        }

        if (!(matchResult.groups[TZ_UTC_PATTERN_NAME]?.value).isNullOrBlank()) {
            timezoneOffset = 0
        } else {
            val tzSignString = matchResult.groups[TZ_SIGN_PATTERN_NAME]?.value
            if (!tzSignString.isNullOrBlank()) {
                val tzSign = if ((tzSignString[0] == '-')) -1 else 1
                val tzHour = matchResult.groups[TZ_HOUR_PATTERN_NAME]!!.value.toInt()
                val tzMinute = matchResult.groups[TZ_MINUTE_PATTERN_NAME]?.value?.toInt() ?: 0
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
        private const val YEAR_LONG_INDICATOR_PATTERN_NAME = "yearlongind"
        private const val YEAR_OPEN_FLAGS_PATTERN_NAME = "yearopenflags"
        private const val YEAR_NUM_PATTERN_NAME = "yearnum"
        private const val YEAR_PRECISION_PATTERN_NAME = "yearprecision"
        private const val YEAR_CLOSE_FLAGS_PATTERN_NAME = "yearcloseflags"
        private const val MONTH_OPEN_FLAGS_PATTERN_NAME = "monthopenflags"
        private const val MONTH_NUM_PATTERN_NAME = "monthnum"
        private const val MONTH_CLOSE_FLAGS_PATTERN_NAME = "monthcloseflags"
        private const val DAY_OPEN_FLAGS_PATTERN_NAME = "dayopenflags"
        private const val DAY_NUM_PATTERN_NAME = "daynum"
        private const val DAY_CLOSE_FLAGS_PATTERN_NAME = "daycloseflags"
        private const val HOUR_PATTERN_NAME = "hour"
        private const val MINUTE_PATTERN_NAME = "minute"
        private const val SECOND_PATTERN_NAME = "second"
        private const val TZ_UTC_PATTERN_NAME = "tzutc"
        private const val TZ_SIGN_PATTERN_NAME = "tzsign"
        private const val TZ_HOUR_PATTERN_NAME = "tzhour"
        private const val TZ_MINUTE_PATTERN_NAME = "tzminute"
        private val REGEX = """
			(?<$YEAR_LONG_INDICATOR_PATTERN_NAME>Y?)
			(?>(?<$YEAR_OPEN_FLAGS_PATTERN_NAME>[~?%]{0,2})(?<$YEAR_NUM_PATTERN_NAME>[+-]?(?:\d+E\d+|[0-9X]+))(?>S(?<$YEAR_PRECISION_PATTERN_NAME>\d+))?(?<$YEAR_CLOSE_FLAGS_PATTERN_NAME>[~?%]{0,2}))
			(?>-(?>(?<$MONTH_OPEN_FLAGS_PATTERN_NAME>[~?%]{0,2})(?<$MONTH_NUM_PATTERN_NAME>(?>[0-9X]{1,2}))(?<$MONTH_CLOSE_FLAGS_PATTERN_NAME>[~?%]{0,2}))
			(?>-(?>(?<$DAY_OPEN_FLAGS_PATTERN_NAME>[~?%]{0,2})(?<$DAY_NUM_PATTERN_NAME>(?>[0-9X]{1,2}))(?<$DAY_CLOSE_FLAGS_PATTERN_NAME>[~?%]{0,2}))
			(?>T(?<$HOUR_PATTERN_NAME>[0-9]{2}):?(?<$MINUTE_PATTERN_NAME>[0-9]{2}):?(?<$SECOND_PATTERN_NAME>[0-9]{2})(?>(?<$TZ_UTC_PATTERN_NAME>Z)|(?<$TZ_SIGN_PATTERN_NAME>[+-])(?<$TZ_HOUR_PATTERN_NAME>[01][0-9])(?>:(?<$TZ_MINUTE_PATTERN_NAME>[0-5][0-9]))?)?)?)?)?$
			""".trimIndent().toRegex(RegexOption.COMMENTS)
        private val MONTHS_WITH_30_DAYS: Set<Int> = setOf(4, 6, 9, 11)

        fun parse(dateString: String, hasInterval: Boolean): EdtfDate {
            return EdtfDateParser().parseInternal(dateString, hasInterval)
        }
    }
}