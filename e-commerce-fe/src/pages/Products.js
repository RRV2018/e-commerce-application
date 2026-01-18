import { useEffect, useState } from "react";
import api from "../api/axios";

function Products() {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState(""); // 🔹 SEARCH STATE
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stock: "",
    categoryId: ""
  });
  const [editingId, setEditingId] = useState(null);

  /* ---------- FETCH PRODUCTS ---------- */
  const fetchProducts = async () => {
    try {
      const res = await api.get("/api/products");
      setProducts(res.data || []);
    } catch (err) {
      console.error(err);
      setError("Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  /* ---------- SEARCH FILTER ---------- */
  const filteredProducts = products.filter((p) =>
    (p.name || "").toLowerCase().includes(search.toLowerCase()) ||
    (p.description || "").toLowerCase().includes(search.toLowerCase()) ||
    (p.category?.name || "").toLowerCase().includes(search.toLowerCase()) ||
    String(p.id).includes(search)
  );

  /* ---------- FORM HANDLING ---------- */
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const resetForm = () => {
    setForm({
      name: "",
      description: "",
      price: "",
      stock: "",
      categoryId: ""
    });
    setEditingId(null);
  };

  /* ---------- ADD / UPDATE ---------- */

  const saveProduct = async () => {
    const payload = {
      ...form,
      price: Number(form.price),
      stock: Number(form.stock),
      categoryId: Number(form.categoryId)
    };

    if (editingId) {
       // EDIT
      await api.put(`/api/products/${editingId}`, payload);
      alert("Product updated successfully");
    } else {
       // ADD
      await api.post("/api/products", payload);
      alert("Product added successfully");
    }
    resetForm();
    fetchProducts();
  };

  /* ---------- EDIT ---------- */
  const editProduct = (product) => {
    setForm({
      name: product.name || "",
      description: product.description || "",
      price: product.price || "",
      stock: product.stock || "",
      categoryId: product.category?.id || ""
    });
    setEditingId(product.id);
  };

  /* ---------- DELETE ---------- */
  const deleteProduct = async (id) => {
    if (!window.confirm("Are you sure you want to delete this product?")) return;
    await api.delete(`/products/${id}`);
    alert("Product deleted");
    fetchProducts();
  };

  /* ---------- DELETE ---------- */
    const addProductToCard = async (id) => {
      await api.delete(`/products/${id}`);
      alert("Product deleted");
      fetchProducts();
    };

  if (loading) return <p style={styles.loading}>Loading products...</p>;
  if (error) return <p style={styles.error}>{error}</p>;

  return (
    <div style={styles.container}>
      <h2>{editingId ? "Edit Product" : "Add Product"}</h2>

      {/* ---------- PRODUCT FORM ---------- */}
      <table>
        <tbody>
          <tr>
            <td>Product Name:</td>
            <td>
              <input name="name" value={form.name} onChange={handleChange} />
            </td>
          </tr>
          <tr>
            <td>Description:</td>
            <td>
              <input name="description" value={form.description} onChange={handleChange} />
            </td>
          </tr>
          <tr>
            <td>Product Price:</td>
            <td>
              <input type="number" name="price" value={form.price} onChange={handleChange} />
            </td>
          </tr>
          <tr>
            <td>Quantity:</td>
            <td>
              <input type="number" name="stock" value={form.stock} onChange={handleChange} />
            </td>
          </tr>
          <tr>
            <td>Product Category:</td>
            <td>
              <input type="number" name="categoryId" value={form.categoryId} onChange={handleChange} />
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <button onClick={saveProduct}>
                {editingId ? "Update" : "Add"}
              </button>
              {editingId && <button onClick={resetForm}>Cancel</button>}
            </td>
          </tr>
        </tbody>
      </table>

      <hr />

      {/* ---------- SEARCH INPUT ---------- */}
      <input
        type="text"
        placeholder="Search by id, name, category, description..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{
          marginBottom: "15px",
          padding: "8px",
          width: "300px"
        }}
      />

      <h2 style={styles.title}>Products</h2>

      {/* ---------- PRODUCTS TABLE ---------- */}
      <div style={styles.card}>
        <table style={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Description</th>
              <th>Price (₹)</th>
              <th>Stock</th>
              <th>Category</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {filteredProducts.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td style={styles.name}>{p.name}</td>
                <td>{p.description}</td>
                <td>{p.price}</td>
                <td>{p.stock}</td>
                <td>{p.category?.name}</td>
                <td>
                  <button onClick={() => editProduct(p)}>Edit</button>
                  <button
                    style={styles.deleteBtn}
                    onClick={() => deleteProduct(p.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {filteredProducts.length === 0 && (
          <p style={styles.noData}>No products found</p>
        )}
      </div>
    </div>
  );
}

const styles = {
  container: {
    padding: "30px",
    background: "#f5f7fb",
    minHeight: "100vh",
  },
  title: {
    marginBottom: "20px",
  },
  card: {
    background: "#fff",
    borderRadius: "12px",
    boxShadow: "0 8px 20px rgba(0,0,0,0.08)",
    overflowX: "auto",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
  },
  name: {
    fontWeight: "600",
  },
  deleteBtn: {
    background: "#e63946",
    color: "#fff",
    border: "none",
    padding: "8px 14px",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
  loading: {
    padding: "20px",
    textAlign: "center",
  },
  error: {
    padding: "20px",
    color: "red",
    textAlign: "center",
  },
  noData: {
    padding: "20px",
    textAlign: "center",
    color: "#777",
  },
};

export default Products;
