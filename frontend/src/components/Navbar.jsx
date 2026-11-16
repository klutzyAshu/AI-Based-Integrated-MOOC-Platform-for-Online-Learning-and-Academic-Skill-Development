import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="topbar">
      <Link to="/dashboard" className="brand">
        <span className="brand-mark">L</span> Learnspace
      </Link>
      <div className="nav-links">
        <Link to="/dashboard" className="nav-link">Dashboard</Link>
        {user && (user.role === "INSTRUCTOR" || user.role === "ADMIN") && (
          <Link to="/instructor" className="nav-link">Instructor</Link>
        )}
        {user && user.role === "ADMIN" && (
          <Link to="/admin" className="nav-link">Admin</Link>
        )}
        {user ? (
          <button onClick={handleLogout} className="button button-quiet">
            {user.name} <span aria-hidden="true"> · Sign out</span>
          </button>
        ) : (
          <Link to="/login" className="nav-link">Sign in</Link>
        )}
      </div>
    </nav>
  );
}

