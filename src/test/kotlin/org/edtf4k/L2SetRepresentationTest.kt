package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class L2SetRepresentationTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, size: Int, rangePositions: BooleanArray, representation: EdtfDateSet.Representation, expectedString: String) {
        val set = EdtfDateSet(data)

        assertSoftly { softly ->
            softly.assertThat(set.representation).isEqualTo(representation)
            softly.assertThat(set).hasSize(size)
            softly.assertThat(set.toString()).isEqualTo(expectedString)
            set.withIndex().forEach { (index, datePair) ->
                softly.assertThat(datePair.isRange).isEqualTo(rangePositions[index])
            }
        }
    }

    companion object {
        @JvmStatic
        fun test(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("[1667,1668,1670..1672]", 3, booleanArrayOf(false, false, true), EdtfDateSet.Representation.ONE_OF_A_SET, "[1667,1668,1670..1672]"),
                Arguments.of("[..1760-12-03]", 1, booleanArrayOf(true), EdtfDateSet.Representation.ONE_OF_A_SET, "[..1760-12-03]"),
                Arguments.of("[1760-12..]", 1, booleanArrayOf(true), EdtfDateSet.Representation.ONE_OF_A_SET, "[1760-12..]"),
                Arguments.of("[1760-01,1760-02,1760-12..]", 3, booleanArrayOf(false, false, true), EdtfDateSet.Representation.ONE_OF_A_SET, "[1760-01,1760-02,1760-12..]"),
                Arguments.of("[1667,1760-12]", 2, booleanArrayOf(false, false), EdtfDateSet.Representation.ONE_OF_A_SET, "[1667,1760-12]"),
                Arguments.of("[..1984]", 1, booleanArrayOf(true), EdtfDateSet.Representation.ONE_OF_A_SET, "[..1984]"),
                Arguments.of("{1667,1668,1670..1672}", 3, booleanArrayOf(false, false, true), EdtfDateSet.Representation.ALL_MEMBERS, "{1667,1668,1670..1672}"),
                Arguments.of("{1960,1961-12}", 2, booleanArrayOf(false, false), EdtfDateSet.Representation.ALL_MEMBERS, "{1960,1961-12}"),
                Arguments.of("{..1984}", 1, booleanArrayOf(true), EdtfDateSet.Representation.ALL_MEMBERS, "{..1984}")
            )
        }
    }
}