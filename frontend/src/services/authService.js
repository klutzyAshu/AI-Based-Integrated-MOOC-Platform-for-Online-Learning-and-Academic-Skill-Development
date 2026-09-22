import api from "./api.js";

export async function login(email, password) {
  const { data } = await api.post("/auth/login", { email, password });
  localStorage.setItem("mooc_token", data.token);
  localStorage.setItem("mooc_user", JSON.stringify(data));
  return data;
}

export async function register(name, email, password, role) {
  const { data } = await api.post("/auth/register", { name, email, password, role });
  localStorage.setItem("mooc_token", data.token);
  localStorage.setItem("mooc_user", JSON.stringify(data));
  return data;
}

export function logout() {
  localStorage.removeItem("mooc_token");
  localStorage.removeItem("mooc_user");
}

export function getCurrentUser() {
  const raw = localStorage.getItem("mooc_user");
  return raw ? JSON.parse(raw) : null;
}
