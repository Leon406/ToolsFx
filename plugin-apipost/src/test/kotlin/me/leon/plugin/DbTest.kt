package me.leon.plugin

import java.sql.DriverManager
import kotlin.test.Test
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.groupBy

class DbTest {

    /** download from https://leon.lanzoub.com/ivWsX14euing */
    val dbPath = "E:\\ciku.db"

    @Test
    fun createDb() {
        val connection = DriverManager.getConnection("jdbc:sqlite:sample.db")
        connection.createStatement().apply {
            queryTimeout = 30
            //            executeUpdate("drop table if exists person")
            executeUpdate(
                "create table if not exists  person (id INTEGER PRIMARY KEY  AUTOINCREMENT  , name string)"
            )

            runCatching {
                //                executeUpdate("insert into person values(1, 'leo')")
                //                executeUpdate("insert into person values(2, 'yui')")
                executeUpdate("insert into person(name) values('yui')")
                executeUpdate("insert into person(name) values('leon')")
                executeUpdate("insert into person(name) values('lamb')")
            }

            executeQuery("select * from person").run {
                while (this.next()) {
                    println(this.getString("name") + this.getString("id"))
                }
            }

            executeQuery("select * from person where name ='leon'").run {
                while (this.next()) {
                    println(this.getString("name") + this.getString("id"))
                }
            }
            executeQuery("select * from person where id in (1,2,3,4)").run {
                while (this.next()) {
                    println(this.getString("name") + this.getString("id"))
                }
            }
        }
    }

    @Test
    fun readDb() {

        val tables = mutableSetOf<String>()
        DriverManager.getConnection("jdbc:sqlite:$dbPath").use {
            it.createStatement()
                .executeQuery("SELECT name FROM sqlite_master WHERE type='table'")
                .run {
                    while (this.next()) {
                        val table = this.getString("name")
                        tables.add(table)
                        println(table)
                        it.metaData.getColumns(null, null, table, null).run {
                            while (next()) {
                                println("${getString("COLUMN_NAME")} ${getString("TYPE_NAME")}")
                            }
                        }
                    }
                }
        }
    }

    @Test
    fun orm() {
        // https://www.ktorm.org/zh-cn/quick-start.html
        val database = Database.connect("jdbc:sqlite:$dbPath")

        //        database
        //            .from(OxfordWord)
        //            .select(OxfordWord.word, OxfordWord.wordSearch)
        //            .where { (OxfordWord.word like "a%") or (OxfordWord.word like "% a%") }
        //            .forEach { row -> println(row[OxfordWord.word]) }

        // select * from oxford_x_sngs where type='idm' and name like '% look%' or name like 'look%'
        // select * from oxford_x_sngs where type='pvg' and name like '%look%'
        // select * from oxford_x_sngs where type='sng' and word = 'look'

        val keyword = "look"
        database
            .from(OxfordMeaning)
            .select(
                OxfordMeaning.type,
                OxfordMeaning.word,
                OxfordMeaning.defEng,
                OxfordMeaning.defSimp
            )
            .where { OxfordMeaning.name like "%$keyword%" }
            .forEach { row -> println(row[OxfordMeaning.defEng]) }
    }

    @Test
    fun ormBinding() {
        // https://www.ktorm.org/zh-cn/quick-start.html
        val database = Database.connect("jdbc:sqlite:$dbPath")

        val keyword = "look"
        database.oxfordMeaning
            .filter { it.name like "%$keyword%" }
            .groupBy { it.type }
            .forEach { t, u ->
                println("type: $t")
                println(
                    u.joinToString(System.lineSeparator()) {
                        "${it.name} ${it.defEng} ${it.defSimp}"
                    }
                )
            }
    }
}
