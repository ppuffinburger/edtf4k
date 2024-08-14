package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L1SubYearGroupingTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int, startMonth: Int, startSubYearGrouping: EdtfSubYearGrouping, expectedString: String) {
        val pair = EdtfDatePair(data)

        assertSoftly { softly ->
            softly.assertThat(pair.start).satisfies({
                assertThat(it.status).isEqualTo(startStatus)
                assertThat(it.year.hasValue()).isTrue
                assertThat(it.year.value).isEqualTo(startYear)
                assertThat(it.month.hasValue()).isTrue
                assertThat(it.month.value).isEqualTo(startMonth)
                assertThat(it.getSubYearGrouping()).isEqualTo(startSubYearGrouping)
            })
            softly.assertThat(pair.end.status).isEqualTo(EdtfDateStatus.UNUSED)
            softly.assertThat(pair.toString()).isEqualTo(expectedString)
        }
    }

    companion object {
        @JvmStatic
        fun test(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("2001-21", EdtfDateStatus.NORMAL, 2001, 21, EdtfSubYearGrouping.SPRING, "2001-21"),
                Arguments.of("2003-22", EdtfDateStatus.NORMAL, 2003, 22, EdtfSubYearGrouping.SUMMER, "2003-22"),
                Arguments.of("2000-23", EdtfDateStatus.NORMAL, 2000, 23, EdtfSubYearGrouping.AUTUMN, "2000-23"),
                Arguments.of("2010-24", EdtfDateStatus.NORMAL, 2010, 24, EdtfSubYearGrouping.WINTER, "2010-24")
            )
        }
    }
}