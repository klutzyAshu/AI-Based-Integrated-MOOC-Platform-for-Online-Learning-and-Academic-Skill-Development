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
      <div className="page-heading"><div><span className="eyebrow">ADMINISTRATION</span><h2>Manage users</h2><p>Review accounts, update access, and deactivate users.</p></div></div>
      <div className="table-wrap"><table>
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
                <button className="button-danger" onClick={() => handleDeactivate(u.id)}>
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
      </table></div>
    </div>
  );
}

