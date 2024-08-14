package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L2ExponentialYearTest : WithAssertions {
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
                Arguments.of("Y17E7", EdtfDateStatus.NORMAL, 170000000, "Y170000000"),
                Arguments.of("Y-17E7", EdtfDateStatus.NORMAL, -170000000, "Y-170000000"),
                Arguments.of("Y17101E4S3", EdtfDateStatus.NORMAL, 171010000, "Y171010000S3")
            )
        }
    }
}