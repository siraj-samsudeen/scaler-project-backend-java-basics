package scaler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MergeSortFiles {
    public static void main(String[] args) {
        File folder = new File("app/files");
        System.out.println("MergeSortFiles at : " + folder.getAbsolutePath());
        List<Integer> list = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.getName().startsWith("out")) {
                continue;
            }
            System.out.println("processing..." + file.getName());
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextInt()) {
                    list.add(scanner.nextInt());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        list.sort(null);

        File outputFile = new File(folder, "out.txt");
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            for (Integer integer : list) {
                writer.println(integer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}