package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L1UncertainApproximateTest : WithAssertions {
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
                Arguments.of("?1984", EdtfDateStatus.NORMAL, 1984, true, false, null, false, false, null, false, false, "?1984"),
                Arguments.of("2004-06?", EdtfDateStatus.NORMAL, 2004, true, false, 6, true, false, null, false, false, "2004-06?"),
                Arguments.of("2004-06-11?", EdtfDateStatus.NORMAL, 2004, true, false, 6, true, false, 11, true, false, "2004-06-11?"),
                Arguments.of("~1984", EdtfDateStatus.NORMAL, 1984, false, true, null, false, false, null, false, false, "~1984"),
                Arguments.of("%1984", EdtfDateStatus.NORMAL, 1984, true, true, null, false, false, null, false, false, "%1984")
            )
        }
    }
}