package com.pacmac.devinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    searchTerm: String,
    onNewSearchTerm: (String) -> Unit = {},
    onHideSearch: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val focusRequester = FocusRequester()
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    DisposableEffect(key1 = Unit) {
        onDispose(focusManager::clearFocus)
    }

    TextField(
        value = searchTerm,
        onValueChange = onNewSearchTerm,
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .focusRequester(focusRequester),
        leadingIcon = {
            IconButton(onClick = {
                onHideSearch()
                onNewSearchTerm("")
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Exit Search",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                if (searchTerm.isNotBlank()) {
                    onNewSearchTerm("")
                } else {
                    onHideSearch()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close Search",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_hint),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            autoCorrect = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        )
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewSearchBar() {
    DeviceInfoTheme() {
        SearchBar("CELL")
    }
}
