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
