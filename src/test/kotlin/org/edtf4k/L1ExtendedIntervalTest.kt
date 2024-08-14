package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L1ExtendedIntervalTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int?, approximateStartYear: Boolean, uncertainStartYear: Boolean, startMonth: Int?, approximateStartMonth: Boolean, uncertainStartMonth: Boolean, startDay: Int?, approximateStartDay: Boolean, uncertainStartDay: Boolean, endStatus: EdtfDateStatus, endYear: Int?, approximateEndYear: Boolean, uncertainEndYear: Boolean, endMonth: Int?, approximateEndMonth: Boolean, uncertainEndMonth: Boolean, endDay: Int?, approximateEndDay: Boolean, uncertainEndDay: Boolean, expectedString: String) {
        val pair = EdtfDatePair(data)

        assertSoftly { softly ->
            softly.assertThat(pair.start).satisfies({
                assertThat(it.status).isEqualTo(startStatus)
                if (startYear == null) {
                    assertThat(it.year.hasValue()).isFalse
                } else {
                    assertThat(it.year.hasValue()).isTrue
                    assertThat(it.year.isApproximate).isEqualTo(approximateStartYear)
                    assertThat(it.year.isUncertain).isEqualTo(uncertainStartYear)
                    assertThat(it.year.value).isEqualTo(startYear)
                }
                if (startMonth == null) {
                    assertThat(it.month.hasValue()).isFalse
                } else {
                    assertThat(it.month.hasValue()).isTrue
                    assertThat(it.month.isApproximate).isEqualTo(approximateStartMonth)
                    assertThat(it.month.isUncertain).isEqualTo(uncertainStartMonth)
                    assertThat(it.month.value).isEqualTo(startMonth)
                }
                if (startDay == null) {
                    assertThat(it.day.hasValue()).isFalse
                } else {
                    assertThat(it.day.hasValue()).isTrue
                    assertThat(it.day.isApproximate).isEqualTo(approximateStartDay)
                    assertThat(it.day.isUncertain).isEqualTo(uncertainStartDay)
                    assertThat(it.day.value).isEqualTo(startDay)
                }
            })
            softly.assertThat(pair.end).satisfies({
                assertThat(it.status).isEqualTo(endStatus)
                if (endYear == null) {
                    assertThat(it.year.hasValue()).isFalse
                } else {
                    assertThat(it.year.hasValue()).isTrue
                    assertThat(it.year.isApproximate).isEqualTo(approximateEndYear)
                    assertThat(it.year.isUncertain).isEqualTo(uncertainEndYear)
                    assertThat(it.year.value).isEqualTo(endYear)
                }
                if (endMonth == null) {
                    assertThat(it.month.hasValue()).isFalse
                } else {
                    assertThat(it.month.hasValue()).isTrue
                    assertThat(it.month.isApproximate).isEqualTo(approximateEndMonth)
                    assertThat(it.month.isUncertain).isEqualTo(uncertainEndMonth)
                    assertThat(it.month.value).isEqualTo(endMonth)
                }
                if (endDay == null) {
                    assertThat(it.day.hasValue()).isFalse
                } else {
                    assertThat(it.day.hasValue()).isTrue
                    assertThat(it.day.isApproximate).isEqualTo(approximateEndDay)
                    assertThat(it.day.isUncertain).isEqualTo(uncertainEndDay)
                    assertThat(it.day.value).isEqualTo(endDay)
                }
            })
            softly.assertThat(pair.toString()).isEqualTo(expectedString)
        }
    }

    companion object {
        @JvmStatic
        fun test(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("/2006", EdtfDateStatus.UNKNOWN, null, false, false, null, false, false, null, false, false, EdtfDateStatus.NORMAL, 2006, false, false, null, false, false, null, false, false, "/2006"),
                Arguments.of("2004-06-01/", EdtfDateStatus.NORMAL, 2004, false, false, 6, false, false, 1, false, false, EdtfDateStatus.UNKNOWN, null, false, false, null, false, false, null, false, false, "2004-06-01/"),
                Arguments.of("2004-01-01/..", EdtfDateStatus.NORMAL, 2004, false, false, 1, false, false, 1, false, false, EdtfDateStatus.OPEN, null, false, false, null, false, false, null, false, false, "2004-01-01/.."),
                Arguments.of("../2004-01-01", EdtfDateStatus.OPEN, null, false, false, null, false, false, null, false, false, EdtfDateStatus.NORMAL, 2004, false, false, 1, false, false, 1, false, false, "../2004-01-01"),
                Arguments.of("~1984/2004-06", EdtfDateStatus.NORMAL, 1984, true, false, null, false, false, null, false, false, EdtfDateStatus.NORMAL, 2004, false, false, 6, false, false, null, false, false, "~1984/2004-06"),
                Arguments.of("1984/2004-06~", EdtfDateStatus.NORMAL, 1984, false, false, null, false, false, null, false, false, EdtfDateStatus.NORMAL, 2004, true, false, 6, true, false, null, false, false, "1984/2004-06~"),
                Arguments.of("~1984/~2004", EdtfDateStatus.NORMAL, 1984, true, false, null, false, false, null, false, false, EdtfDateStatus.NORMAL, 2004, true, false, null, false, false, null, false, false, "~1984/~2004"),
                Arguments.of("?1984/%2004", EdtfDateStatus.NORMAL, 1984, false, true, null, false, false, null, false, false, EdtfDateStatus.NORMAL, 2004, true, true, null, false, false, null, false, false, "?1984/%2004"),
                Arguments.of("1984-06?/2004-08?", EdtfDateStatus.NORMAL, 1984, false, true, 6, false, true, null, false, false, EdtfDateStatus.NORMAL, 2004, false, true, 8, false, true, null, false, false, "1984-06?/2004-08?"),
                Arguments.of("1984-06-02?/2004-08-08~", EdtfDateStatus.NORMAL, 1984, false, true, 6, false, true, 2, false, true, EdtfDateStatus.NORMAL, 2004, true, false, 8, true, false, 8, true, false, "1984-06-02?/2004-08-08~"),
                Arguments.of("1984-06-02?/", EdtfDateStatus.NORMAL, 1984, false, true, 6, false, true, 2, false, true, EdtfDateStatus.UNKNOWN, null, false, false, null, false, false, null, false, false, "1984-06-02?/")
            )
        }
    }
}