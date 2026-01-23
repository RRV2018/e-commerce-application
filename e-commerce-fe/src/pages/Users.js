import React, { useEffect, useState } from "react";
import api from "../api/axios";

const Users = () => {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");
  const [editingId, setEditingId] = useState(null);

  // Form is EMPTY on page load
  const [formData, setForm] = useState({
    name: "",
    email: "",
    password: ""
  });

  /* ---------- FETCH USERS ---------- */
  const fetchUsers = async () => {
    try {
      const res = await api.get("/api/user/allUsers");
      setUsers(res.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  /* ---------- SEARCH ---------- */
  const filteredUsers = users.filter((u) =>
    (u.name || "").toLowerCase().includes(search.toLowerCase()) ||
    (u.email || "").toLowerCase().includes(search.toLowerCase()) ||
    String(u.id) === search
  );

  /* ---------- FORM HANDLING ---------- */
    const handleChange = (e) => {
        setForm({ ...formData, [e.target.name]: e.target.value });
      };


  const resetForm = () => {
    setForm({
      name: "",
      email: "",
      password: ""
    });
    setEditingId(null);
  };

  /* ---------- ADD / UPDATE ---------- */
  const saveUser = async () => {
    try {
      if (editingId) {
        await api.put(`/api/user/${editingId}`, formData);
        alert("User updated successfully");
      } else {
        await api.post("/api/user/register", formData);
        alert("User added successfully");
      }

      resetForm();
      fetchUsers();
    } catch (err) {
      console.error(err);
    }
  };

  /* ---------- EDIT ---------- */
  const editUser = (user) => {
    setForm({
      name: user.name || "",
      email: user.email || "",
      password: "" // password stays empty
    });
    setEditingId(user.id);
  };

  /* ---------- DELETE ---------- */
  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this user?")) {
      try {
        await api.delete(`/api/user/${id}`);
        fetchUsers();
      } catch (err) {
        console.error(err);
      }
    }
  };

  return (
    <div style={{ width: "80%", margin: "20px auto" }}>
      <h2>Users</h2>

      {/* ---------- USER FORM ---------- */}
      <table>
        <tbody>
          <tr>
            <td>User Name:</td>
            <td>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </td>
          </tr>

          <tr>
            <td>Email:</td>
            <td>
              <input
                type="email"
                name="email"
                value={formData.emailId}
                onChange={handleChange}
              />
            </td>
          </tr>

          <tr>
            <td>Password:</td>
            <td>
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required={!editingId}
              />
            </td>
          </tr>

          <tr>
            <td></td>
            <td>
              <button onClick={saveUser}>
                {editingId ? "Update" : "Add"}
              </button>

              {editingId && (
                <button onClick={resetForm} style={{ marginLeft: "10px" }}>
                  Cancel
                </button>
              )}
            </td>
          </tr>
        </tbody>
      </table>

      {/* ---------- SEARCH ---------- */}
      <input
        type="text"
        placeholder="Search by ID, name, or email"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{
          margin: "10px 0",
          padding: "5px",
          width: "300px"
        }}
      />

      {/* ---------- USERS TABLE ---------- */}
      <table style={{ width: "70%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
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
                <button onClick={() => editUser(u)}>Edit</button>
                <button
                  onClick={() => handleDelete(u.id)}
                  style={{ marginLeft: "5px" }}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Users;
