import { useEffect, useState } from "react";
import api from "../api/axios";
import { FaTrash, FaEdit, FaCartPlus, FaPlus, FaMinus } from "react-icons/fa";
import { AiOutlinePlus, AiOutlineMinus } from "react-icons/ai";
import "./Products.css";

function Products() {
  const [products, setProducts] = useState([]);
  const [cardData, setCardData] = useState([]);
  const [search, setSearch] = useState("");
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

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await api.get("/api/products");
      setProducts(res.data || []);

      const cardItem = await api.get("/api/products/card");
      setCardData(cardItem.data || []);
    } catch {
      setError("Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const resetForm = () => {
    setForm({ name: "", description: "", price: "", stock: "", categoryId: "" });
    setEditingId(null);
  };

  const saveProduct = async () => {
    const payload = {
      ...form,
      price: Number(form.price),
      stock: Number(form.stock),
      categoryId: Number(form.categoryId)
    };

    editingId
      ? await api.put(`/api/products/${editingId}`, payload)
      : await api.post("/api/products", payload);

    resetForm();
    fetchProducts();
  };

  const editProduct = (p) => {
    setForm({
      name: p.name,
      description: p.description,
      price: p.price,
      stock: p.stock,
      categoryId: p.category?.id || ""
    });
    setEditingId(p.id);
  };

  const deleteProduct = async (id) => {
    if (window.confirm("Delete this product?")) {
      await api.delete(`/api/products/${id}`);
      fetchProducts();
    }
  };

  const addToCart = async (p) => {
    await api.post(`/api/products/card/${p.id}`);
    fetchProducts();
  };

  const bookOrder = async () => {
    await api.post("/api/orders/card");
    alert("Order booked successfully");
  };

  const filteredProducts = products.filter((p) =>
    `${p.id} ${p.name} ${p.description} ${p.category?.name}`
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  const totalAmount = cardData.reduce(
    (sum, item) => sum + Number(item.amount || 0),
    0
  );

  const removeCartItem = async (c) => {
   if (!window.confirm("Remove item from cart?")) return;
    await api.delete(`/api/products/card/${c.id}`);
    fetchProducts();
  };

  const increaseQty = async (c) => {
    await api.post(`/api/products/card/${c.id}/increase`);
    fetchProducts();
  };

  const decreaseQty = async (c) => {
    await api.post(`/api/products/card/${c.id}/decrease`);
    fetchProducts();
  };
  if (loading) return <p className="loading">Loading...</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <div className="products-container">
      {/* FORM + CART */}
      <div className="top-grid">
        <div className="card">
          <h2>{editingId ? "Edit Product" : "Add Product"}</h2>

          <input name="name" placeholder="Name" value={form.name} onChange={handleChange} />
          <input name="description" placeholder="Description" value={form.description} onChange={handleChange} />
          <input type="number" name="price" placeholder="Price" value={form.price} onChange={handleChange} />
          <input type="number" name="stock" placeholder="Stock" value={form.stock} onChange={handleChange} />
          <input type="number" name="categoryId" placeholder="Category ID" value={form.categoryId} onChange={handleChange} />

          <div className="form-actions">
            <button onClick={saveProduct}>{editingId ? "Update" : "Add"}</button>
            {editingId && <button className="secondary" onClick={resetForm}>Cancel</button>}
          </div>
        </div>

        <div className="card">
          <h2>Cart Items</h2>
          <table>
            <thead>
              <tr><th>Name</th><th>Qty</th><th>Amount</th><th>Action</th></tr>
            </thead>
            <tbody>
              {cardData.map(c => (
                <tr key={c.id}>
                  <td>{c.productName}</td>
                  <td className="qty-cell">
                            <AiOutlineMinus onClick={() => decreaseQty(c)} />
                            <span>{c.quantity}</span>
                            <AiOutlinePlus onClick={() => increaseQty(c)} />
                   </td>
                  <td>{c.amount}</td>
                <td className="cart-actions">
                <FaTrash onClick={() => removeCartItem(c)} /> </td>
                </tr>

              ))}
              <tr className="total-row">
                    <td colSpan="2"><strong>Total Amount</strong></td>
                    <td><strong>₹ {totalAmount}</strong></td>
                    <td></td>
              </tr>
            </tbody>
          </table>
          <button className="primary full" onClick={bookOrder}>Book Order</button>
        </div>
      </div>

      {/* PRODUCT LIST */}
      <h2 className="section-title">Products</h2>
      <input
        className="search"
        placeholder="Search products..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <div className="card">
        <table className="product-table">
          <thead>
            <tr>
              <th>ID</th><th>Name</th><th>Desc</th><th>₹</th><th>Stock</th><th>Category</th><th>Action</th>
            </tr>
          </thead>
          <tbody>
            {filteredProducts.map(p => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td className="bold">{p.name}</td>
                <td>{p.description}</td>
                <td>{p.price}</td>
                <td>{p.stock}</td>
                <td>{p.category?.name}</td>
                <td className="actions">
                  <FaEdit onClick={() => editProduct(p)} />
                  <FaTrash onClick={() => deleteProduct(p.id)} />
                  <FaCartPlus onClick={() => addToCart(p)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Products;
