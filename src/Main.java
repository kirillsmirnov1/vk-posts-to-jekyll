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
                // TODO записать пост
            }
            // TODO переместить файл к обработанным
        }

    }

    private static Post generatePostFromLines(ArrayList<String> postLines) {
        Post post = new Post();

        post.date = parseDate(postLines.get(0).split(" "));
        postLines.remove(0);

        post.title = postLines.get(0);

        parsePhotosAndFiles(post, postLines);

        post.content = combineContent(post, postLines);

        return post;
    }

    private static ArrayList<String> combineContent(Post post, ArrayList<String> lines) {
        ArrayList<String> content = new ArrayList<>();

        content.add(lines.get(0));

        if(lines.size() > 1) {
            content.add(lines.get(1));
        }

        for(String img : post.images) {
            content.add(img);
        }
        
        content.add("<!--excerpt-->");

        for(int i = 2; i < lines.size(); ++i){
            content.add(lines.get(i));
        }

        for(String file : post.files){
            content.add(file);
        }

        return content;
    }

    private static void parsePhotosAndFiles(Post post, ArrayList<String> lines) {
        ArrayList<String> linesToRemove = new ArrayList<>();

        for(int i = 0; i < lines.size(); ++i){
            if(lines.get(i).contains("[фотография]")){
                post.images.add("![img](" + lines.get(i).split(" ")[1] + ")");
                linesToRemove.add(lines.get(i));
            } else if (lines.get(i).contains("[файл]")){
                post.files.add("[" + lines.get(i).split(" ")[1] + "]("
                        + lines.get(i+1).split("\\?")[0] + ")");
                linesToRemove.add(lines.get(i));
                linesToRemove.add(lines.get(i+1));
                i++;
            }
        }

        for(String lineToRemove : linesToRemove){
            lines.remove(lineToRemove);
        }
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
