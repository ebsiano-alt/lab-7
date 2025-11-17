/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_7;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Course {
    String courseId; 
    String title;
    String description;
    String instructorId;

    List<Lesson> lessons = new ArrayList<>(); // loaded from lessons store
    List<String> studentIds = new ArrayList<>();

    public Course() {}

    public Course(String title, String description, String instructorId) {
        this.courseId = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInstructorId() { return instructorId; }

    public List<Lesson> getLessons() { return lessons; }
    public List<String> getStudentIds() { return studentIds; }

    public void addLesson(Lesson l) { if (l != null) lessons.add(l); }
    public void removeLessonById(String lessonId) {
        lessons.removeIf(l -> l.getLessonId().equals(lessonId));
    }
    public void enrollStudent(String studentId) {
        if (studentId == null) return;
        if (!studentIds.contains(studentId)) studentIds.add(studentId);
    }
}
