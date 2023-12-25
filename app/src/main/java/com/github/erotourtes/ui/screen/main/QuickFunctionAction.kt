package com.github.erotourtes.ui.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.erotourtes.ui.screen.main.data.QuickFunction
import com.github.erotourtes.ui.screen.main.data.quickFunctionList
import com.github.erotourtes.ui.theme.spacing

@Composable
fun QuickFunctionAction(
    functionList: List<QuickFunction> = quickFunctionList,
    onQuickFunctionClick: (QuickFunction) -> Unit,
) {
    Column {
        Text(
            text = "Quick Function Action",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(MaterialTheme.spacing.large)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            contentPadding = PaddingValues(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(functionList.size) { index ->
                val function = functionList[index]
                QuickFunctionItem(function = function, modifier = Modifier.clickable {
                    onQuickFunctionClick(function)
                })
            }
        }
    }
}

@Composable
fun QuickFunctionItem(
    function: QuickFunction,
    modifier: Modifier = Modifier,
) {
    Card(modifier) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.large)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = function.icon, contentDescription = function.name, modifier = Modifier.size(48.dp)
                )
                Text(
                    text = function.name,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            Text(
                text = function.formula,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}