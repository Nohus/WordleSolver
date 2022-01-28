import androidx.compose.ui.res.useResource
import java.io.File
import kotlin.math.max

class WordleSolver {

    private val allWords = readDictionary()

    data class SolverResult(
        val solutions: List<String>,
        val suggestions: List<String>
    )

    fun solve(state: GameState): SolverResult {
        val solutions = getBestSolutions(state)
        val suggestions = getBestSuggestions(state, solutions)
        return SolverResult(solutions, suggestions)
    }

    private fun getBestSolutions(state: GameState): List<String> {
        val possibleWords = getPossibleWords(state)
        val frequency = getLetterFrequency(possibleWords)
        return scoreWords(possibleWords, frequency).map { it.first }
    }

    private fun getBestSuggestions(state: GameState, solutions: List<String>): List<String> {
        val uniqueLetterWords = solutions.map { it.chunked(1).toSet() }
        val letters = uniqueLetterWords.flatten().toSet()
        val lettersToMaxSolutionCount = letters.associateWith { letter ->
            val solutionsWithLetter = solutions.count { it.contains(letter) }
            val solutionsWithoutLetter = (solutions.size - solutionsWithLetter)
            max(solutionsWithLetter, solutionsWithoutLetter)
        }
        return allWords
            .filter { word -> word.all { it.toString() in letters } }
            .map { word ->
                word to word.toSet().sumOf { lettersToMaxSolutionCount[it.toString()] ?: 0 }
            }
            .sortedWith(compareBy(
                { -it.first.toSet().size }, // Number of unique letters
                { it.second }, // Solution-space minimization score
                { it.first
                    .chunked(1)
                    .withIndex()
                    .count { letter ->
                        state.yellowLetters
                            .any { it.letter == letter.value && it.position == letter.index}
                    }
                }, // Yellow letters in wrong places
                { it.first !in solutions }, // Is a possible solution
            ))
            .map { it.first }
    }

    private fun getPossibleWords(state: GameState): List<String> {
        // Cannot contain black letters
        var possible = allWords.filterNot { it.chunked(1).any { it in state.blackLetters } }
        state.greenLetters.forEach { green ->
            // Has to contain green letters at correct positions
            possible = possible.filter { it.chunked(1)[green.position] == green.letter }
        }
        state.yellowLetters.forEach { yellow ->
            // Has to contain yellow letters
            possible = possible.filter { it.chunked(1).any { it == yellow.letter } }
            // Cannot contain yellow letters at their positions
            possible = possible.filterNot { it.chunked(1)[yellow.position] == yellow.letter }
        }
        return possible
    }

    private fun scoreWords(words: List<String>, letterFrequency: List<Pair<String, Int>>): List<Pair<String, Int>> {
        return words
            .map { it to scoreWord(it, letterFrequency) }
            .sortedByDescending { it.second }
    }

    private fun scoreWord(word: String, letterFrequency: List<Pair<String, Int>>): Int {
        val uniqueLetters = word.chunked(1).toSet()
        return uniqueLetters.sumOf { letter -> letterFrequency.first { it.first == letter }.second }
    }

    private fun getLetterFrequency(words: List<String>): List<Pair<String, Int>> {
        return words
            .flatMap { it.chunked(1) }
            .groupBy { it }
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }
    }

    private fun readDictionary(): List<String> {
        return javaClass.getResource("words").readText().lines()
            .asSequence()
            .filter { it.length == 5 }
            .filter { word -> word.all { it.isLetter() } }
            .map { it.lowercase() }
            .toSet()
            .toList()
    }
}
