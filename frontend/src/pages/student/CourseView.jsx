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

  useEffect(() => {
    getCourse(courseId).then(setCourse);
  }, [courseId]);

  const handleAsk = async (e) => {
    e.preventDefault();
    setAsking(true);
    try {
      const res = await askChatbot(question, { courseId });
      setAnswer(res.reply);
    } finally {
      setAsking(false);
    }
  };

  if (!course) return <p>Loading course…</p>;

  return (
    <div>
      <h2>{course.title}</h2>
      <p>{course.description}</p>

      <h3>Modules</h3>
      <ul>
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

      <div style={styles.aiBox}>
        <h3>Ask the AI Learning Assistant</h3>
        <form onSubmit={handleAsk} style={styles.form}>
          <input
            style={styles.input}
            placeholder="Ask a question about this course…"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
          />
          <button style={styles.button} disabled={asking} type="submit">
            {asking ? "Thinking…" : "Ask"}
          </button>
        </form>
        {answer && <p style={styles.answer}>{answer}</p>}
      </div>
    </div>
  );
}

const styles = {
  aiBox: { marginTop: 32, padding: 16, border: "1px solid #ddd", borderRadius: 10, background: "#fafafa" },
  form: { display: "flex", gap: 8 },
  input: { flex: 1, padding: 10, borderRadius: 6, border: "1px solid #ccc" },
  button: { padding: "10px 16px", borderRadius: 6, border: "none", background: "#121212", color: "#fff", cursor: "pointer" },
  answer: { marginTop: 12, whiteSpace: "pre-wrap" },
};
