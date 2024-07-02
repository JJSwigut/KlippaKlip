package ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onImeSearch: () -> Unit = {},
    autoFocus: Boolean = true,
) {

    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth().focusRequester(focusRequester),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colors.onPrimary,
            backgroundColor = MaterialTheme.colors.primary,
            focusedBorderColor = MaterialTheme.colors.secondaryVariant,
            unfocusedBorderColor = MaterialTheme.colors.primaryVariant
        ),
        shape = RoundedCornerShape(100),
        placeholder = {
            Text(
                style = MaterialTheme.typography.body2,
                text = "Search...",
                maxLines = 1,
                color = MaterialTheme.colors.onPrimary.copy(alpha = .6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary,
            )
        },
        value = searchQuery,
        singleLine = true,
        keyboardActions = KeyboardActions(onSearch = { onImeSearch() }),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        onValueChange = onSearchQueryChange,
        trailingIcon = {
            AnimatedVisibility(
                visible = searchQuery.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
    if (autoFocus) {
        LaunchedEffect(Unit) {
            delay(200)
            focusRequester.requestFocus()
        }
    }
}