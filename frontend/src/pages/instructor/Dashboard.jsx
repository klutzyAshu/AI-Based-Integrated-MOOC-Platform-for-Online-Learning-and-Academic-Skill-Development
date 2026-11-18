import { useEffect, useState } from "react";
import { useAuth } from "../../hooks/useAuth.js";
import api from "../../services/api.js";
import { createCourse, publishCourse, updateCourse } from "../../services/courseService.js";

export default function Dashboard() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [metrics, setMetrics] = useState([]);
  const [selectedCourseId, setSelectedCourseId] = useState("");
  const [moduleTitle, setModuleTitle] = useState("");
  const [moduleOrder, setModuleOrder] = useState(0);
  const [selectedModuleId, setSelectedModuleId] = useState("");
  const [lessonTitle, setLessonTitle] = useState("");
  const [lessonContent, setLessonContent] = useState("");
  const [lessonVideo, setLessonVideo] = useState("");
  const [authoring, setAuthoring] = useState(false);
  const [categories, setCategories] = useState([]);
  const [categoryId, setCategoryId] = useState("");
  const [editingCourse, setEditingCourse] = useState(null);

  const loadCourses = async () => {
    try {
      const { data } = await api.get(`/courses/instructor/${user.userId}`);
      setCourses(data);
      setSelectedCourseId((current) => current || String(data[0]?.id || ""));
      setError("");
    } catch {
      setError("We couldn’t load your courses. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const loadMetrics = async () => {
    try {
      const { data } = await api.get("/analytics/my-courses");
      setMetrics(data);
    } catch {
      // Course management remains usable when analytics are unavailable.
    }
  };

  const loadCategories = async () => {
    try {
      const { data } = await api.get("/categories");
      setCategories(data);
    } catch {
      setCategories([]);
    }
  };

  useEffect(() => {
    loadCourses();
    loadMetrics();
    loadCategories();
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError("");
    setSaving(true);
    try {
      await createCourse({ title, description, level: "BEGINNER", categoryId: categoryId ? Number(categoryId) : null });
      setTitle("");
      setDescription("");
      setCategoryId("");
      await loadCourses();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "We couldn’t create the course. Please try again.");
    } finally {
      setSaving(false);
    }
  };

  const handlePublish = async (id) => {
    try {
      await publishCourse(id);
      await loadCourses();
      await loadMetrics();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "We couldn’t publish the course. Please try again.");
    }
  };

  const handleUpdateCourse = async (event) => {
    event.preventDefault();
    if (!editingCourse) return;
    setAuthoring(true);
    setError("");
    try {
      await updateCourse(editingCourse.id, {
        title: editingCourse.title,
        description: editingCourse.description,
        level: editingCourse.level,
        categoryId: editingCourse.categoryId ? Number(editingCourse.categoryId) : null,
      });
      setEditingCourse(null);
      await loadCourses();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "We couldn’t update this course.");
    } finally {
      setAuthoring(false);
    }
  };

  const selectedCourse = courses.find((course) => String(course.id) === String(selectedCourseId));
  const handleCreateModule = async (event) => {
    event.preventDefault();
    if (!selectedCourseId || !moduleTitle.trim()) return;
    setAuthoring(true);
    setError("");
    try {
      const { data } = await api.post(`/courses/${selectedCourseId}/modules`, { title: moduleTitle, order: Number(moduleOrder) });
      setModuleTitle("");
      setModuleOrder((selectedCourse?.modules?.length || 0) + 1);
      setSelectedModuleId(String(data.id));
      await loadCourses();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "We couldn’t add this module.");
    } finally {
      setAuthoring(false);
    }
  };

  const handleCreateLesson = async (event) => {
    event.preventDefault();
    if (!selectedModuleId || !lessonTitle.trim()) return;
    setAuthoring(true);
    setError("");
    try {
      const selectedModule = selectedCourse?.modules?.find((module) => String(module.id) === String(selectedModuleId));
      await api.post("/lessons", { moduleId: Number(selectedModuleId), title: lessonTitle, content: lessonContent, videoUrl: lessonVideo, order: selectedModule?.lessons?.length || 0 });
      setLessonTitle("");
      setLessonContent("");
      setLessonVideo("");
      await loadCourses();
      await loadMetrics();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "We couldn’t add this lesson.");
    } finally {
      setAuthoring(false);
    }
  };

  return (
    <div>
      <div className="page-heading"><div><span className="eyebrow">INSTRUCTOR SPACE</span><h2>Manage your courses</h2><p>Create a course and share it with learners.</p></div></div>

      {error && <p className="auth-error" role="alert">{error}</p>}
      <div className="stats-grid instructor-stats">
        {[["Courses", metrics.length], ["Learners", metrics.reduce((sum, metric) => sum + metric.enrollments, 0)], ["Completions", metrics.reduce((sum, metric) => sum + metric.completions, 0)]].map(([label, value]) => <div className="stat-card" key={label}><span>{label}</span><strong>{value}</strong></div>)}
      </div>
      <form onSubmit={handleCreate} className="form-row">
        <input
          className="input"
          placeholder="Course title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        {categories.length > 0 && <select aria-label="Course category" value={categoryId} onChange={(e) => setCategoryId(e.target.value)}>
          <option value="">Choose a category</option>
          {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
        </select>}
        <input
          className="input"
          placeholder="Short description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button className="button-primary" type="submit" disabled={saving}>{saving ? "Creating…" : "Create course"}</button>
      </form>

      <h3 className="section-title">My courses</h3>
      {loading ? <p className="muted">Loading your courses…</p> : <div className="table-wrap"><table>
        <thead>
          <tr>
            <th align="left">Title</th>
            <th align="left">Level</th>
            <th align="left">Category</th>
            <th align="left">Learners</th>
            <th align="left">Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {courses.map((c) => (
            <tr key={c.id}>
              <td>{c.title}</td>
              <td>{c.level}</td>
              <td>{c.category?.name || "—"}</td>
              <td>{metrics.find((metric) => metric.courseId === c.id)?.enrollments ?? 0}</td>
              <td>{c.published ? "Published" : "Draft"}</td>
              <td>
                <button className="button" type="button" onClick={() => setEditingCourse({
                  id: c.id, title: c.title, description: c.description || "", level: c.level || "BEGINNER",
                  categoryId: c.category?.id ? String(c.category.id) : "",
                })}>Edit</button>
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
              <td colSpan={6}>No courses yet — create one above.</td>
            </tr>
          )}
        </tbody>
      </table></div>}
      {courses.length > 0 && <section className="authoring-panel">
        <div><span className="eyebrow">COURSE BUILDER</span><h3>Add modules and lessons</h3><p className="muted">Build a clear course outline, then add lesson content and optional video links.</p></div>
        <label className="field-label">Course<select value={selectedCourseId} onChange={(event) => { setSelectedCourseId(event.target.value); setSelectedModuleId(""); }}>
          {courses.map((course) => <option value={course.id} key={course.id}>{course.title}</option>)}
        </select></label>
        <form className="authoring-form" onSubmit={handleCreateModule}>
          <h4>New module</h4>
          <input aria-label="Module title" placeholder="Module title" value={moduleTitle} onChange={(event) => setModuleTitle(event.target.value)} required />
          <input aria-label="Module order" type="number" min="0" value={moduleOrder} onChange={(event) => setModuleOrder(event.target.value)} />
          <button className="button-primary" disabled={authoring}>{authoring ? "Saving…" : "Add module"}</button>
        </form>
        {selectedCourse?.modules?.length > 0 && <label className="field-label">Module for new lesson<select value={selectedModuleId} onChange={(event) => setSelectedModuleId(event.target.value)}>
          <option value="">Choose a module</option>
          {selectedCourse.modules.map((module) => <option value={module.id} key={module.id}>{module.title}</option>)}
        </select></label>}
        {selectedModuleId && <form className="authoring-form lesson-authoring-form" onSubmit={handleCreateLesson}>
          <h4>New lesson</h4>
          <input aria-label="Lesson title" placeholder="Lesson title" value={lessonTitle} onChange={(event) => setLessonTitle(event.target.value)} required />
          <textarea aria-label="Lesson content" placeholder="Lesson content" rows="6" value={lessonContent} onChange={(event) => setLessonContent(event.target.value)} />
          <input aria-label="Video URL" type="url" placeholder="Optional video URL" value={lessonVideo} onChange={(event) => setLessonVideo(event.target.value)} />
          <button className="button-primary" disabled={authoring}>{authoring ? "Saving…" : "Add lesson"}</button>
        </form>}
        {selectedCourse?.modules?.length > 0 && <div className="outline-editor">
          <h4>Course outline</h4>
          {selectedCourse.modules.map((module) => <section className="outline-module" key={module.id}>
            <form onSubmit={async (event) => {
              event.preventDefault();
              const fields = new FormData(event.currentTarget);
              try {
                await api.put(`/modules/${module.id}`, { title: fields.get("title"), order: Number(fields.get("order")) });
                await loadCourses();
              } catch { setError("We couldn’t update this module."); }
            }}>
              <input name="title" aria-label={`Module ${module.title} title`} defaultValue={module.title} required />
              <input name="order" aria-label={`Module ${module.title} order`} type="number" min="0" defaultValue={module.order || 0} />
              <button className="button-primary" type="submit">Save module</button>
              <button className="button-danger" type="button" onClick={async () => {
                if (!window.confirm(`Delete “${module.title}” and all its lessons?`)) return;
                try { await api.delete(`/modules/${module.id}`); if (String(selectedModuleId) === String(module.id)) setSelectedModuleId(""); await loadCourses(); }
                catch { setError("We couldn’t delete this module."); }
              }}>Delete</button>
            </form>
            {(module.lessons || []).map((lesson) => <form className="outline-lesson" key={lesson.id} onSubmit={async (event) => {
              event.preventDefault();
              const fields = new FormData(event.currentTarget);
              try {
                await api.put(`/lessons/${lesson.id}`, { moduleId: module.id, title: fields.get("title"), content: fields.get("content"), videoUrl: fields.get("videoUrl") });
                await loadCourses();
              } catch { setError("We couldn’t update this lesson."); }
            }}>
              <input name="title" aria-label={`Lesson ${lesson.title} title`} defaultValue={lesson.title} required />
              <textarea name="content" aria-label={`Lesson ${lesson.title} content`} defaultValue={lesson.content || ""} rows="3" />
              <input name="videoUrl" aria-label={`Lesson ${lesson.title} video URL`} type="url" placeholder="Video URL" defaultValue={lesson.videoUrl || ""} />
              <button className="button-primary" type="submit">Save lesson</button>
              <button className="button-danger" type="button" onClick={async () => {
                if (!window.confirm(`Delete the lesson “${lesson.title}”?`)) return;
                try { await api.delete(`/lessons/${lesson.id}`); await loadCourses(); }
                catch { setError("We couldn’t delete this lesson."); }
              }}>Delete lesson</button>
            </form>)}
          </section>)}
        </div>}
      </section>}
      {editingCourse && <form className="course-edit-form" onSubmit={handleUpdateCourse}>
        <div><span className="eyebrow">COURSE DETAILS</span><h3>Edit course</h3></div>
        <input aria-label="Course title" value={editingCourse.title} onChange={(event) => setEditingCourse({ ...editingCourse, title: event.target.value })} required />
        <textarea aria-label="Course description" rows="3" value={editingCourse.description} onChange={(event) => setEditingCourse({ ...editingCourse, description: event.target.value })} />
        <select aria-label="Course level" value={editingCourse.level} onChange={(event) => setEditingCourse({ ...editingCourse, level: event.target.value })}>
          <option value="BEGINNER">Beginner</option><option value="INTERMEDIATE">Intermediate</option><option value="ADVANCED">Advanced</option>
        </select>
        <select aria-label="Course category" value={editingCourse.categoryId} onChange={(event) => setEditingCourse({ ...editingCourse, categoryId: event.target.value })}>
          <option value="">No category</option>{categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
        </select>
        <div className="form-row"><button className="button-primary" disabled={authoring}>{authoring ? "Saving…" : "Save changes"}</button><button className="button" type="button" onClick={() => setEditingCourse(null)}>Cancel</button></div>
      </form>}
    </div>
  );
}

