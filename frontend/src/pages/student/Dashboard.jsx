import { useEffect, useState } from "react";
import CourseCard from "../../components/CourseCard.jsx";
import { getPublicCourses, myEnrollments, enroll } from "../../services/courseService.js";

export default function Dashboard() {
  const [courses, setCourses] = useState([]);
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const [allCourses, myCourses] = await Promise.all([
          getPublicCourses(),
          myEnrollments().catch(() => []),
        ]);
        setCourses(allCourses);
        setEnrollments(myCourses);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const handleEnroll = async (courseId) => {
    await enroll(courseId);
    const updated = await myEnrollments();
    setEnrollments(updated);
  };

  if (loading) return <p>Loading courses…</p>;

  return (
    <div>
      <h2>Explore Courses</h2>
      <div style={styles.grid}>
        {courses.map((course) => (
          <div key={course.id}>
            <CourseCard course={course} />
            <button style={styles.enrollBtn} onClick={() => handleEnroll(course.id)}>
              Enroll
            </button>
          </div>
        ))}
        {courses.length === 0 && <p>No published courses yet.</p>}
      </div>

      <h2 style={{ marginTop: 40 }}>My Enrollments</h2>
      <ul>
        {enrollments.map((e) => (
          <li key={e.id}>
            {e.course?.title} — {e.status}
          </li>
        ))}
        {enrollments.length === 0 && <p>You haven't enrolled in any course yet.</p>}
      </ul>
    </div>
  );
}

const styles = {
  grid: { display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(240px, 1fr))", gap: 16 },
  enrollBtn: { marginTop: 8, padding: "6px 12px", borderRadius: 6, border: "none", background: "#121212", color: "#fff", cursor: "pointer" },
};
