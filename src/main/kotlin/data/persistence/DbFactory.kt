package data.persistence

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.jjswigut.klippaklip.Database


object DbFactory {

    fun createDb(): Database {
        val driver = JdbcSqliteDriver("jdbc:sqlite:klippaklip.db")
        Database.Schema.create(driver)
        return Database(driver)
    }
}