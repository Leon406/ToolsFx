package me.leon.plugin

import java.sql.DriverManager
import org.junit.Test

class DbTest {
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
}
