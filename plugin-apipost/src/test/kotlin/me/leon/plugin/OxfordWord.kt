package me.leon.plugin

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*

/**
 * @author Leon
 * @since 2023-08-03 16:18
 * @email deadogone@gmail.com
 */
object OxfordWord : Table<Nothing>("oxford_x_word") {
    val id = varchar("id").primaryKey()
    val word = varchar("word")
    val wordBody = varchar("word_body")
    val wordSearch = varchar("word_search")
    val num = int("num")
}

object OxfordSentence : Table<Nothing>("oxford_x_xg") {
    val id = varchar("id").primaryKey()
    val sourceId = varchar("source_id")
    val eng = varchar("x_eng")
    val simple = varchar("x_simp")
    val audio = varchar("x_audio")
}

interface Meaning : Entity<Meaning> {
    val id: String
    val sourceId: String
    val type: String
    val word: String
    val pos: String
    val defEng: String
    val defSimp: String
    val name: String

    companion object : Entity.Factory<Meaning>()
}

object OxfordMeaning : Table<Meaning>("oxford_x_sngs") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val sourceId = varchar("source_id").bindTo { it.sourceId }
    val type = varchar("type").bindTo { it.type }
    val word = varchar("word").bindTo { it.word }
    val pos = varchar("pos").bindTo { it.pos }
    val defEng = varchar("def_eng").bindTo { it.defEng }
    val defSimp = varchar("def_simp").bindTo { it.defSimp }
    val prongs = varchar("prongs")
    val name = varchar("name").bindTo { it.name }
}

val Database.oxfordMeaning
    get() = this.sequenceOf(OxfordMeaning)

object OxfordSyllable : Table<Nothing>("oxford_x_infl_word") {
    val id = varchar("id").primaryKey()
    val allWord = varchar("all_word")
    val word = varchar("word")
    val wordId = varchar("word_id")
}
