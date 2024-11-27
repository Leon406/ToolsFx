package me.leon.misc

import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.ln
import kotlin.math.max
import me.leon.config.WORDNINJA_DICT_FILE
import me.leon.ext.readResourceText

/**
 * ported from
 * https://stackoverflow.com/questions/8870261/how-to-split-text-without-spaces-into-list-of-words/11642687
 */
object Spliter {
    private val splitRegex = "[^a-zA-Z0-9']+".toRegex()
    private val wordCost = mutableMapOf<String, Number>()
    private var maxWordLength: Int = 0

    init {
        initDict()
    }

    fun initDict(file: File = WORDNINJA_DICT_FILE) {
        val dictionaryWords =
            if (file.exists()) {
                file.readLines()
            } else {
                readResourceText("/${WORDNINJA_DICT_FILE.name}").lines()
            }
        // Build a cost dictionary, assuming Zipf's law and cost = -math.log(probability).
        wordCost.clear()
        val lgDictSize = ln(dictionaryWords.size.toDouble())
        var wordIdx = 0
        for (word in dictionaryWords) {
            wordCost[word] = ln(++wordIdx * lgDictSize)
        }
        maxWordLength = dictionaryWords.maxBy { it.length }.length
    }

    fun splitContiguousWords(sentence: String): List<String> {
        val splitWords =
            sentence.parts().fold(mutableListOf<String>()) { acc, s ->
                acc.apply {
                    if (splitRegex.matches(s)) {
                        add(s)
                    } else {
                        add(split(s))
                    }
                }
            }
        return splitWords
    }

    private fun split(partSentence: String): String {
        // Build the cost array.
        val cost = mutableListOf<Pair<Number, Number>>()
        cost.add(0 to 0)
        for (index in 1 until partSentence.length + 1) {
            cost.add(bestMatch(partSentence, cost, index))
        }

        //  Backtrack to recover the minimal-cost string.
        var idx = partSentence.length
        val output = mutableListOf<String>()
        while (idx > 0) {
            val candidate = bestMatch(partSentence, cost, idx)
            val candidateCost = candidate.first
            val candidateIndexValue = candidate.second
            if (candidateCost.toDouble() != cost[idx].first.toDouble()) {
                error("Candidate cost unmatched; This should not be the case!")
            }
            // Apostrophe and digit handling
            var newToken = true
            val token = partSentence.substring(idx - candidateIndexValue.toInt(), idx)
            if (token !== "'" && output.size > 0) {
                val lastWord = output[output.size - 1]
                if (
                    lastWord.equals("'s", ignoreCase = true) ||
                        partSentence[idx - 1].isDigit() && lastWord[0].isDigit()
                ) {
                    output[output.size - 1] = token + lastWord
                    newToken = false
                }
            }
            if (newToken) {
                output.add(token)
            }
            idx -= candidateIndexValue.toInt()
        }

        return output.reversed().joinToString(" ")
    }

    /**
     * Find the best match for the i first characters, assuming cost has been built for the i-1
     * first characters. Returns a pair (match_cost, match_length).
     */
    private fun bestMatch(
        partSentence: String,
        cost: List<Pair<Number, Number>>,
        index: Int,
    ): Pair<Number, Number> {

        val candidates =
            cost.subList(max(0.0, (index - maxWordLength).toDouble()).toInt(), index).reversed()
        var enumerateIdx = 0
        var minPair: Pair<Number, Number> = Int.MAX_VALUE to enumerateIdx
        for (pair in candidates) {
            ++enumerateIdx
            val subsequence = partSentence.substring(index - enumerateIdx, index).lowercase()
            var minCost: Number = Int.MAX_VALUE
            if (wordCost.containsKey(subsequence)) {
                minCost = pair.first.toDouble() + wordCost[subsequence]!!.toDouble()
            }
            if (minCost.toDouble() < minPair.first.toDouble()) {
                minPair = minCost.toDouble() to enumerateIdx
            }
        }
        return minPair
    }

    fun String.parts() =
        fold(Triple(mutableListOf<String>(), StringBuilder(), AtomicBoolean(false))) { acc, char ->
                if (char.isLetter()) {
                    if (acc.third.get()) {
                        acc.first.add(acc.second.toString())
                        acc.second.clear()
                    }
                    acc.third.set(false)
                    acc.second.append(char)
                } else {
                    if (acc.second.isNotEmpty()) {
                        acc.first.add(acc.second.toString())
                        acc.second.clear()
                    }
                    acc.second.append(char)
                    acc.third.set(true)
                }
                acc
            }
            .also {
                if (it.second.isNotEmpty()) {
                    it.first.add(it.second.toString())
                }
            }
            .first
}
