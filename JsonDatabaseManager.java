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

    public void saveUsers() {
        JSONArray arr = new JSONArray();
        for (User u : usersById.values()) {
            JSONObject o = new JSONObject();
            o.put("userId", u.userId);
            o.put("role", u.role);
            o.put("username", u.username);
            o.put("email", u.email);
            o.put("passwordHash", u.passwordHash);
            if (u instanceof Student s) {
                o.put("enrolledCourseIds", new JSONArray(s.enrolledCourseIds));
                o.put("completedLessonIds", new JSONArray(s.completedLessonIds));
            } else if (u instanceof Instructor ins) {
                o.put("createdCourseIds", new JSONArray(ins.createdCourseIds));
            }
            arr.put(o);
        }
        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            fw.write(arr.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized boolean addUser(User u) {
        if (u == null || u.getEmail() == null) return false;
        if (findUserByEmail(u.getEmail()) != null) return false;
        if (u.getUserId() == null) u.userId = UUID.randomUUID().toString();
        usersById.put(u.getUserId(), u);
        saveUsers();
        return true;
    }

    public synchronized User findUserByEmail(String email) {
        if (email == null) return null;
        for (User u : usersById.values()) {
            if (email.equalsIgnoreCase(u.getEmail())) return u;
        }
        return null;
    }

    public synchronized User findUserById(String id) {
        return usersById.get(id);
    }

    public synchronized void updateUser(User u) {
        if (u == null || u.getUserId() == null) return;
        usersById.put(u.getUserId(), u);
        saveUsers();
    }


    private void loadCourses() {
        File f = new File(COURSES_FILE);
        if (!f.exists()) {
            saveCourses();
            return;
        }

        try {
            String content = Files.readString(f.toPath());
            if (content.isBlank()) return;
            JSONArray arr = new JSONArray(content);
            coursesById.clear();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Course c = new Course();
                c.courseId = o.optString("courseId", UUID.randomUUID().toString());
                c.title = o.optString("title", "");
                c.description = o.optString("description", "");
                c.instructorId = o.optString("instructorId", "");

              
                JSONArray ls = o.optJSONArray("lessons");
                if (ls != null) {
                    for (int j = 0; j < ls.length(); j++) {
                        Object entry = ls.get(j);
                        if (entry instanceof JSONObject) {
                            JSONObject lo = (JSONObject) entry;
                            Lesson l = new Lesson();
                            l.lessonId = lo.optString("lessonId", UUID.randomUUID().toString());
                            l.title = lo.optString("title", "");
                            l.content = lo.optString("content", "");
                            lessonsById.putIfAbsent(l.lessonId, l);
                            c.getLessons().add(l);
                        } else {
                            
                            String lessonId = ls.getString(j);
                            Lesson l = lessonsById.get(lessonId);
                            if (l != null) c.getLessons().add(l);
                        }
                    }
                }

              
                JSONArray st = o.optJSONArray("studentIds");
                if (st != null) for (int j = 0; j < st.length(); j++) c.studentIds.add(st.getString(j));

                coursesById.put(c.courseId, c);
            }
        } catch (Exception e) {
            System.err.println("Error loading courses: " + e.getMessage());
            coursesById.clear();
        }
    }

    public void saveCourses() {
        JSONArray arr = new JSONArray();
        for (Course c : coursesById.values()) {
            JSONObject o = new JSONObject();
            o.put("courseId", c.courseId);
            o.put("title", c.title);
            o.put("description", c.description);
            o.put("instructorId", c.instructorId);
          
            JSONArray ls = new JSONArray();
            for (Lesson l : c.getLessons()) ls.put(l.getLessonId());
            o.put("lessons", ls);
            o.put("studentIds", new JSONArray(c.getStudentIds()));
            arr.put(o);
        }
        try (FileWriter fw = new FileWriter(COURSES_FILE)) {
            fw.write(arr.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void addCourse(Course c) {
        if (c == null) return;
        if (c.courseId == null) c.courseId = UUID.randomUUID().toString();
        coursesById.put(c.courseId, c);
        saveCourses();
    }

    public synchronized Course findCourseById(String id) { return coursesById.get(id); }

    public synchronized List<Course> listAllCourses() { return new ArrayList<>(coursesById.values()); }

    public synchronized void updateCourse(Course c) {
        if (c == null || c.courseId == null) return;
        coursesById.put(c.courseId, c);
        saveCourses();
    }

    public synchronized void deleteCourse(String id) {
        coursesById.remove(id);
        saveCourses();
    }
}