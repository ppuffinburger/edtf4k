package org.edtf4k

import java.util.*
import kotlin.math.abs

class EdtfDateComponent(input: String = "") {
    val unspecifiedMask: BitSet
    var value: Int = Int.MIN_VALUE
        private set
    var insignificantDigits: Int = 0
        internal set
    var significantDigits: Int = 0
        internal set
    var isInvalid: Boolean = false
        internal set
    var isApproximate: Boolean = false
        internal set
    var isUncertain: Boolean = false
        internal set

    init {
        val builder = StringBuilder(input)
        with(builder) {
            if (isBlank()) {
                unspecifiedMask = BitSet(0)
            } else {
                unspecifiedMask = BitSet(length)

                val firstUnspecified = indexOfFirst { it == UNSPECIFIED }

                if (firstUnspecified != -1 && lastIndexOfAny(NUMERICS) > firstUnspecified) {
                    isInvalid = true
                } else {
                    if (firstUnspecified != -1) {
                        for (index in firstUnspecified until length) {
                            if (get(index) == UNSPECIFIED) {
                                unspecifiedMask.flip(index)
                                setCharAt(index, '0')
                            }
                        }
                    }

                    if (startsWith(LONG_YEAR)) {
                        deleteCharAt(0)
                    }

                    value = if (contains(EXPONENT)) {
                        toString().toDouble().toInt()
                    } else {
                        toString().toInt()
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return getPaddedString(0)
    }

    fun hasValue(): Boolean {
        return value != Int.MIN_VALUE
    }

    fun getPaddedString(padding: Int): String {
        if (value == Int.MIN_VALUE) {
            return ""
        }

        val work = StringBuilder(abs(value.toDouble()).toInt().toString().padStart(padding, '0'))
        with (work) {
            for (index in 0 until unspecifiedMask.length()) {
                if (unspecifiedMask[index]) {
                    setCharAt(index, UNSPECIFIED)
                }
            }

            for (index in length until padding) {
                insert(0, '0')
            }

            if (value < 0) {
                insert(0, '-')
            }

            if (value > 9999 || value < -9999) {
                insert(0, LONG_YEAR)
            }

            if (significantDigits > 0) {
                append(SIGNIFICANT_DIGIT).append(significantDigits)
            }

            return toString()
        }
    }

    internal fun setFlags(flags: String) {
        if (flags.isNotBlank()) {
            isApproximate = isApproximate or flags.any { it == APPROXIMATE || it == APPROXIMATE_UNCERTAIN }
            isUncertain = isUncertain or flags.any { it == UNCERTAIN || it == APPROXIMATE_UNCERTAIN }
        }
    }

    companion object {
        internal val NUMERICS = "0123456789".toCharArray()
    }
}