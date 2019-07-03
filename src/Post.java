import java.util.ArrayList;

public class Post {
    public String title;
    public String date;
    public ArrayList<String> images;
    public ArrayList<String> files;
    public ArrayList<String> content;

    Post(){
        title   = null;
        date    = null;
        images  = new ArrayList<>();
        files   = new ArrayList<>();
        content = new ArrayList<>();
    }
}
