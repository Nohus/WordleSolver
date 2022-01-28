package composables

import GameState
import StatusBox
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Game(
    state: GameState,
    onLetterClicked: (word: Int, letter: Int) -> Unit,
    onRestartClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(10.dp)
            .width(IntrinsicSize.Max)
    ) {
        MessageBox("Wordle")
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .animateContentSize()
        ) {
            state.words.forEachIndexed { wordIndex, word ->
                WordSlots(
                    slots = word,
                    onLetterClicked = { onLetterClicked(wordIndex, it) }
                )
            }
        }
        StatusBox(state)
        MessageBox(
            message = "Click to restart",
            modifier = Modifier
                .pointerHoverIcon(PointerIconDefaults.Hand)
                .clickable { onRestartClicked() }
        )
    }
}
