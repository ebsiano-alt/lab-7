package BackEnd;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class Lesson {
    String lessonId;
    String title;
    String content;
    List<String> resources = new ArrayList<>();

    public Lesson() {}

    public Lesson(String title, String content) {
        this.lessonId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
    }

    
    public String getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getResources() { return resources; }

    public void addResource(String r) { if (r != null) resources.add(r); }
}
