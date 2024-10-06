import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanCoding {
    public static Map<Character, Long> countFrequencies(String pathName) throws IOException {
        Map<Character, Long> frequencies = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(pathName));
        int c = 0;
        while ((c = reader.read()) != -1) {
            char ch = (char) c;
            if (frequencies.containsKey(ch)) {
                frequencies.put(ch, frequencies.get(ch) + 1);
            } else {
                frequencies.put(ch, (long) 1);
            }
        }

        reader.close();
        return frequencies;
    }


    public static BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) throws Exception {
        PriorityQueue<BinaryTree<CodeTreeElement>> newFreq = new PriorityQueue<BinaryTree<CodeTreeElement>>(frequencies.size(), new TreeComparator());
        for (Character b : frequencies.keySet()) {
            CodeTreeElement newElement = new CodeTreeElement(frequencies.get(b), b);
            BinaryTree<CodeTreeElement> binaryChar = new BinaryTree<>(newElement);
            newFreq.add(binaryChar);
        }
        while (newFreq.size() > 1) {
            BinaryTree<CodeTreeElement> t1 = newFreq.remove();
            BinaryTree<CodeTreeElement> t2 = newFreq.remove();
            CodeTreeElement test = new CodeTreeElement((t1.getData().getFrequency() + t2.getData().getFrequency()), null);
            BinaryTree<CodeTreeElement> r = new BinaryTree<>(test, t1, t2);
            newFreq.add(r);
        }
        return newFreq.remove();
    }

        /**
         * Computes the code for all characters in the tree and enters them
         * into a map where the key is a character and the value is the code of 1's and 0's representing
         * that character.
         *
         * @param codeTree the tree for encoding characters produced by makeCodeTree
         * @return the map from characters to codes
         */

    public static Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        Map<Character, String> pairs = new HashMap<>();
        helper(codeTree, "", pairs);
        return pairs;
    }

    private static void helper(BinaryTree<CodeTreeElement> tree, String pathSoFar, Map<Character, String> pairs) {
        if (tree == null) {
            return;
        }
        CodeTreeElement data = tree.getData();
        if (tree.isLeaf()){
            pairs.put(tree.data.getChar(), pathSoFar);
        }
        helper(tree.getLeft(), pathSoFar + "0", pairs);
        helper(tree.getRight(), pathSoFar + "1", pairs);
    }



    static Map<Character, String> MakeCodeMap(String pathName) throws Exception {
        Map<Character, Long> letterFrequencies = countFrequencies(pathName);
        BinaryTree<CodeTreeElement> newTree = makeCodeTree(letterFrequencies);
        Map<Character, String> codeMap = computeCodes(newTree);
        return codeMap;
    }


    static void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws Exception {
            BufferedReader bitInput = new BufferedReader(new FileReader(pathName));
            BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName);

            int intRead = bitInput.read();
            while (intRead > -1) {
                char c = (char) intRead;
                String code = codeMap.get(c);
                for (int i = 0; i < code.length(); i++) {
                    char bitChar = code.charAt(i);
                    boolean bit = (bitChar == '1');
                    bitOutput.writeBit(bit);
                }
                intRead = bitInput.read();
            }
            bitInput.close();
            bitOutput.close();

    }



    public static void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
        BufferedBitReader bitInput = new BufferedBitReader(compressedPathName);
        BufferedWriter bitOutput = new BufferedWriter(new FileWriter(decompressedPathName));

        BinaryTree<CodeTreeElement> currentNode = codeTree;
        while (bitInput.hasNext()) {
            boolean bit = bitInput.readBit();

            if (currentNode.isLeaf()) {
                bitOutput.write(currentNode.getData().getChar());
                currentNode = codeTree;
            }
            if (bit) {
                currentNode = currentNode.getRight();
            } else {
                currentNode = currentNode.getLeft();
            }
        }
        bitInput.close();
        bitOutput.close();
    }


        public static void main(String[] args) throws Exception {
            String pathName = "C:\\Users\\smile\\Documents\\IdeaProjects\\cs10\\PS3\\USConstitution.txt";
            String compressedPathName = "C:\\Users\\smile\\Documents\\IdeaProjects\\cs10\\PS3\\USConstitution-compressed.txt";
            String outputPath = "C:\\Users\\smile\\Documents\\IdeaProjects\\cs10\\PS3\\USConstitution-output.txt";

            compressFile(MakeCodeMap(pathName), pathName, compressedPathName);
            System.out.println(countFrequencies(pathName));

            BinaryTree<CodeTreeElement> codeTree = makeCodeTree(countFrequencies(pathName));
            decompressFile(compressedPathName, outputPath, codeTree);
            Map map = countFrequencies(pathName);
            System.out.println(map.keySet());


    }
}

