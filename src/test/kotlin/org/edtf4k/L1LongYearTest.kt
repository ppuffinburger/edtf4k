package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L1LongYearTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int, expectedString: String) {
        val pair = EdtfDatePair(data)

        assertSoftly { softly ->
            softly.assertThat(pair.start).satisfies({
                assertThat(it.status).isEqualTo(startStatus)
                assertThat(it.year.hasValue()).isTrue
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
                Arguments.of("Y170000002", EdtfDateStatus.NORMAL, 170000002, "Y170000002"),
                Arguments.of("Y-170000002", EdtfDateStatus.NORMAL, -170000002, "Y-170000002")
            )
        }
    }
}