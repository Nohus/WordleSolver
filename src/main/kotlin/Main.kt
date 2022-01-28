import WordleSolver.SolverResult
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.awtEvent
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import composables.Game
import composables.MessageBox
import composables.SlotState.TYPED
import composables.SolutionsList
import composables.SuggestionsList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
@Preview
fun App(
    state: GameState,
    onLetterClicked: (word: Int, letter: Int) -> Unit,
    onSolutionClicked: (letter: String) -> Unit,
    onRestartClicked: () -> Unit
) {
    MaterialTheme {
        Row {
            Game(state, onLetterClicked, onRestartClicked)
            val solver by remember { mutableStateOf(WordleSolver()) }
            var result: SolverResult by remember { mutableStateOf(solver.solve(state)) }
            LaunchedEffect(state) {
                withContext(Dispatchers.Default) {
                    result = solver.solve(state)
                }
            }
            SuggestionsList(state, result.suggestions, onSolutionClicked)
            SolutionsList(state, result.solutions, onSolutionClicked)
        }
    }
}

@Composable
fun StatusBox(state: GameState) {
    val message = if (state.words.dropLast(1).any { it.any { it.state == TYPED } }) {
        "Click letters to input the outcome"
    } else {
        "Type your attempt"
    }
    MessageBox(message)
}

fun main() = application {
    var state by remember { mutableStateOf(GameState()) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Wordle Solver by Nohus",
        onPreviewKeyEvent = { event ->
            if (event.type == KeyEventType.KeyUp) {
                state = handleKeyPress(event, state)
            }
            false
        },
        resizable = false,
        state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified)
    ) {
        App(
            state = state,
            onLetterClicked = { word, letter ->
                state = state.rotateLetterState(word, letter)
            },
            onSolutionClicked = { word ->
                word.chunked(1).forEach { letter ->
                    state = state.addLetter(letter)
                }
            },
            onRestartClicked = {
                state = GameState()
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun handleKeyPress(event: KeyEvent, state: GameState): GameState {
    return if (event.key == Key.Backspace) state.removeLetter()
    else if (event.awtEvent.keyChar.isLetter()) state.addLetter(event.awtEvent.keyChar.toString())
    else state
}
