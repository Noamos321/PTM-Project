package graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String name; // שם הצומת
    private List<Node> edges; // רשימת הקשרים של הצומת לצמתים אחרים

    // בונה - מאתחל את הצומת עם שם ורשימת קשרים ריקה
    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    // מחזיר את שם הצומת
    public String getName() {
        return name;
    }

    // מחזיר את רשימת הקשרים של הצומת
    public List<Node> getEdges() {
        return edges;
    }

    // מוסיף קשר לצומת אחר אם הקשר לא קיים כבר
    public void addEdge(Node e) {
        if (!edges.contains(e)) {
            edges.add(e);
        }
    }

    // בודק אם יש מחזור בגרף שמתחיל מהצומת הנוכחי
    public boolean hasCycles() {
        return hasCyclesHelper(new ArrayList<>(), new ArrayList<>());
    }

    // שיטה עזר שמבצעת חיפוש עומק (DFS) כדי לזהות מחזורים
    private boolean hasCyclesHelper(List<Node> visited, List<Node> stack) {
        // אם הצומת כבר נמצא בנתיב הנוכחי, זוהה מחזור
        if (stack.contains(this)) {
            return true;
        }

        // אם הצומת כבר ביקרנו בו, אין צורך לבדוק אותו שוב
        if (visited.contains(this)) {
            return false;
        }

        // מסמנים את הצומת כ"נבדק" ומוסיפים אותו לנתיב הנוכחי
        visited.add(this);
        stack.add(this);

        // בודקים את כל הצמתים השכנים
        for (Node neighbor : edges) {
            if (neighbor.hasCyclesHelper(visited, stack)) {
                return true; // זוהה מחזור בצומת שכן
            }
        }

        // מסיימים לעבד את הצומת, מסירים אותו מהנתיב
        stack.remove(this);

        return false; // לא זוהה מחזור
    }

    // השוואה בין צמתים - צמתים נחשבים זהים אם שמותיהם זהים
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // השוואה ישירה
        if (obj == null || getClass() != obj.getClass()) return false; // בדיקה אם אותו סוג אובייקט
        Node other = (Node) obj;
        return name.equals(other.name); // השוואה לפי שם
    }

    // החזרת ערך ייחודי (hash) עבור הצומת על בסיס השם שלו
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
