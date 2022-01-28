package composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import composables.SlotState.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

enum class SlotState {
    BLACK, YELLOW, GREEN, TYPED, EMPTY
}

data class LetterSlotContent(
    val letter: String? = null,
    val state: SlotState
)

@Preview
@Composable
private fun LetterSlotPreview() {
    LetterSlot(LetterSlotContent("G", GREEN))
}

@OptIn(ExperimentalUnitApi::class, ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun LetterSlot(
    content: LetterSlotContent,
    isClickable: Boolean = true,
    onClicked: () -> Unit = {}
) {
    val background by animateColorAsState(getBackgroundColor(content.state))
    val border by animateColorAsState(getBorderColor(content.state))
    var modifier = Modifier
        .size(62.dp)
        .background(background)
    if (content.state != EMPTY && isClickable) {
        modifier = modifier
            .pointerHoverIcon(PointerIconDefaults.Hand)
            .clickable { onClicked() }
    }
    modifier = modifier.border(2.dp, border)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = content
        ) { content ->
            val color = getLetterColor(content.state)
            Text(
                text = content.letter?.uppercase() ?: "",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = TextUnit(32f, TextUnitType.Sp)
            )
        }
    }
}

private fun getBackgroundColor(state: SlotState) = when (state) {
    BLACK -> Color(0xff787c7e)
    YELLOW -> Color(0xffc9b458)
    GREEN -> Color(0xff6aaa64)
    else -> Color.White
}

private fun getBorderColor(state: SlotState) = when (state) {
    EMPTY -> Color(0xffd3d6da)
    TYPED -> Color(0xff878a8c)
    else -> getBackgroundColor(state)
}

private fun getLetterColor(state: SlotState) = when (state) {
    TYPED -> Color.Black
    else -> Color.White
}
