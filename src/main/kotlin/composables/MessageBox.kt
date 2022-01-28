package composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun MessageBoxPreview() {
    MessageBox("Message")
}

@OptIn(ExperimentalUnitApi::class, ExperimentalAnimationApi::class)
@Composable
fun MessageBox(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xffd3d6da))
            .padding(10.dp)
    ) {
        AnimatedContent(targetState = message) { message ->
            Text(
                text = message,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = TextUnit(16f, TextUnitType.Sp),
            )
        }
    }
}
