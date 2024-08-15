# EDTF4K ![Build Status](https://github.com/ppuffinburger/edtf4k/workflows/Build/badge.svg)

## What is it!

EDTF4K is a Kotlin library for parsing [EDTF](https://www.loc.gov/standards/datetime/ "EDTF") (Extended Date/Time Format) strings.

## Basic Usage

`org.edtf4k.EdtfDateFactory` is the entry-point to parsing an EDTF string.  The return will either be an `org.edtf4k.EdtfDatePair` or an `org.edtf4k.EdtDateSet` depending on the string.

`org.edtf4k.EdtfDatePair` holds a pair of `org.edtf4k.EdtfDate`, because dates can be intervals or ranges.  Each `org.edtf4k.EdtfDate` has a status to determine if it is NORMAL, OPEN, UNUSED, UNKNOWN, or INVALID. 


```Kotlin
    val parsedDate = EdtfDateFactory.parse("2001-02-03")
    val parsedDateTime = EdtfDateFactory.parse("2001-02-03T09:30:01")
    val parsedDateTimeInUtc = EdtfDateFactory.parse("2004-01-01T10:10:10Z")
    val parsedDateTimeWithTimezone = EdtfDateFactory.parse("2004-01-01T10:10:10+05:00")
```

```Kotlin
    val parsedDateInterval = EdtfDateFactory.parse("1964/2008")
    val parsedDateRange = EdtfDateFactory.parse("1964..2008")
    val parsedMixedInterval = EdtfDateFactory.parse("2004-02-01/2005")
    val parsedMixedRange = EdtfDateFactory.parse("2004-02-01..2005")
```

EdtfDateSets contain a list of EdtfDatePairs and a flag stating what kind of set. The kinds are either ONE_OF_A_SET or ALL_MEMBERS.

```Kotlin
    val parsedOneOfASet = EdtfDateFactory.parse("[1667,1668,1670..1672]")
    val parsedAllMembers = EdtfDateFactory.parse("{1960,1961-12}")
```

## About EDTF

EDTF stands for Extended Date/Time Format. The EDTF specification was created by the [Library of Congress](https://www.loc.gov "Library of Congress") and others to define features to be supported in a date/time string, features considered useful for a wide variety of applications.