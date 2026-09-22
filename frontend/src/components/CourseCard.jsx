import { Link } from "react-router-dom";

export default function CourseCard({ course }) {
  return (
    <div style={styles.card}>
      <h3 style={styles.title}>{course.title}</h3>
      <p style={styles.level}>{course.level}</p>
      <p style={styles.desc}>{course.description}</p>
      <Link to={`/courses/${course.id}`} style={styles.link}>
        View Course →
      </Link>
    </div>
  );
}

const styles = {
  card: {
    border: "1px solid #e0e0e0",
    borderRadius: 10,
    padding: 16,
    background: "#fafafa",
  },
  title: { margin: "0 0 4px 0" },
  level: { fontSize: 12, color: "#666", textTransform: "uppercase", margin: "0 0 8px 0" },
  desc: { fontSize: 14, color: "#333" },
  link: { fontSize: 14, color: "#121212", fontWeight: "bold", textDecoration: "none" },
};
