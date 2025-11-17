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