package me.leon.misc.zhconvert

data class TrieNode(val children: MutableMap<Char, TrieNode?> = mutableMapOf()) {
    fun containsKey(ch: Char): Boolean {
        return children.containsKey(ch)
    }

    operator fun get(ch: Char): TrieNode? {
        return children[ch]
    }

    operator fun set(ch: Char, node: TrieNode?) {
        children[ch] = node
    }
}

data class Trie(val root: TrieNode = TrieNode()) {

    fun insert(word: String) {
        var node: TrieNode? = root
        for (ch in word) {
            if (!node!!.containsKey(ch)) {
                node[ch] = TrieNode()
            }
            node = node[ch]
        }
    }

    fun search(word: String): Boolean {
        val node = searchPrefix(word)
        return node != null
    }

    fun search(char: Char): Boolean {
        return root[char] != null
    }

    private fun searchPrefix(prefix: String): TrieNode? {
        var node: TrieNode? = root
        for (ch in prefix) {
            node =
                if (node!!.containsKey(ch)) {
                    node[ch]
                } else {
                    return null
                }
        }
        return node
    }
}
