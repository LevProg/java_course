import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class HuffmanNode {
    char character;
    int frequency;
    HuffmanNode left, right;

    HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.left = this.right = null;
    }
}

class HuffmanComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return Integer.compare(x.frequency, y.frequency);
    }
}

public class Huffman {

    private static Map<Character, String> huffmanCodes = new HashMap<>();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введите путь к файлу для сжатия: ");
            String inputFilePath = scanner.nextLine();
            System.out.println("Введите путь для сохранения сжатого файла (bin): ");
            String compressedFilePath = scanner.nextLine();
            System.out.println("Введите путь для сохранения декодированного файла: ");
            String decompressedFilePath = scanner.nextLine();

            // Чтение входного файла
            String input = Files.readString(Paths.get(inputFilePath));
            if (input.isEmpty()) {
                System.err.println("Файл пуст. Программа завершена.");
                return;
            }

            // Вычисление частот символов
            Map<Character, Integer> frequencyMap = calculateFrequency(input);

            // Построение дерева Хаффмана
            HuffmanNode root = buildHuffmanTree(frequencyMap);

            // Генерация кодов Хаффмана
            generateHuffmanCodes(root, "");

            // Сжатие данных
            String compressed = compress(input);

            // Запись сжатых данных в файл
            writeBinaryFileWithTable(compressedFilePath, frequencyMap, compressed);

            // Декодирование данных из файла
            String decompressed = decodeWithTable(compressedFilePath);

            // Сохранение декодированных данных
            Files.write(Paths.get(decompressedFilePath), decompressed.getBytes());

            // Оценка сжатия
            evaluateCompression(input, compressedFilePath, decompressed);
        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
        }
    }

    private static Map<Character, Integer> calculateFrequency(String input) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : input.toCharArray()) {
            frequencyMap.merge(c, 1, Integer::sum);
        }
        return frequencyMap;
    }

    private static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        if (frequencyMap.size() == 1) {
            // Специальная обработка для одного символа
            char singleChar = frequencyMap.keySet().iterator().next();
            return new HuffmanNode(singleChar, frequencyMap.get(singleChar));
        }

        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>(new HuffmanComparator());
        frequencyMap.forEach((character, frequency) -> priorityQueue.add(new HuffmanNode(character, frequency)));

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();
            HuffmanNode newNode = new HuffmanNode('\0', left.frequency + right.frequency);
            newNode.left = left;
            newNode.right = right;
            priorityQueue.add(newNode);
        }
        return priorityQueue.poll();
    }

    private static void generateHuffmanCodes(HuffmanNode node, String code) {
        if (node == null) return;

        if (node.left == null && node.right == null) {
            huffmanCodes.put(node.character, code.isEmpty() ? "0" : code); // Присваиваем "0", если код пустой
        } else {
            generateHuffmanCodes(node.left, code + "0");
            generateHuffmanCodes(node.right, code + "1");
        }
    }

    private static String compress(String input) {
        StringBuilder compressed = new StringBuilder();
        for (char c : input.toCharArray()) {
            compressed.append(huffmanCodes.get(c));
        }
        return compressed.toString();
    }

    private static void writeBinaryFileWithTable(String filePath, Map<Character, Integer> frequencyMap, String binaryData) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
            // Запись размера таблицы частот
            dos.writeInt(frequencyMap.size());
            for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
                dos.writeChar(entry.getKey());
                dos.writeInt(entry.getValue());
            }

            // Запись длины бинарных данных
            dos.writeInt(binaryData.length());

            // Преобразование строки битов в массив байтов
            byte[] bytes = new byte[(binaryData.length() + 7) / 8];
            for (int i = 0; i < binaryData.length(); i++) {
                int byteIndex = i / 8;
                int bitIndex = 7 - (i % 8);
                bytes[byteIndex] |= (binaryData.charAt(i) - '0') << bitIndex;
            }
            dos.write(bytes);
        }
    }

    private static String decodeWithTable(String filePath) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            // Чтение таблицы частот
            int mapSize = dis.readInt();
            Map<Character, Integer> frequencyMap = new HashMap<>();
            for (int i = 0; i < mapSize; i++) {
                char character = dis.readChar();
                int frequency = dis.readInt();
                frequencyMap.put(character, frequency);
            }

            // Построение дерева Хаффмана
            HuffmanNode root = buildHuffmanTree(frequencyMap);

            // Чтение длины бинарных данных
            int binaryDataLength = dis.readInt();

            // Чтение бинарных данных
            StringBuilder binaryData = new StringBuilder();
            int byteRead;
            while ((byteRead = dis.read()) != -1) {
                binaryData.append(String.format("%8s", Integer.toBinaryString(byteRead & 0xFF)).replace(' ', '0'));
            }
            binaryData.setLength(binaryDataLength);

            // Декодирование данных
            return decode(root, binaryData.toString());
        }
    }

    private static String decode(HuffmanNode root, String binaryData) {
        StringBuilder result = new StringBuilder();

        if (root.left == null && root.right == null) {
            // Если дерево состоит из одного узла, просто повторяем символ
            for (int i = 0; i < binaryData.length(); i++) {
                result.append(root.character);
            }
            return result.toString();
        }

        HuffmanNode current = root;
        for (char bit : binaryData.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;
            if (current.left == null && current.right == null) {
                result.append(current.character);
                current = root;
            }
        }
        return result.toString();
    }

    private static void evaluateCompression(String input, String compressedFilePath, String decompressed) throws IOException {
        long originalSize = input.length() * 8;
        long compressedSize = new File(compressedFilePath).length() * 8;
        double compressionRatio = (double) compressedSize / originalSize;

        System.out.printf("Коэффициент сжатия: %.2f%%%n", compressionRatio * 100);
        if (input.equals(decompressed)) {
            System.out.println("Декодирование выполнено успешно: исходная строка совпадает с декодированной.");
        } else {
            System.err.println("Ошибка декодирования: исходная строка не совпадает с декодированной.");
        }
    }
}