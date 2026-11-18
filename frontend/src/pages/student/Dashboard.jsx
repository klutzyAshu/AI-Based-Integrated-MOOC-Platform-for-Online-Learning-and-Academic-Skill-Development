import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import CourseCard from "../../components/CourseCard.jsx";
import { getPublicCourses, myEnrollments, enroll } from "../../services/courseService.js";

export default function Dashboard() {
  const [courses, setCourses] = useState([]);
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState("");
  const [loadError, setLoadError] = useState("");

  useEffect(() => {
    (async () => {
      try {
        const [allCourses, myCourses] = await Promise.all([
          getPublicCourses(),
          myEnrollments().catch(() => []),
        ]);
        setCourses(allCourses);
        setEnrollments(myCourses);
      } catch {
        setLoadError("We couldn’t load the course catalog. Please refresh to try again.");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const handleEnroll = async (courseId) => {
    setActionError("");
    try {
      await enroll(courseId);
      const updated = await myEnrollments();
      setEnrollments(updated);
    } catch (error) {
      setActionError(error.response?.data?.error || "We couldn’t enroll you. Please try again.");
    }
  };

  if (loading) return <p>Loading courses…</p>;

  return (
    <div>
      <div className="page-heading"><div><span className="eyebrow">YOUR LEARNING SPACE</span><h2>Explore courses</h2><p>Find a course and keep building your skills.</p></div></div>
      {loadError && <p className="auth-error" role="alert">{loadError}</p>}
      {actionError && <p className="auth-error" role="alert">{actionError}</p>}
      <div className="course-grid">
        {courses.map((course) => (
          <div className="course-tile" key={course.id}>
            <CourseCard course={course} />
            <button className="button-primary" disabled={enrollments.some((e) => e.course?.id === course.id && e.status !== "DROPPED")} onClick={() => handleEnroll(course.id)}>
              {enrollments.some((e) => e.course?.id === course.id && e.status !== "DROPPED") ? "Enrolled" : "Enroll"}
            </button>
          </div>
        ))}
        {courses.length === 0 && <div className="empty-state">No published courses yet. Check back soon.</div>}
      </div>

      <h2 className="section-title">My enrollments</h2>
      <ul className="enrollment-list">
        {enrollments.map((e) => (
          <li key={e.id}>
            <div className="enrollment-row">
              <div><Link to={`/courses/${e.course?.id}`}>{e.course?.title}</Link><span className="muted">{e.status === "COMPLETED" ? "Completed" : "In progress"}</span></div>
              {e.status === "COMPLETED" && <Link className="text-link" to={`/certificates/${e.course?.id}`}>View certificate ↗</Link>}
            </div>
          </li>
        ))}
        {enrollments.length === 0 && <li className="muted">You haven’t enrolled in a course yet.</li>}
      </ul>
    </div>
  );
}

