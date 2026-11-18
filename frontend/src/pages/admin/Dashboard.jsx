import { useEffect, useState } from "react";
import { useAuth } from "../../hooks/useAuth.js";
import api from "../../services/api.js";

export default function Dashboard() {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [metrics, setMetrics] = useState(null);
  const [categories, setCategories] = useState([]);
  const [categoryName, setCategoryName] = useState("");
  const [categoryDescription, setCategoryDescription] = useState("");

  const loadUsers = async () => {
    try {
      const { data } = await api.get("/users");
      setUsers(data);
      setError("");
    } catch {
      setError("We couldn’t load the user list. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const loadMetrics = async () => {
    try {
      const { data } = await api.get("/analytics/overview");
      setMetrics(data);
    } catch {
      setError("We couldn’t load platform analytics.");
    }
  };

  const loadCategories = async () => {
    try {
      const { data } = await api.get("/categories");
      setCategories(data);
    } catch {
      setError("We couldn’t load course categories.");
    }
  };

  useEffect(() => {
    loadUsers();
    loadMetrics();
    loadCategories();
  }, []);

  const handleRoleChange = async (id, role) => {
    try {
      await api.patch(`/users/${id}/role`, null, { params: { role } });
      await loadUsers();
    } catch {
      setError("We couldn’t update that role. Please try again.");
    }
  };

  const handleDeactivate = async (id) => {
    try {
      await api.delete(`/users/${id}`);
      await loadUsers();
    } catch {
      setError("We couldn’t deactivate that account. Please try again.");
    }
  };

  const handleCreateCategory = async (event) => {
    event.preventDefault();
    try {
      await api.post("/categories", { name: categoryName, description: categoryDescription });
      setCategoryName("");
      setCategoryDescription("");
      await loadCategories();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "We couldn’t create that category.");
    }
  };

  return (
    <div>
      <div className="page-heading"><div><span className="eyebrow">ADMINISTRATION</span><h2>Manage users</h2><p>Review accounts, update access, and deactivate users.</p></div></div>
      {metrics && <div className="stats-grid">
        {[["Learners", metrics.students], ["Instructors", metrics.instructors], ["Published courses", metrics.publishedCourses], ["Enrollments", metrics.enrollments], ["Completions", metrics.completions], ["Certificates", metrics.certificates]].map(([label, value]) => <div className="stat-card" key={label}><span>{label}</span><strong>{value}</strong></div>)}
      </div>}
      <section className="category-admin">
        <div><span className="eyebrow">COURSE CATALOG</span><h3>Categories</h3><p className="muted">Organize courses so learners can discover them.</p></div>
        <form className="category-create-form" onSubmit={handleCreateCategory}>
          <input aria-label="New category name" placeholder="Category name" value={categoryName} onChange={(event) => setCategoryName(event.target.value)} required />
          <input aria-label="Category description" placeholder="Short description (optional)" value={categoryDescription} onChange={(event) => setCategoryDescription(event.target.value)} />
          <button className="button-primary" type="submit">Add category</button>
        </form>
        {categories.map((category) => <form className="category-row" key={category.id} onSubmit={async (event) => {
          event.preventDefault();
          const fields = new FormData(event.currentTarget);
          try {
            await api.put(`/categories/${category.id}`, { name: fields.get("name"), description: fields.get("description") });
            await loadCategories();
          } catch (requestError) {
            setError(requestError.response?.data?.error || "We couldn’t update this category.");
          }
        }}>
          <input name="name" aria-label={`${category.name} name`} defaultValue={category.name} required />
          <input name="description" aria-label={`${category.name} description`} defaultValue={category.description || ""} />
          <button className="button-primary" type="submit">Save</button>
          <button className="button-danger" type="button" onClick={async () => {
            if (!window.confirm(`Delete “${category.name}”? Courses in this category will become uncategorized.`)) return;
            try { await api.delete(`/categories/${category.id}`); await loadCategories(); }
            catch { setError("We couldn’t delete this category."); }
          }}>Delete</button>
        </form>)}
        {categories.length === 0 && <p className="muted">No categories yet.</p>}
      </section>
      {error && <p className="auth-error" role="alert">{error}</p>}
      {loading ? <p className="muted">Loading users…</p> : <div className="table-wrap"><table>
        <thead>
          <tr>
            <th align="left">Name</th>
            <th align="left">Email</th>
            <th align="left">Role</th>
            <th align="left">Active</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.name}</td>
              <td>{u.email}</td>
              <td>
                  <select value={u.role} disabled={String(u.id) === String(user?.userId)} onChange={(e) => handleRoleChange(u.id, e.target.value)}>
                  <option value="STUDENT">STUDENT</option>
                  <option value="INSTRUCTOR">INSTRUCTOR</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </td>
              <td>{u.active ? "Yes" : "No"}</td>
              <td>
                <button className="button-danger" disabled={String(u.id) === String(user?.userId) || !u.active} onClick={() => handleDeactivate(u.id)}>
                  {u.active ? "Deactivate" : "Inactive"}
                </button>
              </td>
            </tr>
          ))}
          {users.length === 0 && (
            <tr>
              <td colSpan={5}>No users found.</td>
            </tr>
          )}
        </tbody>
      </table></div>}
    </div>
  );
}

