package com.github.erotourtes.ui.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class DragAnchors(val fraction: Float) {
    Start(0f), Half(.5f), // recalculating this value on size change to fit hidden content
    End(1f),
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwapToReveal(
    hiddenContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onRemove: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    var hiddenWidth by remember { mutableFloatStateOf(0f) }
    var fullWidth by remember { mutableFloatStateOf(0f) }

    val minVisibleFraction = 0.1f
    val density = LocalDensity.current

    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween(),
            confirmValueChange = { newValue ->
                if (newValue == DragAnchors.End) {
                    onRemove()
                    false
                } else true
            },
        ).apply {
            updateAnchors(DraggableAnchors {
                DragAnchors.entries.forEach { anchor -> anchor at 0f }
            })
        }
    }

    LaunchedEffect(fullWidth, hiddenWidth) {
        val dragEndPoint = fullWidth * (1 - minVisibleFraction)
        anchoredDraggableState.updateAnchors(DraggableAnchors {
            DragAnchors.entries.forEach { anchor ->
                if (anchor == DragAnchors.Half) anchor at hiddenWidth
                else anchor at dragEndPoint * anchor.fraction
            }
        })
    }

    Box(modifier = modifier.onGloballyPositioned {
        fullWidth = it.size.width.toFloat()
    }) {
        Box(modifier = Modifier
            .fillMaxHeight()
            .onGloballyPositioned {
                hiddenWidth = it.size.width
                    .toFloat()
                    .coerceIn(0f, fullWidth)
            }) {
            hiddenContent()
        }

        Box(modifier = Modifier
            .fillMaxHeight()
            .offset {
                IntOffset(
                    x = anchoredDraggableState
                        .requireOffset()
                        .roundToInt(), y = 0
                )
            }
            .anchoredDraggable(anchoredDraggableState, Orientation.Horizontal)) {
            content()
        }
    }
}

@Preview
@Composable
fun SwapPreview() {
    val height by remember {
        mutableStateOf(150.dp)
    }
    SwapToReveal(
        hiddenContent = {
            Box(
                modifier = Modifier
                    .background(color = Color.Green)
                    .width(100.dp)
                    .height(height),
            )
        },
        modifier = Modifier
            .background(color = Color.Blue)
            .fillMaxWidth()
            .height(height)
            .padding(10.dp),
    ) {
        Box(
            modifier = Modifier
                .background(color = Color.Red)
                .fillMaxWidth()
                .height(height),
        )
    }
}
