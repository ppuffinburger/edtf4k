package org.edtf4k

class EdtfDatePair(dateString: String) : EdtfDateType() {
    val start: EdtfDate
    val end: EdtfDate
    val isRange: Boolean

    init {
        if (dateString.isNotBlank()) {
            val indexOfIntervalDelimiter = dateString.indexOf(INTERVAL_DELIMITER)
            if (indexOfIntervalDelimiter == -1) {
                val indexOfRangeDelimiter = dateString.indexOf(RANGE_DELIMITER)
                if (indexOfRangeDelimiter == -1) {
                    start = EdtfDate.parse(dateString)
                    end = EdtfDate.parse(String())
                    isRange = false
                } else {
                    val startString = dateString.substring(0, indexOfRangeDelimiter)
                    val endString = dateString.substring(indexOfRangeDelimiter + RANGE_DELIMITER.length)

                    start = EdtfDate.parse(startString)
                    end = EdtfDate.parse(endString)
                    isRange = true
                }
            } else {
                val startString = if (indexOfIntervalDelimiter == 0) String() else dateString.substring(0, indexOfIntervalDelimiter)
                val endString = dateString.substring(indexOfIntervalDelimiter + 1)

                start = EdtfDate.parse(startString, true)
                end = EdtfDate.parse(endString, true)
                isRange = false
            }
        } else {
            start = EdtfDate.parse(String())
            end = EdtfDate.parse(String())
            isRange = false
        }
    }

    override fun toString(): String {
        val startString = start.toString()
        val endString = end.toString()
        if (isRange) {
            return "$startString$RANGE_DELIMITER$endString"
        }
        if (end.status != EdtfDateStatus.UNKNOWN && endString.isBlank()) {
            return startString
        }
        return "$startString$INTERVAL_DELIMITER$endString"
    }
}