package com.example.knot_thing

/**
 * This interface acts as an abstraction on how to compare two KNoTValues. It departs from the idea
 * that each KNoTValue will have a lower and an upper bound and that the comparision of these limits
 * is intrinsic to it's type.
 * e.g.: the accelerometer should be the one responsible to compare itself.
 * P.S.: The creation of this class, instead of using Comparator interface, was necessary in order
 * to be compatible with the KNoT protocol.
 */
interface KNoTComparator {
    fun lesser() : Boolean
    fun greater() : Boolean
}
