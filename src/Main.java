import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final SimpleDateFormat inputDate = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        String pathToFiles = "C:\\prog\\Java\\vk_posts_to_jekyll\\files";
        File[] files = new File(pathToFiles).listFiles();

        // TODO создать папки для обработанных и необработанных файлов

        for(File file : files){
            ArrayList<String> postLines = readPost(file);
            if(postLines != null){
                Post post = generatePostFromLines(postLines);
            }
            // TODO переместить файл к обработанным
        }

    }

    private static Post generatePostFromLines(ArrayList<String> postLines) {
        Post post = new Post();

        post.date = parseDate(postLines.get(0).split(" "));

        return null;
    }

    private static String parseDate(String[] dateLine) {
        String rawDate = dateLine[78];
        String rawTime = dateLine[80];
        String date = null;

        try {
            date = outputDate.format(inputDate.parse(rawDate))
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
