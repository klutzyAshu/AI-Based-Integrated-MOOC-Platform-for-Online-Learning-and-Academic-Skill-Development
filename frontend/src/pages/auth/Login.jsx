import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth.js";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await login(email, password);
      navigate("/dashboard");
    } catch (err) {
      setError(err.response?.data?.error || "Login failed");
    }
  };

  return (
    <div className="auth-shell">
      <aside className="auth-aside">
        <div className="auth-brand"><span className="brand-mark">N</span><span>Neolce</span></div>
        <div className="auth-aside-copy">
          <span className="auth-kicker">LEARN AT YOUR PACE</span>
          <h2>Small steps.<br />Big progress.</h2>
          <p>A good course gives you somewhere to start. The rest comes one lesson at a time.</p>
        </div>
        <span className="auth-aside-footer">A place to keep learning.</span>
      </aside>
      <div className="auth-form-wrap"><form onSubmit={handleSubmit} className="auth-form">
        <h2>Welcome back</h2><p>Sign in to continue learning.</p>
        {error && <p className="auth-error" role="alert">{error}</p>}
        <input
          type="email"
          placeholder="Email address"
          aria-label="Email address"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          aria-label="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Sign in</button>
        <p className="auth-footer">New to Neolce? <Link to="/register">Create an account</Link></p>
      </form></div>
    </div>
  );
}
