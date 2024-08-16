package org.edtf4k

import kotlin.math.abs

class EdtfDate(
    val status: EdtfDateStatus,
    val year: EdtfDateComponent = EdtfDateComponent(),
    val month: EdtfDateComponent = EdtfDateComponent(),
    val day: EdtfDateComponent = EdtfDateComponent(),
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val timezoneOffset: Int? = null
) : EdtfDateType {
    fun hasTimezoneOffset(): Boolean = timezoneOffset != null

    fun getSubYearGrouping(): EdtfSubYearGrouping? {
        return EdtfSubYearGrouping.fromValue(month.value)
    }

    override fun toString(): String {
        if (status == EdtfDateStatus.UNUSED || status == EdtfDateStatus.UNKNOWN) {
            return String()
        }

        if (status == EdtfDateStatus.OPEN) {
            return RANGE_DELIMITER
        }

        if (!year.hasValue()) {
            return String()
        }

        val singleUncertain = year.isUncertain && !month.isUncertain
        val singleApproximate = year.isApproximate && !month.isApproximate

        val work = StringBuilder()
        addFlagsToString(work, singleUncertain, singleApproximate)

        work.append(year.getPaddedString(4))

        if (month.hasValue()) {
            work.append('-')

            val leftMonthUncertain = !year.isUncertain && month.isUncertain
            val leftMonthApproximate = !year.isApproximate && month.isApproximate

            addFlagsToString(work, leftMonthUncertain, leftMonthApproximate)

            work.append(month.getPaddedString(2))

            val rightMonthUncertain = year.isUncertain && month.isUncertain && !day.isUncertain
            val rightMonthApproximate = year.isApproximate && month.isApproximate && !day.isApproximate

            addFlagsToString(work, rightMonthUncertain, rightMonthApproximate)

            if (day.hasValue()) {
                work.append('-')

                val leftDayUncertain = !(year.isUncertain && month.isUncertain) && day.isUncertain
                val leftDayApproximate = !(year.isApproximate && month.isApproximate) && day.isApproximate

                addFlagsToString(work, leftDayUncertain, leftDayApproximate)

                work.append(day.getPaddedString(2))

                val rightDayUncertain = year.isUncertain && month.isUncertain && day.isUncertain
                val rightDayApproximate = year.isApproximate && month.isApproximate && day.isApproximate

                addFlagsToString(work, rightDayUncertain, rightDayApproximate)

                if (hour > 0 || minute > 0 || second > 0) {
                    work.append('T')
                        .append(String.format("%02d", hour))
                        .append(':')
                        .append(String.format("%02d", minute))
                        .append(':')
                        .append(String.format("%02d", second))

                    if (timezoneOffset != null) {
                        if (timezoneOffset == 0) {
                            work.append('Z')
                        } else {
                            val tzHour = timezoneOffset / 60
                            val tzMinute = timezoneOffset % 60
                            work.append((if (timezoneOffset < 0) '-' else '+'))
                                .append(String.format("%02d", abs(tzHour)))
                                .append(':')
                                .append(String.format("%02d", tzMinute))
                        }
                    }
                }
            }
        }

        return work.toString()
    }

    private fun addFlagsToString(builder: StringBuilder, uncertain: Boolean, approximate: Boolean) {
        if (uncertain || approximate) {
            builder.append(if (uncertain && approximate) APPROXIMATE_UNCERTAIN else if (uncertain) UNCERTAIN else APPROXIMATE)
        }
    }

    companion object {
        fun parse(dateString: String, hasInterval: Boolean = false): EdtfDate {
            return EdtfDateParser.parse(dateString, hasInterval)
        }
    }
}

enum class EdtfDateStatus {
    NORMAL,
    UNKNOWN,
    OPEN,
    UNUSED,
    INVALID
}

enum class EdtfSubYearGrouping(val subYearGroupingValue: Int) {
    SPRING(21),
    SUMMER(22),
    AUTUMN(23),
    WINTER(24),
    SPRING_NORTHERN_HEMISPHERE(25),
    SUMMER_NORTHERN_HEMISPHERE(26),
    AUTUMN_NORTHERN_HEMISPHERE(27),
    WINTER_NORTHERN_HEMISPHERE(28),
    SPRING_SOUTHERN_HEMISPHERE(29),
    SUMMER_SOUTHERN_HEMISPHERE(30),
    AUTUMN_SOUTHERN_HEMISPHERE(31),
    WINTER_SOUTHERN_HEMISPHERE(32),
    QUARTER_1(33),
    QUARTER_2(34),
    QUARTER_3(35),
    QUARTER_4(36),
    QUADRIMESTER_1(37),
    QUADRIMESTER_2(38),
    QUADRIMESTER_3(39),
    SEMESTRAL_1(40),
    SEMESTRAL_2(41);

    companion object {
        fun fromValue(subYearGroupingValue: Int): EdtfSubYearGrouping? {
            for (edtfSubYearGrouping in EdtfSubYearGrouping.entries) {
                if (edtfSubYearGrouping.subYearGroupingValue == subYearGroupingValue) {
                    return edtfSubYearGrouping
                }
            }
            return null
        }
    }
}