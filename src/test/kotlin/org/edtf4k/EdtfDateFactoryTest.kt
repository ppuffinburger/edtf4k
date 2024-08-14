package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.reflect.KClass

class EdtfDateFactoryTest: WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(dateString: String, type: KClass<EdtfDateType>, expectedString: String) {
        val edtfDateType = EdtfDateFactory.parse(dateString)

        assertSoftly { softly ->
            softly.assertThat(edtfDateType::class).isEqualTo(type)
            softly.assertThat(edtfDateType.toString()).isEqualTo(expectedString)
        }
    }

    companion object {
        @JvmStatic
        fun test(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("2001-02-03", EdtfDatePair::class, "2001-02-03"),
                Arguments.of("/2006", EdtfDatePair::class, "/2006"),
                Arguments.of("Y170000002", EdtfDatePair::class, "Y170000002"),
                Arguments.of("?1984", EdtfDatePair::class, "?1984"),
                Arguments.of("199X", EdtfDatePair::class, "199X"),
                Arguments.of("Y17E7", EdtfDatePair::class, "Y170000000"),
                Arguments.of("?2004-06-11", EdtfDatePair::class, "?2004-06-11"),
                Arguments.of("156X-12-25", EdtfDatePair::class, "156X-12-25"),
                Arguments.of("1960S2", EdtfDatePair::class, "1960S2"),
                Arguments.of("[1667,1668,1670..1672]", EdtfDateSet::class, "[1667,1668,1670..1672]"),
                Arguments.of("{1667,1668,1670..1672}", EdtfDateSet::class, "{1667,1668,1670..1672}")
            )
        }
    }
}