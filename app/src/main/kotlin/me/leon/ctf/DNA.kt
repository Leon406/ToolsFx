package me.leon.ctf

// ported from https://github.com/karma9874/DNA-Cipher-Script-CTF

val dnaBin = mapOf("00" to "A", "10" to "C", "01" to "G", "11" to "T")
val decodeBin = dnaBin.values.zip(dnaBin.keys).toMap()
val dnaDecodeMap =
    mapOf(
        "AAA" to "a",
        "AAC" to "b",
        "AAG" to "c",
        "AAT" to "d",
        "ACA" to "e",
        "ACC" to "f",
        "ACG" to "g",
        "ACT" to "h",
        "AGA" to "i",
        "AGC" to "j",
        "AGG" to "k",
        "AGT" to "l",
        "ATA" to "m",
        "ATC" to "n",
        "ATG" to "o",
        "ATT" to "p",
        "CAA" to "q",
        "CAC" to "r",
        "CAG" to "s",
        "CAT" to "t",
        "CCA" to "u",
        "CCC" to "v",
        "CCG" to "w",
        "CCT" to "x",
        "CGA" to "y",
        "CGC" to "z",
        "CGG" to "A",
        "CGT" to "B",
        "CTA" to "C",
        "CTC" to "D",
        "CTG" to "E",
        "CTT" to "F",
        "GAA" to "G",
        "GAC" to "H",
        "GAG" to "I",
        "GAT" to "J",
        "GCA" to "K",
        "GCC" to "L",
        "GCG" to "M",
        "GCT" to "N",
        "GGA" to "O",
        "GGC" to "P",
        "GGG" to "Q",
        "GGT" to "R",
        "GTA" to "S",
        "GTC" to "T",
        "GTG" to "U",
        "GTT" to "V",
        "TAA" to "W",
        "TAC" to "X",
        "TAG" to "Y",
        "TAT" to "Z",
        "TCA" to "1",
        "TCC" to "2",
        "TCG" to "3",
        "TCT" to "4",
        "TGA" to "5",
        "TGC" to "6",
        "TGG" to "7",
        "TGT" to "8",
        "TTA" to "9",
        "TTC" to "0",
        "TTG" to " ",
        "TTT" to "."
    )
val dnaMap = dnaDecodeMap.values.zip(dnaDecodeMap.keys).toMap()

fun String.dnaDecode() =
    split("[^01AGCTagct]".toRegex())
        .map {
            if (it.contains("0|1".toRegex())) {
                dnaDecodeMap[it.chunked(2).map { dnaBin[it] }.joinToString("")]
            } else {
                dnaDecodeMap[it]
            }
        }
        .joinToString("")

fun String.dna() = map { dnaMap[it.toString()] ?: it }.joinToString(" ")
