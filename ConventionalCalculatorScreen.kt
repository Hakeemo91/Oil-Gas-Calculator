@file:OptIn(ExperimentalMaterial3Api::class)

package com.mahmoud.swtcalculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmoud.swtcalculator.ui.shared.*

@Composable
fun ConventionalCalculatorScreen(onBack: () -> Unit) {

    val focusManager = LocalFocusManager.current

    // Display and calculation state
    var display by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var isNewNumber by remember { mutableStateOf(true) }
    var lastOperation by remember { mutableStateOf<String?>(null) }

    fun appendNumber(num: String) {
        display = if (isNewNumber) {
            isNewNumber = false
            num
        } else {
            if (display == "0") num else display + num
        }
    }

    fun appendDecimal() {
        if (!display.contains(".")) {
            display += "."
            isNewNumber = false
        }
    }

    fun performOperation(op: String) {
        val currentValue = display.toDoubleOrNull() ?: return

        if (firstOperand == null) {
            firstOperand = currentValue
        } else if (operation != null) {
            val result = when (operation) {
                "+" -> firstOperand!! + currentValue
                "-" -> firstOperand!! - currentValue
                "×" -> firstOperand!! * currentValue
                "÷" -> if (currentValue != 0.0) firstOperand!! / currentValue else return
                else -> currentValue
            }
            firstOperand = result
            display = formatResult(result)
        }

        operation = op
        isNewNumber = true
        lastOperation = op
    }

    fun calculate() {
        val currentValue = display.toDoubleOrNull() ?: return

        if (firstOperand == null || operation == null) {
            return
        }

        val result = when (operation) {
            "+" -> firstOperand!! + currentValue
            "-" -> firstOperand!! - currentValue
            "×" -> firstOperand!! * currentValue
            "÷" -> if (currentValue != 0.0) firstOperand!! / currentValue else return
            else -> currentValue
        }

        display = formatResult(result)
        firstOperand = null
        operation = null
        isNewNumber = true
        lastOperation = null
    }

    fun clear() {
        display = "0"
        firstOperand = null
        operation = null
        isNewNumber = true
        lastOperation = null
        focusManager.clearFocus()
    }

    fun toggleSign() {
        val value = display.toDoubleOrNull() ?: return
        display = formatResult(-value)
    }

    fun percentage() {
        val value = display.toDoubleOrNull() ?: return
        display = formatResult(value / 100.0)
    }

    fun backspace() {
        if (display.length > 1) {
            display = display.dropLast(1)
        } else {
            display = "0"
            isNewNumber = true
        }
    }

    fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.10f", value).trimEnd('0').trimEnd('.')
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Calculator", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { pv ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            IntroHintGray(
                subtitle = "Standard calculator for basic arithmetic operations.",
                leadingIcon = Icons.Default.Lightbulb
            )

            // Display Card
            CardBox {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (lastOperation != null) {
                        Text(
                            "${firstOperand?.let { formatResult(it) } ?: ""} $lastOperation",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        display,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Calculator Grid
            CardBox {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Row 1: Clear, +/-, %, ÷
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton(
                            text = "C",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            textColor = MaterialTheme.colorScheme.onErrorContainer,
                            onClick = { clear() }
                        )
                        CalcButton(
                            text = "+/-",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = { toggleSign() }
                        )
                        CalcButton(
                            text = "%",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = { percentage() }
                        )
                        CalcButton(
                            text = "÷",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = Color(red=0.97f, green=0.55f, blue=0.15f),
                            textColor = androidx.compose.ui.graphics.Color.White,
                            onClick = { performOperation("÷") }
                        )
                    }

                    // Row 2: 7, 8, 9, ×
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton("7", Modifier.weight(1f).fillMaxHeight()) { appendNumber("7") }
                        CalcButton("8", Modifier.weight(1f).fillMaxHeight()) { appendNumber("8") }
                        CalcButton("9", Modifier.weight(1f).fillMaxHeight()) { appendNumber("9") }
                        CalcButton(
                            text = "×",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = Color(red=0.97f, green=0.55f, blue=0.15f),
                            textColor = androidx.compose.ui.graphics.Color.White,
                            onClick = { performOperation("×") }
                        )
                    }

                    // Row 3: 4, 5, 6, −
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton("4", Modifier.weight(1f).fillMaxHeight()) { appendNumber("4") }
                        CalcButton("5", Modifier.weight(1f).fillMaxHeight()) { appendNumber("5") }
                        CalcButton("6", Modifier.weight(1f).fillMaxHeight()) { appendNumber("6") }
                        CalcButton(
                            text = "−",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = Color(red=0.97f, green=0.55f, blue=0.15f),
                            textColor = androidx.compose.ui.graphics.Color.White,
                            onClick = { performOperation("-") }
                        )
                    }

                    // Row 4: 1, 2, 3, +
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton("1", Modifier.weight(1f).fillMaxHeight()) { appendNumber("1") }
                        CalcButton("2", Modifier.weight(1f).fillMaxHeight()) { appendNumber("2") }
                        CalcButton("3", Modifier.weight(1f).fillMaxHeight()) { appendNumber("3") }
                        CalcButton(
                            text = "+",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = Color(red=0.97f, green=0.55f, blue=0.15f),
                            textColor = androidx.compose.ui.graphics.Color.White,
                            onClick = { performOperation("+") }
                        )
                    }

                    // Row 5: 0, ., ⌫, =
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalcButton(
                            "0",
                            Modifier
                                .weight(2f)
                                .fillMaxHeight()
                        ) { appendNumber("0") }
                        CalcButton(".", Modifier.weight(1f).fillMaxHeight()) { appendDecimal() }
                        CalcButton(
                            text = "⌫",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = { backspace() }
                        )
                        CalcButton(
                            text = "=",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            backgroundColor = Color(red=0.10f, green=0.72f, blue=0.85f),
                            textColor = androidx.compose.ui.graphics.Color.White,
                            onClick = { calculate() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
