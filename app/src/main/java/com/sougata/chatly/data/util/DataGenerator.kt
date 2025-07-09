package com.sougata.chatly.data.util

object DataGenerator {

    fun generateUserSearchKeywords(name: String): List<String> {

        name.ifEmpty { return emptyList() }

        val keywords = mutableListOf<String>()

        val filteredName = name.replace(Regex("\\s+"), " ").trim()
        val names = filteredName.split(" ") + filteredName

        for (n in names) {
            for (i in 0 until n.length) {
                if (filteredName[i] != ' ') {
                    keywords.add(n.substring(0, i + 1).lowercase())
                }
            }
        }

        return keywords.distinct()
    }
}