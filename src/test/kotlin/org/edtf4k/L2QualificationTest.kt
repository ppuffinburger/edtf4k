package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L2QualificationTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int, startYearUncertain: Boolean, startYearApproximate: Boolean, startMonth: Int?, startMonthUncertain: Boolean, startMonthApproximate: Boolean, startDay: Int?, startDayUncertain: Boolean, startDayApproximate: Boolean, expectedString: String) {
        val pair = EdtfDatePair(data)

        assertSoftly { softly ->
            softly.assertThat(pair.start).satisfies({
                assertThat(it.status).isEqualTo(startStatus)
                assertThat(it.year.hasValue()).isTrue
                assertThat(it.year.isUncertain).isEqualTo(startYearUncertain)
                assertThat(it.year.isApproximate).isEqualTo(startYearApproximate)
                assertThat(it.year.value).isEqualTo(startYear)
                if (startMonth == null) {
                    assertThat(it.month.hasValue()).isFalse
                } else {
                    assertThat(it.month.hasValue()).isTrue
                    assertThat(it.month.isUncertain).isEqualTo(startMonthUncertain)
                    assertThat(it.month.isApproximate).isEqualTo(startMonthApproximate)
                    assertThat(it.month.value).isEqualTo(startMonth)
                }
                if (startDay == null) {
                    assertThat(it.day.hasValue()).isFalse
                } else {
                    assertThat(it.day.hasValue()).isTrue
                    assertThat(it.day.isUncertain).isEqualTo(startDayUncertain)
                    assertThat(it.day.isApproximate).isEqualTo(startDayApproximate)
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
                Arguments.of("?2004-06-11", EdtfDateStatus.NORMAL, 2004, true, false, 6, false, false, 11, false, false, "?2004-06-11"),
                Arguments.of("2004-06~-11", EdtfDateStatus.NORMAL, 2004, false, true, 6, false, true, 11, false, false, "2004-06~-11"),
                Arguments.of("2004-?06-11", EdtfDateStatus.NORMAL, 2004, false, false, 6, true, false, 11, false, false, "2004-?06-11"),
                Arguments.of("2004-06-~11", EdtfDateStatus.NORMAL, 2004, false, false, 6, false, false, 11, false, true, "2004-06-~11"),
                Arguments.of("2004-%06", EdtfDateStatus.NORMAL, 2004, false, false, 6, true, true, null, false, false, "2004-%06"),
                Arguments.of("2004-?06-?11", EdtfDateStatus.NORMAL, 2004, false, false, 6, true, false, 11, true, false, "2004-?06-?11"),
                Arguments.of("?2004-06-~11", EdtfDateStatus.NORMAL, 2004, true, false, 6, false, false, 11, false, true, "?2004-06-~11"),
                Arguments.of("2004-~06?", EdtfDateStatus.NORMAL, 2004, true, false, 6, true, true, null, false, false, "2004-~06?"),
                Arguments.of("?2004?-%06", EdtfDateStatus.NORMAL, 2004, true, false, 6, true, true, null, false, false, "2004-~06?"),
                Arguments.of("?2004-~06-~04", EdtfDateStatus.NORMAL, 2004, true, false, 6, false, true, 4, false, true, "?2004-~06-~04"),
                Arguments.of("2011-~06-~04", EdtfDateStatus.NORMAL, 2011, false, false, 6, false, true, 4, false, true, "2011-~06-~04"),
                Arguments.of("2011-23~", EdtfDateStatus.NORMAL, 2011, false, true, 23, false, true, null, false, false, "2011-23~")
            )
        }
    }
}