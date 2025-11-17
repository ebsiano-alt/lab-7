package lab_7;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.*;


public class JsonDatabaseManager {

    private static final String USERS_FILE = "users.json";
    private static final String COURSES_FILE = "courses.json";
    private static final String LESSONS_FILE = "lessons.json";

    private final Map<String, User> usersById = new HashMap<>();
    private final Map<String, Course> coursesById = new LinkedHashMap<>();
    private final Map<String, Lesson> lessonsById = new LinkedHashMap<>();

    public JsonDatabaseManager() {
        loadLessons();     
        loadUsers();
        loadCourses();
    }

    private void loadLessons() {
        File f = new File(LESSONS_FILE);
        if (!f.exists()) {
        
            saveLessons();
            return;
        }

        try {
            String content = Files.readString(f.toPath());
            if (content.isBlank()) return;
            JSONArray arr = new JSONArray(content);
            lessonsById.clear();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Lesson l = new Lesson();
                l.lessonId = o.optString("lessonId", UUID.randomUUID().toString());
                l.title = o.optString("title", "");
                l.content = o.optString("content", "");
                JSONArray res = o.optJSONArray("resources");
                if (res != null) {
                    for (int j = 0; j < res.length(); j++) l.resources.add(res.getString(j));
                }
                lessonsById.put(l.lessonId, l);
            }
        } catch (Exception e) {
            System.err.println("Error loading lessons: " + e.getMessage());
            lessonsById.clear();
        }
    }

    public void saveLessons() {
        JSONArray arr = new JSONArray();
        for (Lesson l : lessonsById.values()) {
            JSONObject o = new JSONObject();
            o.put("lessonId", l.lessonId);
            o.put("title", l.title);
            o.put("content", l.content);
            o.put("resources", new JSONArray(l.resources));
            arr.put(o);
        }
        try (FileWriter fw = new FileWriter(LESSONS_FILE)) {
            fw.write(arr.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Lesson getLessonById(String id) {
        return lessonsById.get(id);
    }

    public void addOrUpdateLesson(Lesson l) {
        if (l == null || l.lessonId == null) return;
        lessonsById.put(l.lessonId, l);
        saveLessons();
    }


    private void loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) {
            saveUsers();
            return;
        }

        try {
            String content = Files.readString(f.toPath());
            if (content.isBlank()) return;
            JSONArray arr = new JSONArray(content);
            usersById.clear();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String role = o.optString("role", "student");

                if ("student".equalsIgnoreCase(role)) {
                    Student s = new Student();
                    s.userId = o.optString("userId", UUID.randomUUID().toString());
                    s.role = "student";
                    s.username = o.optString("username", "");
                    s.email = o.optString("email", "");
                    s.passwordHash = o.optString("passwordHash", "");
                    JSONArray ec = o.optJSONArray("enrolledCourseIds");
                    if (ec != null) for (int j = 0; j < ec.length(); j++) s.enrolledCourseIds.add(ec.getString(j));
                    JSONArray cl = o.optJSONArray("completedLessonIds");
                    if (cl != null) for (int j = 0; j < cl.length(); j++) s.completedLessonIds.add(cl.getString(j));
                    usersById.put(s.userId, s);
                } else {
                    Instructor iUser = new Instructor();
                    iUser.userId = o.optString("userId", UUID.randomUUID().toString());
                    iUser.role = "instructor";
                    iUser.username = o.optString("username", "");
                    iUser.email = o.optString("email", "");
                    iUser.passwordHash = o.optString("passwordHash", "");
                    JSONArray cc = o.optJSONArray("createdCourseIds");
                    if (cc != null) for (int j = 0; j < cc.length(); j++) iUser.createdCourseIds.add(cc.getString(j));
                    usersById.put(iUser.userId, iUser);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            usersById.clear();
        }
    }

   