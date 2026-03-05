import React, { useEffect, useState } from "react";
import api from "../api/axios";
import "./css/PagesCommon.css";
import "./css/User.css";

const Users = () => {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [visiblePasswordId, setVisiblePasswordId] = useState(null);
  const [error, setError] = useState("");
  const [formData, setForm] = useState({
    name: "",
    email: "",
    password: "",
  });

  const fetchUsers = async () => {
    try {
      setError("");
      const res = await api.get("/api/user/allUsers");
      setUsers(res.data || []);
    } catch (err) {
      const msg = err.response?.data?.error || err.response?.data?.message || "Failed to load users.";
      setError(msg);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const filteredUsers = users.filter(
    (u) =>
      (u.name || "").toLowerCase().includes(search.toLowerCase()) ||
      (u.email || "").toLowerCase().includes(search.toLowerCase()) ||
      String(u.id) === search
  );

  const handleChange = (e) => {
    setForm({ ...formData, [e.target.name]: e.target.value });
  };

  const resetForm = () => {
    setForm({ name: "", email: "", password: "" });
    setEditingId(null);
  };

  const saveUser = async () => {
    try {
      setError("");
      const payload = { name: formData.name, email: formData.email };
      if (formData.password && formData.password.trim()) {
        payload.password = formData.password;
      }
      if (editingId) {
        await api.put(`/api/user/${editingId}`, payload);
        alert("User updated successfully");
      } else {
        await api.post("/api/user/register", formData);
        alert("User added successfully");
      }
      resetForm();
      fetchUsers();
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data?.errors?.email || "Failed to save user.";
      setError(msg);
    }
  };

  const editUser = (user) => {
    setForm({
      name: user.name || "",
      email: user.email || "",
      password: "", // Leave blank to keep current; API does not return password
    });
    setEditingId(user.id);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this user?")) {
      try {
        setError("");
        await api.delete(`/api/user/${id}`);
        fetchUsers();
      } catch (err) {
        setError(err.response?.data?.message || "Failed to delete user.");
      }
    }
  };

  return (
    <div className="users-wrap">
      <h1 className="page-title">Users</h1>
      {error && (
        <div className="page-card form-error" role="alert">
          {error}
        </div>
      )}

      <div className="page-card users-form-card">
        <div className="form-field">
          <label htmlFor="user-name">Name</label>
          <input
            id="user-name"
            className="page-input"
            type="text"
            name="name"
            placeholder="Full name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-field">
          <label htmlFor="user-email">Email</label>
          <input
            id="user-email"
            className="page-input"
            type="email"
            name="email"
            placeholder="email@example.com"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-field">
          <label htmlFor="user-password">Password</label>
          <input
            id="user-password"
            className="page-input"
            type="password"
            name="password"
            placeholder={editingId ? "Leave blank to keep current" : "••••••••"}
            value={formData.password}
            onChange={handleChange}
            required={!editingId}
          />
        </div>
        <div className="form-actions">
          <button type="button" className="btn btn-primary" onClick={saveUser}>
            {editingId ? "Update" : "Add user"}
          </button>
          {editingId && (
            <button type="button" className="btn btn-secondary" onClick={resetForm}>
              Cancel
            </button>
          )}
        </div>
      </div>

      <input
        type="text"
        className="search-bar"
        placeholder="Search by ID, name, or email"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <div className="page-card users-table-card">
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Password</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td>
                  <span
                    className="password-cell"
                    onClick={() => setVisiblePasswordId((prev) => (prev === u.id ? null : u.id))}
                    role="button"
                    tabIndex={0}
                    onKeyDown={(e) => e.key === "Enter" && setVisiblePasswordId((prev) => (prev === u.id ? null : u.id))}
                    title={visiblePasswordId === u.id ? "Click to hide" : "Click to show"}
                  >
                    {visiblePasswordId === u.id ? (u.password ?? "—") : "********"}
                  </span>
                </td>
                <td>
                  <div className="cell-actions">
                    <button
                      type="button"
                      className="btn btn-secondary btn-sm"
                      onClick={() => editUser(u)}
                    >
                      Edit
                    </button>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(u.id)}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Users;
