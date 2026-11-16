import { Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "./layouts/MainLayout.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";

import Login from "./pages/auth/Login.jsx";
import Register from "./pages/auth/Register.jsx";

import StudentDashboard from "./pages/student/Dashboard.jsx";
import CourseView from "./pages/student/CourseView.jsx";

import InstructorDashboard from "./pages/instructor/Dashboard.jsx";
import AdminDashboard from "./pages/admin/Dashboard.jsx";
import { useAuth } from "./hooks/useAuth.js";

function DashboardRouter() {
  const { user } = useAuth();
  if (user?.role === "ADMIN") return <AdminDashboard />;
  if (user?.role === "INSTRUCTOR") return <InstructorDashboard />;
  return <StudentDashboard />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route element={<MainLayout />}>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute roles={["STUDENT", "INSTRUCTOR", "ADMIN"]}>
              <DashboardRouter />
            </ProtectedRoute>
          }
        />
        <Route
          path="/courses/:courseId"
          element={
            <ProtectedRoute roles={["STUDENT", "INSTRUCTOR", "ADMIN"]}>
              <CourseView />
            </ProtectedRoute>
          }
        />
        <Route
          path="/instructor"
          element={
            <ProtectedRoute roles={["INSTRUCTOR", "ADMIN"]}>
              <InstructorDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
