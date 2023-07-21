package me.leon

import java.io.File
import me.leon.ext.toFile

/**
 * @author Leon
 * @since 2023-06-30 14:23
 * @email deadogone@gmail.com
 */
val USER = System.getenv("userprofile")
val ONE_DRIVE = System.getenv("onedrive")
const val MDX_DIR = "E:/software/Lily5/soft/GoldenDict/content"

const val MDX = "$MDX_DIR/The little dict多功能/TLD.mdx"
val DESKTOP = "$USER${File.separator}Desktop"

val ONE_DRIVE_DIR = "$ONE_DRIVE${File.separator}我的文档${File.separator}学习"

val VOCABULARY_FILE = "$ONE_DRIVE_DIR/vocabulary.txt".toFile()
val VOCABULARY_FILE2 = "$ONE_DRIVE_DIR/vocabulary2.txt".toFile()
val translateFile = "$DESKTOP/trans.txt"
val SYLLABLE_FILE = "$ONE_DRIVE_DIR/syllable.txt".toFile()
val SYLLABLE_NO_DATA_FILE = "$ONE_DRIVE_DIR/syllable-no.txt".toFile()
