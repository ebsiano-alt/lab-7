package lab_7;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LessonViewer {

    private final Student student;
    private final Course course;
    private final JsonDatabaseManager db;
    private JFrame frame;

    public LessonViewer(Student s, Course c, JsonDatabaseManager db) {
        this.student = s;
        this.course = c;
        this.db = db;
    }

    public void show() {
        frame = new JFrame("Course: " + course.getTitle());
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        JPanel p = new JPanel(new BorderLayout());

        DefaultListModel<Lesson> lm = new DefaultListModel<>();
        for (Lesson l : course.getLessons())
            lm.addElement(l);

        JList<Lesson> list = new JList<>(lm);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                Lesson l = (Lesson) value;
                return super.getListCellRendererComponent(
                        list, l.getTitle(), index, isSelected, cellHasFocus);
            }
        });

        JTextArea contentArea = new JTextArea();
        contentArea.setEditable(false) ;

        list.addListSelectionListener(e -> {
            Lesson sel = list.getSelectedValue();
            if (sel != null)
                contentArea.setText(sel.getContent());
        });

        JButton markDone = new JButton("Mark Lesson Completed");
        markDone.addActionListener(e -> {
            Lesson sel = list.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(frame, "Select a lesson first.");
                return;
            }
            student.completeLesson(sel.getLessonId());
            db.updateUser(student);
            JOptionPane.showMessageDialog(frame, "Lesson marked completed!");
        });

        p.add(new JScrollPane(list), BorderLayout.WEST);
        p.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        p.add(markDone, BorderLayout.SOUTH);

        frame.setContentPane(p);
        frame.setVisible(true);
    }
}