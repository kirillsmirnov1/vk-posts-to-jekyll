import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private static String parseDate(String[] dateLine) {
        String rawDate = dateLine[78];
        String rawTime = dateLine[80];
        String date = null;

        try {
            Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(rawDate);
            date = new SimpleDateFormat("yyyy-MM-dd").format(date1)
                    + " " + rawTime + ":00 +0300";
        } catch (ParseException e){
            e.printStackTrace();
        }

        return date;
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
