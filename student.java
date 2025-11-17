package lab_7;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    // package-private lists so JsonDatabaseManager can assign and read them directly
    List<String> enrolledCourseIds = new ArrayList<>();
    List<String> completedLessonIds = new ArrayList<>();

    public Student() { super(); this.role = "student"; }

    public Student(String username, String email, String passwordHash) {
        super("student", username, email, passwordHash);
    }

    public List<String> getEnrolledCourseIds() { return enrolledCourseIds; }
    public List<String> getCompletedLessonIds() { return completedLessonIds; }

    public void enrollCourse(String courseId) {
        if (courseId == null) return;
        if (!enrolledCourseIds.contains(courseId)) enrolledCourseIds.add(courseId);
    }

    public void completeLesson(String lessonId) {
        if (lessonId == null) return;
        if (!completedLessonIds.contains(lessonId)) completedLessonIds.add(lessonId);
    }
}