package org.edtf4k

import java.util.regex.Pattern

object EdtfDateFactory {
    private const val EDTF_SET_REPRESENTATION_REGEX: String = "^[\\[|{].*[]}]$"
    private val EDTF_SET_REPRESENTATION_PATTERN: Pattern = Pattern.compile(EDTF_SET_REPRESENTATION_REGEX)

    fun parse(dateString: String): EdtfDateType {
        return if (EDTF_SET_REPRESENTATION_PATTERN.matcher(dateString).matches()) {
            EdtfDateSet(dateString)
        } else {
            EdtfDatePair(dateString)
        }
    }
}

sealed class EdtfDateType