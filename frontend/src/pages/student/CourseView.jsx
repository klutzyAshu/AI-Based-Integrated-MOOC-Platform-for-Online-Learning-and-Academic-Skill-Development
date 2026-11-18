import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { Link } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth.js";
import { getCourse, getLessonQuizzes, submitQuiz } from "../../services/courseService.js";
import { completeLesson, getCourseProgress, recordLessonVisit } from "../../services/courseService.js";
import { askChatbot } from "../../services/aiService.js";

function readOptions(raw) {
  try {
    const parsed = JSON.parse(raw || "[]");
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

export default function CourseView() {
  const { courseId } = useParams();
  const { user } = useAuth();
  const [course, setCourse] = useState(null);
  const [selectedLesson, setSelectedLesson] = useState(null);
  const [quizzes, setQuizzes] = useState([]);
  const [answers, setAnswers] = useState({});
  const [quizResult, setQuizResult] = useState(null);
  const [quizError, setQuizError] = useState("");
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [asking, setAsking] = useState(false);
  const [loadError, setLoadError] = useState("");
  const [askError, setAskError] = useState("");
  const [progress, setProgress] = useState(null);
  const [progressSaving, setProgressSaving] = useState(false);
  const [progressError, setProgressError] = useState("");

  useEffect(() => {
    let active = true;
    setCourse(null);
    setSelectedLesson(null);
    setLoadError("");
    getCourse(courseId)
      .then((data) => { if (active) setCourse(data); })
      .catch(() => { if (active) setLoadError("We couldn’t load this course. Please try again."); });
    return () => { active = false; };
  }, [courseId]);

  useEffect(() => {
    let active = true;
    setProgress(null);
    if (user?.role !== "STUDENT") return () => { active = false; };
    getCourseProgress(courseId)
      .then((data) => {
        if (!active) return;
        setProgress(data);
        const resumeLesson = (course?.modules || []).flatMap((module) => module.lessons || [])
          .find((lesson) => lesson.id === data.lastLessonId);
        if (resumeLesson) setSelectedLesson((current) => current || resumeLesson);
      })
      .catch(() => { if (active) setProgress(null); });
    return () => { active = false; };
  }, [courseId, user?.role, course]);

  useEffect(() => {
    if (!selectedLesson) {
      setQuizzes([]);
      return undefined;
    }
    let active = true;
    setQuizError("");
    setQuizResult(null);
    setAnswers({});
    getLessonQuizzes(selectedLesson.id)
      .then((data) => { if (active) setQuizzes(data); })
      .catch(() => { if (active) { setQuizzes([]); setQuizError("Quizzes couldn’t be loaded right now."); } });
    return () => { active = false; };
  }, [selectedLesson]);

  const lessons = useMemo(
    () => (course?.modules || []).flatMap((module) => module.lessons || []),
    [course],
  );

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

  const handleSubmitQuiz = async (event, quiz) => {
    event.preventDefault();
    const selections = quiz.questions.map((item) => answers[item.id]);
    if (selections.some((selection) => selection === undefined)) {
      setQuizError("Choose an answer for every question before submitting.");
      return;
    }
    setQuizError("");
    setQuizResult(null);
    try {
      const result = await submitQuiz(quiz.id, selections);
      setQuizResult(result);
    } catch (error) {
      setQuizError(error.response?.data?.message || "Your answers couldn’t be submitted. Please try again.");
    }
  };

  const handleCompleteLesson = async () => {
    if (!selectedLesson) return;
    setProgressSaving(true);
    setProgressError("");
    try {
      setProgress(await completeLesson(courseId, selectedLesson.id));
    } catch {
      setProgressError("We couldn’t save your progress. Please try again.");
    } finally {
      setProgressSaving(false);
    }
  };

  const handleSelectLesson = (lesson) => {
    setSelectedLesson(lesson);
    if (progress) {
      recordLessonVisit(courseId, lesson.id).then(setProgress).catch(() => {});
    }
  };

  if (!course) return <p className="muted" role={loadError ? "alert" : undefined}>{loadError || "Loading course…"}</p>;

  return (
    <div className="course-page">
      <header className="course-hero">
        <span className="eyebrow">{course.level || "COURSE"}</span>
        <h1>{course.title}</h1>
        <p>{course.description || "A guided course to help you build practical skills."}</p>
        <span className="course-hero-meta">{lessons.length} {lessons.length === 1 ? "lesson" : "lessons"}</span>
        {progress && <div className="progress-summary">
          <div className="progress-label"><span>Your progress</span><strong>{Math.round(progress.completionPercent)}%</strong></div>
          <div className="progress-track"><span style={{ width: `${progress.completionPercent}%` }} /></div>
          {progress.certificateCode && <Link className="text-link" to={`/certificates/${courseId}`}>View your certificate ↗</Link>}
        </div>}
      </header>

      <div className="learning-layout">
        <aside className="learning-sidebar" aria-label="Course lessons">
          <h2>Course outline</h2>
          {(course.modules || []).map((module) => (
            <section className="module-group" key={module.id}>
              <h3>{module.title}</h3>
              {(module.lessons || []).map((lesson, index) => (
                <button
                  className={`lesson-link${selectedLesson?.id === lesson.id ? " is-active" : ""}`}
                  key={lesson.id}
                  type="button"
                  onClick={() => handleSelectLesson(lesson)}
                >
                  <span className="lesson-index">{index + 1}</span>
                  <span>{lesson.title}</span>
                </button>
              ))}
            </section>
          ))}
          {lessons.length === 0 && <p className="muted">No lessons are available yet.</p>}
        </aside>

        <main className="lesson-content">
          {selectedLesson ? (
            <article>
              <span className="eyebrow">LESSON</span>
              <h2>{selectedLesson.title}</h2>
              {selectedLesson.durationMinutes && <p className="muted">About {selectedLesson.durationMinutes} minutes</p>}
              {selectedLesson.videoUrl && (
                <p><a className="lesson-video-link" href={selectedLesson.videoUrl} target="_blank" rel="noreferrer">Open lesson video ↗</a></p>
              )}
              {selectedLesson.content
                ? <div className="lesson-copy">{selectedLesson.content}</div>
                : <p className="muted">There’s no written lesson content yet.</p>}
              {progress && <div className="lesson-completion">
                <button className="button-primary" type="button" disabled={progressSaving || progress.completedLessonIds.includes(selectedLesson.id)} onClick={handleCompleteLesson}>
                  {progress.completedLessonIds.includes(selectedLesson.id) ? "Lesson completed" : progressSaving ? "Saving…" : "Mark lesson complete"}
                </button>
                {progressError && <span className="auth-error" role="alert">{progressError}</span>}
              </div>}

              <section className="quiz-section">
                <h3>Knowledge check</h3>
                {quizError && <p className="auth-error" role="alert">{quizError}</p>}
                {quizzes.length === 0 && !quizError && <p className="muted">No quiz for this lesson yet.</p>}
                {quizzes.map((quiz) => (
                  <form className="quiz-card" key={quiz.id} onSubmit={(event) => handleSubmitQuiz(event, quiz)}>
                    <h4>{quiz.title}</h4>
                    {(quiz.questions || []).map((item, questionIndex) => {
                      const options = readOptions(item.optionsJson);
                      return (
                        <fieldset className="quiz-question" key={item.id}>
                          <legend>{questionIndex + 1}. {item.text}</legend>
                          {options.map((option, optionIndex) => (
                            <label className="quiz-option" key={`${item.id}-${optionIndex}`}>
                              <input
                                type="radio"
                                name={`question-${item.id}`}
                                checked={answers[item.id] === optionIndex}
                                onChange={() => setAnswers((current) => ({ ...current, [item.id]: optionIndex }))}
                              />
                              <span>{option}</span>
                            </label>
                          ))}
                        </fieldset>
                      );
                    })}
                    <button className="button-primary" type="submit">Submit answers</button>
                    {quizResult && (
                      <p className={quizResult.passed ? "quiz-result is-passed" : "quiz-result"} role="status">
                        Score: {Math.round(quizResult.score)}%. {quizResult.passed ? "You passed." : `You need ${quizResult.passingScore}% to pass.`}
                      </p>
                    )}
                  </form>
                ))}
              </section>
            </article>
          ) : (
            <div className="lesson-welcome">
              <span className="eyebrow">READY WHEN YOU ARE</span>
              <h2>Choose a lesson to begin.</h2>
              <p className="muted">Your course materials and knowledge checks will appear here.</p>
            </div>
          )}
        </main>
      </div>

      <section className="ai-panel">
        <span className="eyebrow">PERSONAL LEARNING ASSISTANT</span>
        <h2>Ask a question about this course</h2>
        <form onSubmit={handleAsk}>
          <input aria-label="Question for the AI assistant" placeholder="What would you like to understand?" value={question} onChange={(e) => setQuestion(e.target.value)} />
          <button disabled={asking || !question.trim()} type="submit">{asking ? "Thinking…" : "Ask assistant"}</button>
        </form>
        {askError && <p className="auth-error" role="alert">{askError}</p>}
        {answer && <p className="ai-answer" role="status">{answer}</p>}
      </section>
    </div>
  );
}
