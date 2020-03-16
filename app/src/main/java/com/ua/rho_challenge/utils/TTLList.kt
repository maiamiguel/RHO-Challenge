package com.ua.rho_challenge.utils

import java.util.*

class TTLList<K>() : MutableList<K> {
    private val tweetsList: MutableList<K>
    private val timestamps: MutableMap<K, Long>

    init {
        tweetsList = LinkedList()
        timestamps = HashMap()
    }

    override fun add(element: K): Boolean {
        add(0, element)
        return true
    }

    override fun add(index: Int, element: K) {
        removeExpiredTweets()
        if (!timestamps.containsKey(element)) {
            timestamps[element] = System.currentTimeMillis()
            tweetsList.add(index, element)
        }
    }

    private fun removeExpiredTweets() {
        val it = tweetsList.iterator()
        while (it.hasNext()) {
            val k = it.next()
            if (timestamps[k] != null) {
                val now = System.currentTimeMillis()
                val insertionTime = timestamps[k]!!
                if (now - insertionTime >= expiring_time) {
                    it.remove()
                    timestamps.remove(k)
                }
            }
        }
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override operator fun contains(element: K): Boolean {
        return tweetsList.contains(element)
    }

    override fun iterator(): MutableIterator<K> {
        return tweetsList.iterator()
    }

    override fun addAll(elements: Collection<K>): Boolean {
        return tweetsList.addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<K>): Boolean {
        return tweetsList.addAll(index, elements)
    }

    override fun clear() {
        tweetsList.clear()
    }

    override fun get(index: Int): K {
        return tweetsList[index]
    }

    override fun set(index: Int, element: K): K {
        return tweetsList.set(index, element)
    }

    override fun listIterator(): MutableListIterator<K> {
        return tweetsList.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<K> {
        return tweetsList.listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<K> {
        return tweetsList.subList(fromIndex, toIndex)
    }

    override val size: Int
        get() = tweetsList.size

    override fun indexOf(element: K): Int {
        return tweetsList.indexOf(element)
    }

    override fun lastIndexOf(element: K): Int {
        return tweetsList.lastIndexOf(element)
    }

    override fun remove(element: K): Boolean {
        return tweetsList.remove(element)
    }

    override fun removeAt(index: Int): K {
        return tweetsList.removeAt(index)
    }

    override fun containsAll(elements: Collection<K>): Boolean {
        return tweetsList.containsAll(elements)
    }

    override fun removeAll(elements: Collection<K>): Boolean {
        return tweetsList.removeAll(elements)
    }

    override fun retainAll(elements: Collection<K>): Boolean {
        return tweetsList.retainAll(elements)
    }
}