package io.arsh.roamr.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.arsh.roamr.ui.theme.RoamrTheme

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Translate : Screen("translate", "Translate", Icons.Filled.Translate)
    object Map : Screen("map", "Map", Icons.Filled.Map)
    object Currency : Screen("currency", "Currency", Icons.Filled.CurrencyExchange)
    object AiFeatures : Screen("ai", "AI", Icons.Filled.Lightbulb)
    object LostMode : Screen("lost", "Lost Mode", Icons.Filled.Sos)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoamrHomeScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roamr") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = Screen.Translate.route, modifier = Modifier.padding(paddingValues)) {
            composable(Screen.Translate.route) { TranslatorScreen() }
            composable(Screen.Map.route) { PlaceholderScreen("Map") }
            composable(Screen.Currency.route) { PlaceholderScreen("Currency Converter") }
            composable(Screen.AiFeatures.route) { PlaceholderScreen("AI Features") }
            composable(Screen.LostMode.route) { PlaceholderScreen("Lost Mode") }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Map,
        Screen.Currency,
        Screen.Translate,
        Screen.AiFeatures,
        Screen.LostMode
    )
    NavigationBar {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach {
            screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen() {
    var textToTranslate by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    val languages = listOf("English", "Spanish", "French", "German", "Japanese", "Italian", "Russian", "Chinese")
    var fromLanguage by remember { mutableStateOf(languages[0]) }
    var toLanguage by remember { mutableStateOf(languages[1]) }
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // From Language Dropdown
            ExposedDropdownMenuBox(
                expanded = fromExpanded,
                onExpandedChange = { fromExpanded = !fromExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = fromLanguage,
                    onValueChange = {},
                    label = { Text("From") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = fromExpanded,
                    onDismissRequest = { fromExpanded = false }
                ) {
                    languages.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                fromLanguage = selectionOption
                                fromExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // To Language Dropdown
            ExposedDropdownMenuBox(
                expanded = toExpanded,
                onExpandedChange = { toExpanded = !toExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = toLanguage,
                    onValueChange = {},
                    label = { Text("To") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = toExpanded,
                    onDismissRequest = { toExpanded = false }
                ) {
                    languages.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                toLanguage = selectionOption
                                toExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = textToTranslate,
            onValueChange = { textToTranslate = it },
            label = { Text("Enter text to translate") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        OutlinedTextField(
            value = translatedText,
            onValueChange = {},
            label = { Text("Translation") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            readOnly = true
        )
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Welcome to the $name screen!")
    }
}

@Preview(showBackground = true)
@Composable
fun RoamrHomeScreenPreview() {
    RoamrTheme {
        RoamrHomeScreen()
    }
}
