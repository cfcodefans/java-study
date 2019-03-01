package cf.study.java.algothrims.tree;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fan on 2017/3/2.
 */
class DictTree {

    static class TrieNode {
        TrieNode parent;
        char value;
        TrieNode[] children = null;
        Set<Integer> childrenIndices = new LinkedHashSet<>();
        boolean end;

        final static AtomicLong COUNT = new AtomicLong(0L);

        public TrieNode(TrieNode parent, char value, TrieNode[] _children) {
            this.parent = parent;
            this.value = value;
            this.children = _children;
            COUNT.incrementAndGet();
        }

        public List<TrieNode> getChildren() {
            if (children == null) return Collections.emptyList();
            List<TrieNode> reList = new ArrayList<>();
            for (int index : childrenIndices) {
                reList.add(children[index]);
            }
            return reList;
        }

        public boolean isAnyChild() {
            return !childrenIndices.isEmpty();
        }

        public TrieNode getOrCreateChild(char ch) {
            int index = ch - 'a';
            if (children == null) {
                children = new TrieNode[26];
            }
            TrieNode child = children[index];
            if (child == null) {
                child = new TrieNode(this, ch);
                children[index] = child;
                childrenIndices.add(index);
            }
            return child;
        }

        public TrieNode() {
            this.children = new TrieNode[26];
        }

        public TrieNode(TrieNode node, char ch) {
            this(node, ch, new TrieNode[26]);
        }

        public static void traverse(TrieNode node, StringBuilder buf, List<String> reList) {
            buf.append(node.value);
            if (!node.isAnyChild()) {
                reList.add(buf.toString());
                return;
            }
            if (node.end) {
                reList.add(buf.toString());
            }
            for (TrieNode child : node.getChildren()) {
                traverse(child, new StringBuilder(buf), reList);
            }
        }

        public List<String> _getChildrenInStrings() {
            ArrayList<String> reList = new ArrayList<>();
            traverse(this, new StringBuilder(), reList);
            return reList;
        }

        public List<String> getChildrenInStrings() {
            List<String> reList = new ArrayList<>();
            if (children == null) return reList;

            for (TrieNode child : getChildren()) {
                Stack<TrieNode> stack = new Stack<>();
                stack.push(child);
                CharBuffer buf = CharBuffer.allocate(50);
                buf.position(0);
                while (!stack.isEmpty()) {
                    TrieNode _param = stack.pop();
                    buf.append(_param.value);
                    if (_param.end) {
                        int pos = buf.position();
                        buf.flip();
                        reList.add(buf.toString());
                        buf.limit(50);
                        buf.position(pos);
                    }

                    if (_param.isAnyChild()) {
                        for (TrieNode _child : _param.getChildren()) {
                            stack.push(_child);
                        }
                    } else {
                        buf.position(buf.position() - 1);//TODO buggy
                    }
                }
            }
            return reList;
        }
    }

    public static List<String> getChildrenWithPrefix(String prefix, TrieNode root) {
        TrieNode _node = root;

        char[] chars = prefix.toCharArray();
        for (int i = 0, j = chars.length; i < j; i++) {
            char ch = chars[i];
            int index = ch - 'a';
            if (_node == null || _node.children == null) return Collections.emptyList();
            _node = _node.children[index];
        }

        return _node.getChildrenInStrings();
    }

    static TrieNode growBranch(String word, TrieNode root) {
        TrieNode _node = root;
        char[] chars = word.toCharArray();
        for (int i = 0, j = chars.length; i < j; i++) {
            char ch = chars[i];
            _node = _node.getOrCreateChild(ch);
        }
        _node.end = true;
        return root;
    }

    static TrieNode createTree(List<String> words) {
        TrieNode root = new TrieNode();
        for (String word : words) {
            growBranch(word, root);
        }
        return root;
    }
}

public class TrieTests {
    static final Logger log = LoggerFactory.getLogger(TrieTests.class);
    static List<String> words = null;

    @BeforeClass
    public static void setupClass() {
        try {
            words = FileUtils.readLines(new File("d:\\data\\20kwords"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testDictTree() {
        List<String> _words = TrieTests.words.subList(0, 5);
        log.info(StringUtils.join(_words, "\n"));
        DictTree.TrieNode root = DictTree.createTree(_words);

        log.info(DictTree.TrieNode.COUNT.toString());

        log.info(StringUtils.join(root._getChildrenInStrings(), "\n"));
    }
}
