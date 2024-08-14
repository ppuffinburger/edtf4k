package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L0IntervalTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int, startMonth: Int?, startDay: Int?, endStatus: EdtfDateStatus, endYear: Int, endMonth: Int?, endDay: Int?, expectedString: String) {
        val pair = EdtfDatePair(data)

        assertSoftly { softly ->
            softly.assertThat(pair.start).satisfies({
                assertThat(it.status).isEqualTo(startStatus)
                assertThat(it.year.hasValue()).isTrue
                assertThat(it.year.value).isEqualTo(startYear)
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
            softly.assertThat(pair.end).satisfies({
                assertThat(it.status).isEqualTo(endStatus)
                assertThat(it.year.hasValue()).isTrue
                assertThat(it.year.value).isEqualTo(endYear)
                if (endMonth == null) {
                    assertThat(it.month.hasValue()).isFalse
                } else {
                    assertThat(it.month.hasValue()).isTrue
                    assertThat(it.month.value).isEqualTo(endMonth)
                }
                if (endDay == null) {
                    assertThat(it.day.hasValue()).isFalse
                } else {
                    assertThat(it.day.hasValue()).isTrue
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
                Arguments.of("1964/2008", EdtfDateStatus.NORMAL, 1964, null, null, EdtfDateStatus.NORMAL, 2008, null, null, "1964/2008"),
                Arguments.of("2004-06/2006-08", EdtfDateStatus.NORMAL, 2004, 6, null, EdtfDateStatus.NORMAL, 2006, 8, null, "2004-06/2006-08"),
                Arguments.of("2004-02-01/2005-02-08", EdtfDateStatus.NORMAL, 2004, 2, 1, EdtfDateStatus.NORMAL, 2005, 2, 8, "2004-02-01/2005-02-08"),
                Arguments.of("2004-02-01/2005-02", EdtfDateStatus.NORMAL, 2004, 2, 1, EdtfDateStatus.NORMAL, 2005, 2, null, "2004-02-01/2005-02"),
                Arguments.of("2004-02-01/2005", EdtfDateStatus.NORMAL, 2004, 2, 1, EdtfDateStatus.NORMAL, 2005, null, null, "2004-02-01/2005"),
                Arguments.of("2005/2006-02", EdtfDateStatus.NORMAL, 2005, null, null, EdtfDateStatus.NORMAL, 2006, 2, null, "2005/2006-02")
            )
        }
    }
}