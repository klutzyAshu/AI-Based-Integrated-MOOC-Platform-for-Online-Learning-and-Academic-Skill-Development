import { Link } from "react-router-dom";

export default function CourseCard({ course }) {
  return (
    <div className="course-card">
      <div className="course-card-top"><span className="course-icon">✦</span><span className="eyebrow">{course.level || "ALL LEVELS"}</span></div>
      <h3>{course.title}</h3>
      <p className="muted course-description">{course.description || "Build practical knowledge with this guided course."}</p>
      <Link to={`/courses/${course.id}`} className="text-link">
        Explore course <span aria-hidden="true">↗</span>
      </Link>
    </div>
  );
}
