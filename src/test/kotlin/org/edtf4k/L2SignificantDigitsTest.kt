package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L2SignificantDigitsTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int, startYearInvalid: Boolean, significantDigits: Int, expectedString: String) {
        val pair = EdtfDatePair(data)

        assertSoftly { softly ->
            softly.assertThat(pair.start).satisfies({
                assertThat(it.status).isEqualTo(startStatus)
                assertThat(it.year.hasValue()).isTrue
                assertThat(it.year.isInvalid).isEqualTo(startYearInvalid)
                assertThat(it.year.significantDigits).isEqualTo(significantDigits)
                assertThat(it.year.value).isEqualTo(startYear)
            })
            softly.assertThat(pair.end.status).isEqualTo(EdtfDateStatus.UNUSED)
            softly.assertThat(pair.toString()).isEqualTo(expectedString)
        }
    }

    companion object {
        @JvmStatic
        fun test(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("1960S2", EdtfDateStatus.NORMAL, 1960, false, 2, "1960S2"),
                Arguments.of("-1960S2", EdtfDateStatus.NORMAL, -1960, false, 2, "-1960S2"),
                Arguments.of("Y196000002S2", EdtfDateStatus.NORMAL, 196000002, false, 2, "Y196000002S2"),
                Arguments.of("Y1960E2S2", EdtfDateStatus.NORMAL, 196000, false, 2, "Y196000S2"),
                Arguments.of("Y196XXXS2", EdtfDateStatus.NORMAL, 196000, false, 2, "Y196XXXS2"),
                Arguments.of("Y1960S5", EdtfDateStatus.INVALID, 1960, true, 5, "1960S5"),
                Arguments.of("Y-1960S5", EdtfDateStatus.INVALID, -1960, true, 5, "-1960S5"),
            )
        }
    }
}