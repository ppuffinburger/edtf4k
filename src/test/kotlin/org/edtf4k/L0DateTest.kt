package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L0DateTest: WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int?, startMonth: Int?, startDay: Int?, expectedString: String) {
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
            })
            softly.assertThat(pair.end.status).isEqualTo(EdtfDateStatus.UNUSED)
            softly.assertThat(pair.toString()).isEqualTo(expectedString)
        }
    }

    companion object {
        @JvmStatic
        fun test(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("2001-02-03", EdtfDateStatus.NORMAL, 2001, 2, 3, "2001-02-03"),
                Arguments.of("2008-12", EdtfDateStatus.NORMAL, 2008, 12, null, "2008-12"),
                Arguments.of("2008", EdtfDateStatus.NORMAL, 2008, null, null, "2008"),
                Arguments.of("-0999", EdtfDateStatus.NORMAL, -999, null, null, "-0999"),
                Arguments.of("0000", EdtfDateStatus.NORMAL, 0, null, null, "0000"),
                Arguments.of("", EdtfDateStatus.UNUSED, null, null, null, ""),
                Arguments.of("-", EdtfDateStatus.INVALID, null, null, null, ""),
                Arguments.of("2019-01-00", EdtfDateStatus.INVALID, 2019, 1, 0, "2019-01-00"),
                Arguments.of("2019-00", EdtfDateStatus.INVALID, 2019, 0, null, "2019-00"),
                Arguments.of("2019-06-31", EdtfDateStatus.INVALID, 2019, 6, 31, "2019-06-31"),
                Arguments.of("2020-02-29", EdtfDateStatus.INVALID, 2020, 2, 29, "2020-02-29"),
                Arguments.of("1900-02-29", EdtfDateStatus.INVALID, 1900, 2, 29, "1900-02-29"),
                Arguments.of("2000-02-29", EdtfDateStatus.INVALID, 2000, 2, 29, "2000-02-29")
            )
        }
    }
}