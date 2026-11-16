import { useEffect, useState } from "react";
import { useAuth } from "../../hooks/useAuth.js";
import api from "../../services/api.js";
import { createCourse, publishCourse } from "../../services/courseService.js";

export default function Dashboard() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [error, setError] = useState("");

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
    setError("");
    try {
      await createCourse({ title, description, level: "BEGINNER" });
      setTitle("");
      setDescription("");
      await loadCourses();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "We couldn’t create the course. Please try again.");
    }
  };

  const handlePublish = async (id) => {
    try {
      await publishCourse(id);
      await loadCourses();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "We couldn’t publish the course. Please try again.");
    }
  };

  return (
    <div>
      <div className="page-heading"><div><span className="eyebrow">INSTRUCTOR SPACE</span><h2>Manage your courses</h2><p>Create a course and share it with learners.</p></div></div>

      {error && <p className="auth-error" role="alert">{error}</p>}
      <form onSubmit={handleCreate} className="form-row">
        <input
          className="input"
          placeholder="Course title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <input
          className="input"
          placeholder="Short description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button className="button-primary" type="submit">Create course</button>
      </form>

      <h3 className="section-title">My courses</h3>
      <div className="table-wrap"><table>
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
                  <button className="button-primary" onClick={() => handlePublish(c.id)}>
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
      </table></div>
    </div>
  );
}

