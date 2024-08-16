package org.edtf4k

class EdtfDateSet(dateString: String) : List<EdtfDateType>, EdtfDateType {
    private val internal: List<EdtfDateType>
    val representation: Representation

    init {
        if (dateString.isNotBlank()) {
            val startCharacter = dateString[0]

            if (startCharacter == '{' || startCharacter == '[') {
                representation = if (startCharacter == '{') {
                    Representation.ALL_MEMBERS
                } else {
                    Representation.ONE_OF_A_SET
                }

                internal = dateString.substring(1, dateString.length - 1).split(",").map { EdtfDateFactory.parse(it) }
            } else {
                internal = ArrayList()
                representation = Representation.INVALID
            }
        } else {
            internal = ArrayList()
            representation = Representation.INVALID
        }
    }

    override val size: Int
        get() = internal.size

    override fun get(index: Int): EdtfDateType {
        return internal[index]
    }

    override fun isEmpty(): Boolean {
        return internal.isEmpty()
    }

    override fun iterator(): Iterator<EdtfDateType> {
        return internal.iterator()
    }

    override fun listIterator(): ListIterator<EdtfDateType> {
        return internal.listIterator()
    }

    override fun listIterator(index: Int): ListIterator<EdtfDateType> {
        return internal.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<EdtfDateType> {
        return internal.subList(fromIndex, toIndex)
    }

    override fun lastIndexOf(element: EdtfDateType): Int {
        return internal.lastIndexOf(element)
    }

    override fun indexOf(element: EdtfDateType): Int {
        return internal.indexOf(element)
    }

    override fun containsAll(elements: Collection<EdtfDateType>): Boolean {
        return internal.containsAll(elements)
    }

    override fun contains(element: EdtfDateType): Boolean {
        return internal.contains(element)
    }

    override fun toString(): String {
        if (internal.isEmpty()) {
            return String()
        }

        val prefix = if (representation == Representation.ALL_MEMBERS) "{" else "["
        val postfix = if (representation == Representation.ALL_MEMBERS) "}" else "]"

        return internal.joinToString(",", prefix, postfix) { it.toString() }
    }

    enum class Representation {
        ONE_OF_A_SET,
        ALL_MEMBERS,
        INVALID
    }
}