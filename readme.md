# EDTF4K ![Build Status](https://github.com/ppuffinburger/edtf4k/workflows/Build/badge.svg)

## What is it!

EDTF4K is a Kotlin library for parsing [EDTF](https://www.loc.gov/standards/datetime/ "EDTF") (Extended Date/Time Format) strings.

`org.edtf4k.EdtfDateFactory` is the entry-point to parsing an EDTF string.  The return will be one of an `org.edtf4k.EdtfDateType` type depending on the string.

There are three EdtfDateType's

- `org.edtf4k.EdtfDate` holds a single date
- `org.edtf4k.EdtfDatePair` holds a pair of `org.edtf4k.EdtfDate`s for ranges and intervals
- `org.edtf4k.EdtfDateSet` holds a list of EdtfDateTypes

Each `org.edtf4k.EdtfDate` has a status to determine if it is NORMAL, OPEN, UNUSED, UNKNOWN, or INVALID.

This implementation supports all EDTF Levels (0, 1, and 2) 

## Basic Usage

```kotlin
for (dateString in arrayOf("2006", "2008..2009", "{2006,2008..2009}")) {
    when (val dateType = EdtfDateFactory.parse(dateString)) {
        is EdtfDate -> println("Single - $dateType")
        is EdtfDatePair -> println("Pair   - $dateType")
        is EdtfDateSet -> println("Set    - ${dateType.joinToString()}")
    }
}
```
will output
```text
Single - 2006
Pair   - 2008..2009
Set    - 2006, 2008..2009
```

## About EDTF

The Extended Date/Time Format (EDTF) was created by the [Library of Congress](https://www.loc.gov "Library of Congress") with the participation and support of the bibliographic community as well as communities with related interests. It defines features to be supported in a date/time string, features considered useful for a wide variety of applications.