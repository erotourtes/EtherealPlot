package com.github.erotourtes.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.github.erotourtes.ui.theme.spacing

const val EXPANSTION_TRANSITION_DURATION = 450

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    expandableContent: @Composable () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    paddingExpanded: Dp = MaterialTheme.spacing.large,
    paddingCollapsed: Dp = MaterialTheme.spacing.default,
    content: @Composable () -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(transitionState, label = "transition")
    val cardPaddingHorizontal by transition.animateDp({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "paddingTransition") { if (expanded) paddingExpanded else paddingCollapsed }
    val cardRoundedCorners by transition.animateDp({
        tween(
            durationMillis = EXPANSTION_TRANSITION_DURATION, easing = FastOutSlowInEasing
        )
    }, label = "cornersTransition") {
        if (expanded) MaterialTheme.spacing.small else MaterialTheme.spacing.medium
    }

    Card(
        shape = RoundedCornerShape(cardRoundedCorners), modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = cardPaddingHorizontal,
            )
    ) {
        Column {
            content()
            ExpandableContent(visible = expanded, content = expandableContent)
        }
    }
}

@Composable
fun ExpandableContent(
    visible: Boolean = true, content: @Composable () -> Unit
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top, animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f, animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top, animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }

    AnimatedVisibility(
        visible = visible, enter = enterTransition, exit = exitTransition
    ) {
        content()
    }
}

@Preview
@Composable
private fun PreviewExpandableCard() {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ExpandableCard(
            expandableContent = {
                Text(
                    text = "Expandable content",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            },
            expanded = expanded,
        ) {
            Text(
                text = "Card content", modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), textAlign = TextAlign.Center
            )
        }
    }
}