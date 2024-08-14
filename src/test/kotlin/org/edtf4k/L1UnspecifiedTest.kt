package org.edtf4k

import org.assertj.core.api.SoftAssertions.assertSoftly
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

class L1UnspecifiedTest : WithAssertions {
    @ParameterizedTest
    @MethodSource
    fun test(data: String, startStatus: EdtfDateStatus, startYear: Int?, startYearInvalid: Boolean, startYearMaskString: String, startMonth: Int?, startMonthInvalid: Boolean, startMonthMaskString: String, startDay: Int?, startDayInvalid: Boolean, startDayMaskString: String, expectedString: String) {
        val pair = EdtfDatePair(data)

        assertSoftly { softly ->
            softly.assertThat(pair.start).satisfies({
                assertThat(it.status).isEqualTo(startStatus)
                if (startYear == null) {
                    assertThat(it.year.hasValue()).isFalse
                    assertThat(it.year.isInvalid).isEqualTo(startYearInvalid)
                } else {
                    assertThat(it.year.hasValue()).isTrue
                    assertThat(it.year.isInvalid).isEqualTo(startYearInvalid)
                    assertThat(it.year.unspecifiedMask).isEqualTo(createBitSet(startYearMaskString))
                    assertThat(it.year.value).isEqualTo(startYear)
                }
                if (startMonth == null) {
                    assertThat(it.month.hasValue()).isFalse
                    assertThat(it.month.isInvalid).isEqualTo(startMonthInvalid)
                } else {
                    assertThat(it.month.hasValue()).isTrue
                    assertThat(it.month.isInvalid).isEqualTo(startMonthInvalid)
                    assertThat(it.month.unspecifiedMask).isEqualTo(createBitSet(startMonthMaskString))
                    assertThat(it.month.value).isEqualTo(startMonth)
                }
                if (startDay == null) {
                    assertThat(it.day.hasValue()).isFalse
                    assertThat(it.day.isInvalid).isEqualTo(startDayInvalid)
                } else {
                    assertThat(it.day.hasValue()).isTrue
                    assertThat(it.day.isInvalid).isEqualTo(startDayInvalid)
                    assertThat(it.day.unspecifiedMask).isEqualTo(createBitSet(startDayMaskString))
                    assertThat(it.day.value).isEqualTo(startDay)
                }
            })
            softly.assertThat(pair.end.status).isEqualTo(EdtfDateStatus.UNUSED)
            softly.assertThat(pair.toString()).isEqualTo(expectedString)
        }

    }

    private fun createBitSet(mask: String): BitSet {
        val bits = BitSet(mask.length)
        for (index in mask.indices) {
            if (mask[index] == '1') {
                bits.set(index)
            }
        }
        return bits
    }

    companion object {
        @JvmStatic
        fun test(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("199X", EdtfDateStatus.NORMAL, 1990, false, "0001", null, false, "00", null, false, "00", "199X"),
                Arguments.of("19XX", EdtfDateStatus.NORMAL, 1900, false, "0011", null, false, "00", null, false, "00", "19XX"),
                Arguments.of("1999-XX", EdtfDateStatus.NORMAL, 1999, false, "0000", 0, false, "11", null, false, "00", "1999-XX"),
                Arguments.of("1999-01-XX", EdtfDateStatus.NORMAL, 1999, false, "0000", 1, false, "00", 0, false, "11", "1999-01-XX"),
                Arguments.of("1999-XX-XX", EdtfDateStatus.NORMAL, 1999, false, "0000", 0, false, "11", 0, false, "11", "1999-XX-XX"),
                Arguments.of("1X99", EdtfDateStatus.INVALID, null, true, "0000", null, false, "00", null, false, "00", ""),
                Arguments.of("X199", EdtfDateStatus.INVALID, null, true, "0000", null, false, "00", null, false, "00", ""),
                Arguments.of("2199-X0", EdtfDateStatus.INVALID, 2199, false, "0000", null, true, "00", null, false, "00", "2199"),
                Arguments.of("2199-10-X0", EdtfDateStatus.INVALID, 2199, false, "0000", 10, false, "00", null, true, "00", "2199-10")
            )
        }
    }
}