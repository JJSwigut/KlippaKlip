package utils

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

class KlipboardManager : ClipboardManager {
    private val clipBoard = try {
        Toolkit.getDefaultToolkit().systemClipboard
    } catch(_: Exception) {
        null
    }

    override fun getText(): AnnotatedString? {
        return try {
            (clipBoard?.getData(DataFlavor.stringFlavor) as? String)?.let {
                AnnotatedString(it)
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun setText(annotatedString: AnnotatedString) {
        clipBoard?.setContents(StringSelection(annotatedString.toString()), null)
    }

    override fun hasText(): Boolean {
        return super.hasText()
    }
}