package org.edtf4k


object EdtfDateFactory {
    private val EDTF_SET_REPRESENTATION_REGEX = "^[\\[|{].*[]}]$".toRegex()

    fun parse(dateString: String): EdtfDateType {
        return if (EDTF_SET_REPRESENTATION_REGEX.matches(dateString)) {
            EdtfDateSet(dateString)
        } else if (dateString.contains(INTERVAL_DELIMITER) or dateString.contains(RANGE_DELIMITER)) {
            EdtfDatePair(dateString)
        } else {
            EdtfDate.parse(dateString)
        }
    }
}

sealed interface EdtfDateType