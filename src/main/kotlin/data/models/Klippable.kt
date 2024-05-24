package data.models

import androidx.compose.ui.text.AnnotatedString

sealed interface Klip {
    val klippedText: AnnotatedString
}