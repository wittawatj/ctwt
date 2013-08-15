package com.wittawat.wordseg.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A <code>Trie</code> in which each node
 * stores the next nodes of using a hash
 * of characters.
 * 
 * @author Wittawat Jitkrittum
 */
public class HashTrie<E> extends Trie<E> {

    private Map<Character, Trie> nodeMap;

    @Override
    protected Trie addChildNode(char ch) {
        if (nodeMap == null) {
            nodeMap = new HashMap<Character, Trie>();
        }
        Trie node = nodeMap.get(ch);
        if (node != null) {
            return node;
        }

        HashTrie newNode = new HashTrie();
        nodeMap.put(ch, newNode);
        return newNode;
    }

    @Override
    public Trie lookUpNode(char ch) {
        if (nodeMap == null) {
            return null;
        }
        return nodeMap.get(ch);
    }

//    private Map<Character, Trie> getNodeMap() {
//        if (nodeMap == null) {
//            nodeMap = new HashMap<Character, Trie>();
//        }
//        return nodeMap;
//    }
    //////////////////////////////p
    public static void main(String[] args) {
        Trie hashTrie = new HashTrie();
        hashTrie.put("สวัสดี");
        hashTrie.put("hello");

        System.out.println(hashTrie.containsKey("สวัสด"));
    }
}
