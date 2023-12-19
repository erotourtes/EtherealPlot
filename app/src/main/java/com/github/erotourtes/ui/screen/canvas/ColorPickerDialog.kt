package com.github.erotourtes.ui.screen.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.erotourtes.ui.theme.AppTheme
import com.github.erotourtes.ui.theme.spacing
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

private val PICKER_SIZE = 300.dp


@Composable
fun ColorPickerScreen(
    initialColor: Color,
    onColorChange: (Color) -> Unit,
    onBackPress: () -> Unit = {},
) {
    val prevColor by remember { mutableStateOf(initialColor) }
    val controller = rememberColorPickerController()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HsvColorPicker(
            modifier = Modifier
                .width(PICKER_SIZE)
                .height(PICKER_SIZE),
            controller = controller,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                onColorChange(colorEnvelope.color)
            },
            initialColor = initialColor
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AlphaTile(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(MaterialTheme.spacing.large)),
                controller = controller
            )

            Button(
                onClick = {
                    onColorChange(prevColor)
                    onBackPress()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.medium),
            ) {
                Text(text = "Cancel")
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun ColorPickerPreview() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.error) {
            ColorPickerScreen(
                initialColor = MaterialTheme.colorScheme.primary,
                onColorChange = { },
            )
        }
    }
}