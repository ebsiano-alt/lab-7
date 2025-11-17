/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_7;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    List<String> createdCourseIds = new ArrayList<>();

    public Instructor() { super(); this.role = "instructor"; }
    public Instructor(String username, String email, String passwordHash) {
        super("instructor", username, email, passwordHash);
    }

    public List<String> getCreatedCourseIds() { return createdCourseIds; }

    public void addCourse(String courseId) {
        if (courseId == null) return;
        if (!createdCourseIds.contains(courseId)) createdCourseIds.add(courseId);
    }
}