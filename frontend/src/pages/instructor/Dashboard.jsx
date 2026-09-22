import { useEffect, useState } from "react";
import { useAuth } from "../../hooks/useAuth.js";
import api from "../../services/api.js";
import { createCourse, publishCourse } from "../../services/courseService.js";

export default function Dashboard() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  const loadCourses = async () => {
    const { data } = await api.get(`/courses/instructor/${user.userId}`);
    setCourses(data);
  };

  useEffect(() => {
    loadCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    await createCourse({ title, description, level: "BEGINNER" });
    setTitle("");
    setDescription("");
    loadCourses();
  };

  const handlePublish = async (id) => {
    await publishCourse(id);
    loadCourses();
  };

  return (
    <div>
      <h2>Instructor Dashboard</h2>

      <form onSubmit={handleCreate} style={styles.form}>
        <input
          style={styles.input}
          placeholder="Course title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <input
          style={styles.input}
          placeholder="Short description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button style={styles.button} type="submit">Create Course</button>
      </form>

      <h3 style={{ marginTop: 32 }}>My Courses</h3>
      <table style={styles.table}>
        <thead>
          <tr>
            <th align="left">Title</th>
            <th align="left">Level</th>
            <th align="left">Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {courses.map((c) => (
            <tr key={c.id}>
              <td>{c.title}</td>
              <td>{c.level}</td>
              <td>{c.published ? "Published" : "Draft"}</td>
              <td>
                {!c.published && (
                  <button style={styles.smallButton} onClick={() => handlePublish(c.id)}>
                    Publish
                  </button>
                )}
              </td>
            </tr>
          ))}
          {courses.length === 0 && (
            <tr>
              <td colSpan={4}>No courses yet — create one above.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

const styles = {
  form: { display: "flex", gap: 8, marginTop: 16 },
  input: { flex: 1, padding: 10, borderRadius: 6, border: "1px solid #ccc" },
  button: { padding: "10px 16px", borderRadius: 6, border: "none", background: "#121212", color: "#fff", cursor: "pointer" },
  smallButton: { padding: "4px 10px", borderRadius: 6, border: "none", background: "#121212", color: "#fff", cursor: "pointer" },
  table: { width: "100%", borderCollapse: "collapse", marginTop: 12 },
};
