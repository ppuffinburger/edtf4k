package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L2SubYearGroupingTest : WithAssertions {
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
                Arguments.of("2001-25", EdtfDateStatus.NORMAL, 2001, 25, EdtfSubYearGrouping.SPRING_NORTHERN_HEMISPHERE, "2001-25"),
                Arguments.of("2003-26", EdtfDateStatus.NORMAL, 2003, 26, EdtfSubYearGrouping.SUMMER_NORTHERN_HEMISPHERE, "2003-26"),
                Arguments.of("2000-27", EdtfDateStatus.NORMAL, 2000, 27, EdtfSubYearGrouping.AUTUMN_NORTHERN_HEMISPHERE, "2000-27"),
                Arguments.of("2003-28", EdtfDateStatus.NORMAL, 2003, 28, EdtfSubYearGrouping.WINTER_NORTHERN_HEMISPHERE, "2003-28"),
                Arguments.of("2000-29", EdtfDateStatus.NORMAL, 2000, 29, EdtfSubYearGrouping.SPRING_SOUTHERN_HEMISPHERE, "2000-29"),
                Arguments.of("2003-30", EdtfDateStatus.NORMAL, 2003, 30, EdtfSubYearGrouping.SUMMER_SOUTHERN_HEMISPHERE, "2003-30"),
                Arguments.of("2000-31", EdtfDateStatus.NORMAL, 2000, 31, EdtfSubYearGrouping.AUTUMN_SOUTHERN_HEMISPHERE, "2000-31"),
                Arguments.of("2003-32", EdtfDateStatus.NORMAL, 2003, 32, EdtfSubYearGrouping.WINTER_SOUTHERN_HEMISPHERE, "2003-32"),
                Arguments.of("2000-33", EdtfDateStatus.NORMAL, 2000, 33, EdtfSubYearGrouping.QUARTER_1, "2000-33"),
                Arguments.of("2003-34", EdtfDateStatus.NORMAL, 2003, 34, EdtfSubYearGrouping.QUARTER_2, "2003-34"),
                Arguments.of("2000-35", EdtfDateStatus.NORMAL, 2000, 35, EdtfSubYearGrouping.QUARTER_3, "2000-35"),
                Arguments.of("2003-36", EdtfDateStatus.NORMAL, 2003, 36, EdtfSubYearGrouping.QUARTER_4, "2003-36"),
                Arguments.of("2000-37", EdtfDateStatus.NORMAL, 2000, 37, EdtfSubYearGrouping.QUADRIMESTER_1, "2000-37"),
                Arguments.of("2003-38", EdtfDateStatus.NORMAL, 2003, 38, EdtfSubYearGrouping.QUADRIMESTER_2, "2003-38"),
                Arguments.of("2000-39", EdtfDateStatus.NORMAL, 2000, 39, EdtfSubYearGrouping.QUADRIMESTER_3, "2000-39"),
                Arguments.of("2000-40", EdtfDateStatus.NORMAL, 2000, 40, EdtfSubYearGrouping.SEMESTRAL_1, "2000-40"),
                Arguments.of("2010-41", EdtfDateStatus.NORMAL, 2010, 41, EdtfSubYearGrouping.SEMESTRAL_2, "2010-41")
            )
        }
    }
}