package FrontEnd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InstructorDashboard {
    private final Instructor instructor;
    private final JsonDatabaseManager db;
    private JFrame frame;

    public InstructorDashboard(Instructor inst, JsonDatabaseManager db) {
        this.instructor = inst;
        this.db = db;
    }

    public void show() {
        frame = new JFrame("Instructor Dashboard - " + instructor.getUsername());
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(new BorderLayout());
        JLabel top = new JLabel("Welcome, " + instructor.getUsername());
        p.add(top, BorderLayout.NORTH);

        DefaultListModel<Course> model = new DefaultListModel<>();
        JList<Course> list = new JList<>(model);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Course c = (Course) value;
                return super.getListCellRendererComponent(l, c.getTitle(), index, isSelected, cellHasFocus);
            }
        });

        for (Course c : db.listAllCourses()) {
            if (instructor.getUserId().equals(c.getInstructorId())) model.addElement(c);
        }

        p.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton createBtn = new JButton("Create Course");
        JButton editBtn = new JButton("Edit Selected");
        JButton viewStudentsBtn = new JButton("View Enrolled Students");
        actions.add(createBtn);
        actions.add(editBtn);
        actions.add(viewStudentsBtn);
        p.add(actions, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(frame, "Course title:");
            if (title == null || title.isBlank()) return;
            String desc = JOptionPane.showInputDialog(frame, "Course description:");
            Course c = new Course(title, desc == null ? "" : desc, instructor.getUserId());
            db.addCourse(c);
            instructor.addCourse(c.getCourseId());
            db.updateUser(instructor);
            model.addElement(c);
            JOptionPane.showMessageDialog(frame, "Course created.");
        });

        editBtn.addActionListener(e -> {
            Course sel = list.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(frame, "Select a course.");
                return;
            }
            String[] options = {"Add Lesson", "Remove Lesson", "Rename Course"};
            int op = JOptionPane.showOptionDialog(frame, "Choose action", "Edit", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (op == 0) {
                String lt = JOptionPane.showInputDialog(frame, "Lesson title:");
                if (lt == null || lt.isBlank()) return;
                String lc = JOptionPane.showInputDialog(frame, "Lesson content:");
                Lesson l = new Lesson(lt, lc == null ? "" : lc);
              
                db.addOrUpdateLesson(l);
                sel.addLesson(l);
                db.updateCourse(sel);
                JOptionPane.showMessageDialog(frame, "Lesson added.");
            } else if (op == 1) {
                StringBuilder listStr = new StringBuilder();
                for (int i=0;i<sel.getLessons().size();i++) {
                    listStr.append(i).append(": ").append(sel.getLessons().get(i).getTitle()).append("\n");
                }
                String idxS = JOptionPane.showInputDialog(frame, "Lessons:\n" + listStr + "\nEnter index to remove:");
                try {
                    int idx = Integer.parseInt(idxS);
                    if (idx >= 0 && idx < sel.getLessons().size()) {
                        
                        sel.getLessons().remove(idx);
                        db.updateCourse(sel);
                        JOptionPane.showMessageDialog(frame, "Lesson removed from course.");
                    }
                } catch (Exception ex) {  }
            } else if (op == 2) {
                String nt = JOptionPane.showInputDialog(frame, "New title:", sel.getTitle());
                if (nt != null && !nt.isBlank()) {
                    sel.title = nt; 
                    db.updateCourse(sel);
                    JOptionPane.showMessageDialog(frame, "Course renamed.");
                }
            }
        });

        viewStudentsBtn.addActionListener(e -> {
            Course sel = list.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(frame, "Select a course.");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String sid : sel.getStudentIds()) {
                User u = db.findUserById(sid);
                if (u != null) sb.append(u.getUsername()).append(" (").append(u.getEmail()).append(")\n");
                else sb.append(sid).append("\n");
            }
            if (sb.length() == 0) sb.append("No students enrolled.");
            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);
            JOptionPane.showMessageDialog(frame, new JScrollPane(ta), "Students", JOptionPane.INFORMATION_MESSAGE);
        });

        frame.setContentPane(p);
        frame.setVisible(true);
    }
}
