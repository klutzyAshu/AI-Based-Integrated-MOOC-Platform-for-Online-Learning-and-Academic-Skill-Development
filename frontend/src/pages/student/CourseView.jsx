import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getCourse } from "../../services/courseService.js";
import { askChatbot } from "../../services/aiService.js";

export default function CourseView() {
  const { courseId } = useParams();
  const [course, setCourse] = useState(null);
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [asking, setAsking] = useState(false);
  const [loadError, setLoadError] = useState("");
  const [askError, setAskError] = useState("");

  useEffect(() => {
    setCourse(null);
    setLoadError("");
    getCourse(courseId).then(setCourse).catch(() => setLoadError("We couldn’t load this course. Please try again."));
  }, [courseId]);

  const handleAsk = async (e) => {
    e.preventDefault();
    if (!question.trim()) return;
    setAsking(true);
    setAskError("");
    try {
      const res = await askChatbot(question, { courseId });
      setAnswer(res.reply);
    } catch {
      setAskError("The assistant couldn’t respond just now. Please try again.");
    } finally {
      setAsking(false);
    }
  };

  if (!course) return <p className="muted">{loadError || "Loading course…"}</p>;

  return (
    <div>
      <span className="eyebrow">{course.level || "COURSE"}</span>
      <h2>{course.title}</h2>
      <p className="muted">{course.description}</p>

      <h3>Modules</h3>
      <ul className="course-outline">
        {(course.modules || []).map((m) => (
          <li key={m.id}>
            <strong>{m.title}</strong>
            <ul>
              {(m.lessons || []).map((l) => (
                <li key={l.id}>{l.title}</li>
              ))}
            </ul>
          </li>
        ))}
        {(!course.modules || course.modules.length === 0) && <p>No modules published yet.</p>}
      </ul>

      <div className="ai-panel">
        <h3>Ask the AI Learning Assistant</h3>
        <form onSubmit={handleAsk}>
          <input
            placeholder="Ask a question about this course…"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
          />
          <button disabled={asking} type="submit">
            {asking ? "Thinking…" : "Ask"}
          </button>
        </form>
        {askError && <p className="auth-error" role="alert">{askError}</p>}
        {answer && <p className="ai-answer">{answer}</p>}
      </div>
    </div>
  );
}

