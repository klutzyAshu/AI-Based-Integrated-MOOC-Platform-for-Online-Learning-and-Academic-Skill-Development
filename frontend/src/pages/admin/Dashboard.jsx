import { useEffect, useState } from "react";
import api from "../../services/api.js";

export default function Dashboard() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    api.get("/users").then(({ data }) => setUsers(data));
  }, []);

  const handleRoleChange = async (id, role) => {
    await api.patch(`/users/${id}/role`, null, { params: { role } });
    const { data } = await api.get("/users");
    setUsers(data);
  };

  const handleDeactivate = async (id) => {
    await api.delete(`/users/${id}`);
    const { data } = await api.get("/users");
    setUsers(data);
  };

  return (
    <div>
      <h2>Admin Dashboard</h2>
      <table style={styles.table}>
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
                <select value={u.role} onChange={(e) => handleRoleChange(u.id, e.target.value)}>
                  <option value="STUDENT">STUDENT</option>
                  <option value="INSTRUCTOR">INSTRUCTOR</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </td>
              <td>{u.active ? "Yes" : "No"}</td>
              <td>
                <button style={styles.button} onClick={() => handleDeactivate(u.id)}>
                  Deactivate
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
      </table>
    </div>
  );
}

const styles = {
  table: { width: "100%", borderCollapse: "collapse", marginTop: 12 },
  button: { padding: "4px 10px", borderRadius: 6, border: "none", background: "#c0392b", color: "#fff", cursor: "pointer" },
};
