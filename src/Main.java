import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final SimpleDateFormat inputDate = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat outputDate = new SimpleDateFormat("yyyy-MM-dd");

    private static String pathToFiles;
    private static String pathToProcessed;
    private static String pathToOutput;

    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    public static void main(String[] args) {

        fillPaths();

        File[] files = new File(pathToFiles).listFiles();

        new File(pathToProcessed).mkdir();
        new File(pathToOutput).mkdir();

        for(File file : files){
            if(file.isFile()) {
                ArrayList<String> postLines = readPost(file);
                if (postLines != null) {
                    Post post = generatePostFromLines(postLines);
                    if(writePostOut(post)){
                        moveFileToProcessed(file);
                    }
                } else {
                    moveFileToProcessed(file);
                }
            }
        }

    }

    private static void moveFileToProcessed(File file) {
        try {
            Files.move(
                    file.toPath(),
                    new File(pathToProcessed + file.getName()).toPath(),
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fillPaths() {
        pathToFiles = "C:\\prog\\Java\\vk_posts_to_jekyll\\files";
        pathToProcessed = pathToFiles + "\\processed\\";
        pathToOutput = pathToFiles + "\\output\\";
    }

    private static boolean writePostOut(Post post) {
        String fileName = generateFileName(post);
        File file = new File(fileName);

        try {
            file.createNewFile();

            try (FileWriter fileWriter = new FileWriter(file)){

                fileWriter.write("---\n");
                fileWriter.write("layout: post\n");
                fileWriter.write("title:  \"" + post.title + "\"\n");
                fileWriter.write("date:   " + post.date + "\n");
                fileWriter.write("tags:   \n");
                fileWriter.write("---\n");

                for(String line : post.content){
                    if(line.charAt(0) != '-') {
                        fileWriter.write("\n");
                    }
                    fileWriter.write(line + "\n");
                }

                fileWriter.flush();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error in file: \n" + fileName + "\n");
            e.printStackTrace();
        }
        return false;
    }

    private static String generateFileName(Post post) {
        String fileName = post.date.split(" ")[0] + "-" + post.title + ".md";

        for(Character ch : ILLEGAL_CHARACTERS){
            fileName = fileName.replace(ch, '-');
        }

        fileName = pathToOutput + fileName;

        return fileName;
    }

    private static Post generatePostFromLines(ArrayList<String> postLines) {
        Post post = new Post();

        post.date = parseDate(postLines.get(0).split(" "));
        postLines.remove(0);

        post.title = postLines.get(0);

        parsePhotosAndFiles(post, postLines);

        parseLists(postLines);

        post.content = combineContent(post, postLines);

        return post;
    }

    private static void parseLists(ArrayList<String> postLines) {
        for(int i = 0; i < postLines.size(); ++i)
        {
            if(postLines.get(i).charAt(0) == '—' || postLines.get(i).charAt(0) == '•' ){
                postLines.set(i, "-" + postLines.get(i).substring(1));
            }
        }
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
        String rawDate = dateLine[dateLine.length-3];
        String rawTime = dateLine[dateLine.length-1];
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
