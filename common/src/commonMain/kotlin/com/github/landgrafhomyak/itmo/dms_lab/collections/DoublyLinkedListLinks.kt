package com.github.landgrafhomyak.itmo.dms_lab.collections

interface DoublyLinkedListLinks<N : Any> : SinglyLinkedListLinks<N> {
    var prev: N?
}