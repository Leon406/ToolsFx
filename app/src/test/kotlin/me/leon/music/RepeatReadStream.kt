package me.leon.music

import java.io.*
import java.util.Arrays

class RepeatReadStream {
    /** 会读多次 */
    private var fis: FileInputStream? = null
    private var file: File? = null

    /** 文件字节输出 */
    fun outPutFile(targetFile: File, fileData: ByteArray?) {
        targetFile.outputStream().use { it.write(fileData) }
    }

    @Throws(FileNotFoundException::class)
    fun setFile(sourceFile: File) {
        if (!sourceFile.exists() || !sourceFile.isFile) {
            throw FileNotFoundException("File is can not readable! please")
        }
        if (fis == null) {
            fis = FileInputStream(sourceFile)
        }
        file = sourceFile
    }

    @Throws(IOException::class)
    fun readFile(length: Int): ValidByte {
        requireNotNull(fis) { "fileStream has not been init,check logic!" }
        val result = ValidByte(length)
        try {
            result.length = fis!!.read(result.data)
        } catch (e: Exception) {
            e.printStackTrace()
            fis!!.close()
        } finally {
            if (result.length == -1) {
                fis!!.close()
                fis = null
            }
        }
        return result
    }

    @Throws(FileNotFoundException::class)
    fun readFile(): ValidByte {
        requireNotNull(fis) { "fileStream has not been init,check logic!" }
        val totalData = ByteArray(file!!.length().toInt())
        try {
            val length = fis!!.read(totalData)
            val validByte = ValidByte(length)
            validByte.data = totalData
            return validByte
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (fis != null) {
                    fis!!.close()
                    fis = null
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ValidByte(0)
    }

    fun skip(offset: Int) {
        if (fis != null) {
            try {
                fis!!.skip(offset.toLong())
            } catch (e: IOException) {
                e.printStackTrace()
                try {
                    fis!!.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }
            }
        }
    }

    fun close() {
        if (fis != null) {
            try {
                fis!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    class ValidByte @JvmOverloads constructor(var length: Int = 0) {
        var data: ByteArray

        init {
            data = ByteArray(length)
        }

        val validByte: ByteArray
            get() = if (length == 0) ByteArray(0) else Arrays.copyOfRange(data, 0, length)
    }
}
