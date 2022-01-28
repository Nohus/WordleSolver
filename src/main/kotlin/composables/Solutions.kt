package composables

import GameState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import composables.SlotState.*

@Preview
@Composable
private fun SolutionsPreview() {
    SolutionsList(GameState(), listOf("nohus", "words", "other"))
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun SolutionsList(
    state: GameState,
    solutions: List<String>,
    onSolutionClicked: (String) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(10.dp)
            .width(IntrinsicSize.Max)
    ) {
        val message = when (solutions.size) {
            0 -> "Uh oh!"
            1 -> "Solution"
            in 1..8 -> "Possible solutions"
            else -> "Top solutions"
        }
        MessageBox(message)
        WordList(
            state = state,
            words = solutions,
            onWordClicked = onSolutionClicked
        )
        val solutionCount = when (solutions.size) {
            0 -> "No solutions"
            1 -> "Finished"
            else -> "${solutions.size} solutions left"
        }
        MessageBox(solutionCount)
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun SuggestionsList(
    state: GameState,
    suggestions: List<String>,
    onSolutionClicked: (String) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(10.dp)
            .width(IntrinsicSize.Max)
    ) {
        val message = when (suggestions.size) {
            0 -> "Uh oh!"
            1 -> "Solution"
            else -> "Best words"
        }
        MessageBox(message)
        WordList(
            state = state,
            words = suggestions,
            onWordClicked = onSolutionClicked
        )
        val solutionCount = when (suggestions.size) {
            0 -> "No suggestions"
            1 -> "Finished"
            else -> "${suggestions.size} words left"
        }
        MessageBox(solutionCount)
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun WordList(
    state: GameState,
    words: List<String>,
    onWordClicked: (String) -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .animateContentSize()
    ) {
        words.take(8).forEach { solution ->
            val slots = solution.chunked(1).mapIndexed { index, letter ->
                val slotState = if (state.greenLetters.any { it.position == index && it.letter == letter }) GREEN
                else if (state.yellowLetters.any { it.letter == letter }) YELLOW
                else TYPED
                LetterSlotContent(letter, slotState)
            }
            Box(
                modifier = Modifier
                    .pointerHoverIcon(PointerIconDefaults.Hand)
                    .clickable { onWordClicked(solution) }
            ) {
                WordSlots(
                    slots = slots,
                    clickableSlots = false
                )
            }
        }
        if (words.isEmpty()) {
            WordSlots(
                slots = List(5) { LetterSlotContent("!", TYPED) },
                clickableSlots = false
            )
        }
    }
}
