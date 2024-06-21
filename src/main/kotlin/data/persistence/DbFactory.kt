package data.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.jjswigut.klippaklip.Database
import java.io.File


object DbFactory {

    fun createDb(): Database {
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        Database.Schema.create(driver)
        return Database(driver)
    }

    private val databaseFile: File
        get() = File(appDir.also { if (!it.exists()) it.mkdirs() }, "klippa.db")

    private val appDir: File
        get() {
            val os = System.getProperty("os.name").lowercase()
            return when {
                os.contains("win") -> {
                    File(System.getenv("AppData"), "klippa/db")
                }

                os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
                    File(System.getProperty("user.home"), ".klippa")
                }

                os.contains("mac") -> {
                    File(System.getProperty("user.home"), "Library/Application Support/klippa")
                }

                else -> error("Unsupported operating system")
            }
        }
}