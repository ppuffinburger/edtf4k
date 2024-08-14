package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L0DateTimeTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int?, startMonth: Int?, startDay: Int?, hour: Int, minute: Int, second: Int, timezoneOffset: Int?, expectedString: String) {
        val pair = EdtfDatePair(data)

        assertSoftly { softly ->
            softly.assertThat(pair.start).satisfies({
                assertThat(it.status).isEqualTo(startStatus)
                if (startYear == null) {
                    assertThat(it.year.hasValue()).isFalse
                } else {
                    assertThat(it.year.hasValue()).isTrue
                    assertThat(it.year.value).isEqualTo(startYear)
                }
                if (startMonth == null) {
                    assertThat(it.month.hasValue()).isFalse
                } else {
                    assertThat(it.month.hasValue()).isTrue
                    assertThat(it.month.value).isEqualTo(startMonth)
                }
                if (startDay == null) {
                    assertThat(it.day.hasValue()).isFalse
                } else {
                    assertThat(it.day.hasValue()).isTrue
                    assertThat(it.day.value).isEqualTo(startDay)
                }
                assertThat(it.hour).isEqualTo(hour)
                assertThat(it.minute).isEqualTo(minute)
                assertThat(it.second).isEqualTo(second)
                if (timezoneOffset == null) {
                    assertThat(it.hasTimezoneOffset()).isFalse
                } else {
                    assertThat(it.hasTimezoneOffset()).isTrue
                }
                assertThat(it.timezoneOffset).isEqualTo(timezoneOffset)
            })
            softly.assertThat(pair.end.status).isEqualTo(EdtfDateStatus.UNUSED)
            softly.assertThat(pair.toString()).isEqualTo(expectedString)
        }
    }

    companion object {
        @JvmStatic
        fun test(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("2001-02-03T09:30:01", EdtfDateStatus.NORMAL, 2001, 2, 3, 9, 30, 1, null, "2001-02-03T09:30:01"),
                Arguments.of("2004-01-01T10:10:10Z", EdtfDateStatus.NORMAL, 2004, 1, 1, 10, 10, 10, 0, "2004-01-01T10:10:10Z"),
                Arguments.of("2004-01-01T10:10:10+05:00", EdtfDateStatus.NORMAL, 2004, 1, 1, 10, 10, 10, 300, "2004-01-01T10:10:10+05:00")
            )
        }
    }
}