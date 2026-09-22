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
    <nav style={styles.nav}>
      <Link to="/dashboard" style={styles.brand}>
        MOOC Platform
      </Link>
      <div style={styles.links}>
        <Link to="/dashboard" style={styles.link}>Dashboard</Link>
        {user && (user.role === "INSTRUCTOR" || user.role === "ADMIN") && (
          <Link to="/instructor" style={styles.link}>Instructor</Link>
        )}
        {user && user.role === "ADMIN" && (
          <Link to="/admin" style={styles.link}>Admin</Link>
        )}
        {user ? (
          <button onClick={handleLogout} style={styles.button}>
            Logout ({user.name})
          </button>
        ) : (
          <Link to="/login" style={styles.link}>Login</Link>
        )}
      </div>
    </nav>
  );
}

const styles = {
  nav: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "12px 24px",
    background: "#121212",
    color: "#fff",
  },
  brand: { color: "#fff", fontWeight: "bold", textDecoration: "none", fontSize: 18 },
  links: { display: "flex", gap: 16, alignItems: "center" },
  link: { color: "#fff", textDecoration: "none" },
  button: {
    background: "transparent",
    border: "1px solid #fff",
    color: "#fff",
    borderRadius: 6,
    padding: "6px 12px",
    cursor: "pointer",
  },
};
