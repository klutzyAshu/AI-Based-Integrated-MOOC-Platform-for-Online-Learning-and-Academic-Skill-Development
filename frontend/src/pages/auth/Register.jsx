import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth.js";

export default function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await register(name, email, password, "STUDENT");
      navigate("/dashboard");
    } catch (err) {
      setError(err.response?.data?.error || "Registration failed");
    }
  };

  return (
    <div className="auth-shell">
      <aside className="auth-aside">
        <div className="auth-brand"><span className="brand-mark">N</span><span>Neolce</span></div>
        <div className="auth-aside-copy">
          <span className="auth-kicker">A FRESH PAGE</span>
          <h2>Make room for what’s next.</h2>
          <p>Start with a subject you’re curious about. Build from there, at your own pace.</p>
        </div>
        <span className="auth-aside-footer">A place to keep learning.</span>
      </aside>
      <div className="auth-form-wrap"><form onSubmit={handleSubmit} className="auth-form">
        <h2>Create your account</h2><p>Start your learning journey today.</p>
        {error && <p className="auth-error" role="alert">{error}</p>}
        <input aria-label="Full name" placeholder="Full name" value={name} onChange={(e) => setName(e.target.value)} required />
        <input aria-label="Email address" type="email" placeholder="Email address" value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input aria-label="Password" type="password" placeholder="Password (min 8 characters)" minLength={8} value={password} onChange={(e) => setPassword(e.target.value)} required />
        <button type="submit">Create account</button>
        <p className="auth-footer">Already have an account? <Link to="/login">Sign in</Link></p>
      </form></div>
    </div>
  );
}
