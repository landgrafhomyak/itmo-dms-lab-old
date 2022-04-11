package com.github.landgrafhomyak.itmo.dms_lab.objects


class LabWorksCollection {
//    private val byId = AbstractRedBlackTree(Node::rbTreeById, LabWork::id)
//    private val byMaximumPoint = AbstractRedBlackTree(Node::rbTreeByMaximumPoint, LabWork::maximumPoint)


    /**
     * **Не является обязательным для выполнения лабораторных работ!**
     *
     * Обёртка вокруг элемента коллекции позволяющая ему быть частью нескольких связных структур
     * @property value значение узла
     */
    private class Node(
        val value: LabWork
    ) {
        /**
         * Связывает с [красно-чёрным деревом](https://ru.wikipedia.org/wiki/%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%BE-%D1%87%D1%91%D1%80%D0%BD%D0%BE%D0%B5_%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE)
         * (словарём) предоставляющему доступ к элементу по значению его [идентификатора][LabWork.id]
         * @see LabWorksCollection.byId
         */
//        val rbTreeById = BinaryTreeLinks()

        /**
         * Связывает с [красно-чёрным деревом](https://ru.wikipedia.org/wiki/%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%BE-%D1%87%D1%91%D1%80%D0%BD%D0%BE%D0%B5_%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE)
         * позволяющим добавлять элемент в коллекцию отсортированную по значению [максимальной точки][LabWork.maximumPoint]
         * @see LabWorksCollection.byMaximumPoint
         */
//        val rbTreeByMaximumPoint = BinaryTreeLinks()

        /**
         * Связывает с [очередью с приоритетом](https://ru.wikipedia.org/wiki/%D0%9E%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C_%D1%81_%D0%BF%D1%80%D0%B8%D0%BE%D1%80%D0%B8%D1%82%D0%B5%D1%82%D0%BE%D0%BC_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
         * (кучей) предоставляющей доступ к элементу с максимальным значением todo
         */
//        val heap = BinaryTreeLinks()


    }


}