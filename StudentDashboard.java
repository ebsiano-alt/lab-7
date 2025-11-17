package lab_7;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentDashboard {
    private final Student student;
    private final JsonDatabaseManager db;
    private JFrame frame;

    public StudentDashboard(Student s, JsonDatabaseManager db) {
        this.student = s;
        this.db = db;
    }

    public void show() {
        frame = new JFrame("Student Dashboard - " + student.getUsername());
        frame.setSize(700, 450);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(new BorderLayout());
        JLabel top = new JLabel("Welcome, " + student.getUsername());
        p.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1,2));
        DefaultListModel<Course> allCoursesModel = new DefaultListModel<>();
        JList<Course> allCoursesList = new JList<>(allCoursesModel);
        allCoursesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Course c = (Course) value;
                return super.getListCellRendererComponent(list, c.getTitle() + " - " + c.getDescription(), index, isSelected, cellHasFocus);
            }
        });

        List<Course> all = db.listAllCourses();
        for (Course c : all) allCoursesModel.addElement(c);
        center.add(new JScrollPane(allCoursesList));

        JPanel right = new JPanel(new BorderLayout());
        DefaultListModel<Course> enrolledModel = new DefaultListModel<>();
        JList<Course> enrolledList = new JList<>(enrolledModel);
        enrolledList.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Course c = (Course) value;
                return super.getListCellRendererComponent(list, c.getTitle(), index, isSelected, cellHasFocus);
            }
        });

        for (String cid : student.getEnrolledCourseIds()) {
            Course c = db.findCourseById(cid);
            if (c != null) enrolledModel.addElement(c);
        }

        JButton enrollBtn = new JButton("Enroll in Selected");
        enrollBtn.addActionListener(e -> {
            Course sel = allCoursesList.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(frame, "Select a course first.");
                return;
            }
            student.enrollCourse(sel.getCourseId());
            sel.enrollStudent(student.getUserId());
            db.updateCourse(sel);
            db.updateUser(student);
            enrolledModel.addElement(sel);
            JOptionPane.showMessageDialog(frame, "Enrolled in " + sel.getTitle());
        });

        JButton openCourseBtn = new JButton("Open Selected Enrolled Course");
        openCourseBtn.addActionListener(e -> {
            Course sel = enrolledList.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(frame, "Select an enrolled course.");
                return;
            }
            new LessonViewer(student, sel, db).show();
        });

        right.add(new JScrollPane(enrolledList), BorderLayout.CENTER);
        JPanel bot = new JPanel();
        bot.add(enrollBtn);
        bot.add(openCourseBtn);
        right.add(bot, BorderLayout.SOUTH);

        center.add(right);
        p.add(center, BorderLayout.CENTER);

        frame.setContentPane(p);
        frame.setVisible(true);
    }
}