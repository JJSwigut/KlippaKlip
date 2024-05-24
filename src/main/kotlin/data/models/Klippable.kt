package data.models

import androidx.compose.ui.text.AnnotatedString

sealed interface Klippable {
    val klippedText: AnnotatedString
}