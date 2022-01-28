import composables.LetterSlotContent
import composables.SlotState
import composables.SlotState.*

val emptyWord = List(5) { LetterSlotContent(null, EMPTY) }

data class GameState(
    val words: List<List<LetterSlotContent>> = listOf(emptyWord),
) {
    val blackLetters: List<String>
        get() = getPositionedLetters(BLACK)
            .asSequence()
            .map { it.letter }.toSet().toList()
            .filter { it !in yellowLetters.map { it.letter } }
            .filter { it !in greenLetters.map { it.letter } }
            .toList()
    val yellowLetters: List<PositionedLetter>
        get() = getPositionedLetters(YELLOW)
    val greenLetters: List<PositionedLetter>
        get() = getPositionedLetters(GREEN)

    fun addLetter(letter: String): GameState {
        var newWords = words
        // Add letter to first empty slot in the last word
        val wordWithLetter = newWords.last().let { word ->
            word.toMutableList().also {
                val index = word.indexOfFirst { it.state == EMPTY }
                it[index] = LetterSlotContent(letter, TYPED)
            }
        }
        newWords = newWords.dropLast(1) + listOf(wordWithLetter)
        // Add empty row if no empty slots left
        if (newWords.last().none { it.state == EMPTY }) {
            newWords = newWords + listOf(emptyWord)
        }
        return copy(words = newWords)
    }

    fun removeLetter(): GameState {
        // Do nothing if nothing to remove
        if (words.first().all { it.state == EMPTY }) {
            return this
        }
        var newWords = words
        // Remove last row if empty
        if (newWords.last().all { it.state == EMPTY }) {
            newWords = newWords.dropLast(1)
        }
        // Remove last letter
        val wordWithoutLetter = newWords.last().let { word ->
            word.toMutableList().also {
                val index = word.indexOfLast { it.state != EMPTY }
                it[index] = LetterSlotContent(null, EMPTY)
            }
        }
        newWords = newWords.dropLast(1) + listOf(wordWithoutLetter)
        return copy(words = newWords)
    }

    fun rotateLetterState(targetWordIndex: Int, targetLetterIndex: Int): GameState {
        val newWords = words.mapIndexed { wordIndex, word ->
            if (wordIndex == targetWordIndex) {
                word.mapIndexed { letterIndex, letter ->
                    if (letterIndex == targetLetterIndex) {
                        letter.copy(state = getNextState(letter.state))
                    } else letter
                }
            } else word
        }
        return copy(words = newWords)
    }

    private fun getNextState(state: SlotState) = when (state) {
        BLACK -> YELLOW
        YELLOW -> GREEN
        GREEN -> TYPED
        TYPED -> BLACK
        else -> TYPED
    }

    private fun getPositionedLetters(state: SlotState): List<PositionedLetter> {
        return words.flatMap { word ->
            word
                .withIndex()
                .filter { it.value.state == state }
                .mapNotNull { (index, letterSlotContent) ->
                    letterSlotContent.letter?.let { PositionedLetter(it, index) }
                }
        }
    }
}
