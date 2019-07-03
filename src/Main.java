import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String pathToFiles = "C:\\prog\\Java\\vk_posts_to_jekyll\\files";
        File[] files = new File(pathToFiles).listFiles();

        // TODO создать папки для обработанных и необработанных файлов

        for(File file : files){
            ArrayList<String> postLines = readPost(file);
            if(postLines != null){
                // TODO обработка
            }
            // TODO переместить файл к обработанным
        }

    }

    private static ArrayList<String> readPost(File post){
        ArrayList<String> postLines = new ArrayList<>();

        try(Scanner scanner = new Scanner(post)){

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();

                if(line.endsWith("---"))    // репосты не нужны
                    return null;

                if(!line.isBlank() && !line.endsWith("===")){
                    postLines.add(line);
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return postLines;
    }
}
