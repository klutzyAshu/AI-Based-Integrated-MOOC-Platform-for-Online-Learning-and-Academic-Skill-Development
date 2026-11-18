import api from "./api.js";

export async function getPublicCourses() {
  const { data } = await api.get("/courses/public");
  return data;
}

export async function searchCourses(query) {
  const { data } = await api.get("/courses/public/search", { params: { q: query } });
  return data;
}

export async function getCourse(id) {
  const { data } = await api.get(`/courses/${id}`);
  return data;
}

export async function createCourse(payload) {
  const { data } = await api.post("/courses", payload);
  return data;
}

export async function updateCourse(id, payload) {
  const { data } = await api.put(`/courses/${id}`, payload);
  return data;
}

export async function publishCourse(id) {
  const { data } = await api.patch(`/courses/${id}/publish`);
  return data;
}

export async function enroll(courseId) {
  const { data } = await api.post(`/enrollments/${courseId}`);
  return data;
}

export async function myEnrollments() {
  const { data } = await api.get("/enrollments/me");
  return data;
}

export async function getLessonQuizzes(lessonId) {
  const { data } = await api.get(`/quizzes/lesson/${lessonId}`);
  return data;
}

export async function submitQuiz(quizId, answers) {
  const { data } = await api.post(`/quizzes/${quizId}/submit`, answers);
  return data;
}

export async function createModule(courseId, payload) {
  const { data } = await api.post(`/courses/${courseId}/modules`, payload);
  return data;
}

export async function createLesson(moduleId, payload) {
  const { data } = await api.post("/lessons", null, { params: { moduleId, ...payload } });
  return data;
}

export async function getCourseProgress(courseId) {
  const { data } = await api.get(`/progress/courses/${courseId}`);
  return data;
}

export async function completeLesson(courseId, lessonId) {
  const { data } = await api.post(`/progress/courses/${courseId}/lessons/${lessonId}/complete`);
  return data;
}

export async function recordLessonVisit(courseId, lessonId) {
  const { data } = await api.post(`/progress/courses/${courseId}/lessons/${lessonId}/visit`);
  return data;
}

export async function getCertificate(courseId) {
  const { data } = await api.get(`/certificates/me/courses/${courseId}`);
  return data;
}
