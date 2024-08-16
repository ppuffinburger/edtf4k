package org.edtf4k

import java.util.regex.Pattern

object EdtfDateFactory {
    private const val EDTF_SET_REPRESENTATION_REGEX: String = "^[\\[|{].*[]}]$"
    private val EDTF_SET_REPRESENTATION_PATTERN: Pattern = Pattern.compile(EDTF_SET_REPRESENTATION_REGEX)

    fun parse(dateString: String): EdtfDateType {
        return if (EDTF_SET_REPRESENTATION_PATTERN.matcher(dateString).matches()) {
            EdtfDateSet(dateString)
        } else if (dateString.contains(INTERVAL_DELIMITER) or dateString.contains(RANGE_DELIMITER)) {
            EdtfDatePair(dateString)
        } else {
            EdtfDate.parse(dateString)
        }
    }
}

sealed interface EdtfDateType