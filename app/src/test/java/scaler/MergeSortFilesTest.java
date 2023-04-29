package scaler;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortFilesTest {

    @Test
    void testMergeSortFiles() throws Exception {
        // Create test files
        File folder = new File("app/files");
        File file1 = new File(folder, "test1.txt");
        File file2 = new File(folder, "test2.txt");
        List<Integer> list1 = Arrays.asList(4, 2, 1);
        List<Integer> list2 = Arrays.asList(3, 5, 6);
        try (PrintWriter writer1 = new PrintWriter(file1)) {
            for (Integer integer : list1) {
                writer1.println(integer);
            }
        }
        try (PrintWriter writer2 = new PrintWriter(file2)) {
            for (Integer integer : list2) {
                writer2.println(integer);
            }
        }

        // Call the method to be tested
        MergeSortFiles.main(null);

        // Verify output file
        List<Integer> expectedList = new ArrayList<>(list1);
        expectedList.addAll(list2);
        expectedList.sort(null);
        File outputFile = new File(folder, "out.txt");
        try (Scanner scanner = new Scanner(outputFile)) {
            List<Integer> actualList = new ArrayList<>();
            while (scanner.hasNextInt()) {
                actualList.add(scanner.nextInt());
            }
            assertEquals(expectedList, actualList);
        }

        // Delete test files
        file1.delete();
        file2.delete();
        outputFile.delete();
    }
}
