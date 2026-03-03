import { useState } from "react";
import api from "../api/axios";
import "./css/Dashboard.css";
import "./css/PagesCommon.css";

export default function Category() {
  const [name, setName] = useState("");

  const addCategory = async () => {
    await api.post("/api/products/category", { name });
    alert("Category Added");
    setName("");
  };

  return (
    <div className="category-card">
      <h3>Category Management</h3>
      <div className="category-form">
        <input
          className="page-input"
          placeholder="Category name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <button type="button" className="btn btn-primary" onClick={addCategory}>
          Add category
        </button>
      </div>
    </div>
  );
}
