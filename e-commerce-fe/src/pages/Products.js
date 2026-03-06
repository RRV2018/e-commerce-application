import { useEffect, useState, useRef } from "react";
import api from "../api/axios";
import { FaTrash, FaEdit, FaCartPlus, FaHeart } from "react-icons/fa";
import { AiOutlinePlus, AiOutlineMinus } from "react-icons/ai";
import "./css/PagesCommon.css";
import "./Products.css";

function ProductReviewsModal({ productId, onClose, onSaved }) {
  const [reviews, setReviews] = useState([]);
  const [rating, setRating] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [form, setForm] = useState({ rating: 5, comment: "" });

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      try {
        const [revRes, ratRes] = await Promise.all([
          api.get(`/api/products/${productId}/reviews`),
          api.get(`/api/products/${productId}/rating`),
        ]);
        if (!cancelled) {
          setReviews(revRes.data || []);
          setRating(ratRes.data?.averageRating ?? 0);
        }
      } catch {
        if (!cancelled) setReviews([]);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [productId]);

  const submitReview = async (e) => {
    e.preventDefault();
    setSubmitLoading(true);
    try {
      await api.post(`/api/products/${productId}/reviews`, { rating: form.rating, comment: form.comment || null });
      const [revRes, ratRes] = await Promise.all([
        api.get(`/api/products/${productId}/reviews`),
        api.get(`/api/products/${productId}/rating`),
      ]);
      setReviews(revRes.data || []);
      setRating(ratRes.data?.averageRating ?? 0);
      setForm({ rating: 5, comment: "" });
    } catch (err) {
      alert(err.response?.data?.message || "Failed to save review");
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content reviews-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Reviews — Product #{productId}</h3>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Close">&times;</button>
        </div>
        {loading ? (
          <p>Loading...</p>
        ) : (
          <>
            <p className="reviews-average">Average rating: <strong>{Number(rating).toFixed(1)}</strong> / 5</p>
            <ul className="reviews-list">
              {reviews.length === 0 ? <li className="page-empty">No reviews yet.</li> : reviews.map((r) => (
                <li key={r.id}>
                  <span className="review-rating">{r.rating}/5</span> — {r.comment || "(no comment)"}
                  <span className="review-date">{r.createdAt ? new Date(r.createdAt).toLocaleDateString() : ""}</span>
                </li>
              ))}
            </ul>
            <form onSubmit={submitReview} className="reviews-form">
              <label>Your rating (1–5)</label>
              <select value={form.rating} onChange={(e) => setForm((f) => ({ ...f, rating: Number(e.target.value) }))}>
                {[1, 2, 3, 4, 5].map((n) => <option key={n} value={n}>{n}</option>)}
              </select>
              <label>Comment (optional)</label>
              <input type="text" className="page-input" value={form.comment} onChange={(e) => setForm((f) => ({ ...f, comment: e.target.value }))} placeholder="Your review" />
              <div className="form-actions">
                <button type="submit" className="btn btn-primary" disabled={submitLoading}>{submitLoading ? "Saving..." : "Submit review"}</button>
                <button type="button" className="btn btn-secondary" onClick={onClose}>Close</button>
              </div>
            </form>
          </>
        )}
      </div>
    </div>
  );
}

function Products() {
  const [products, setProducts] = useState([]);
  const [cardData, setCardData] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [wishlistIds, setWishlistIds] = useState(new Set());
  const [reviewsModal, setReviewsModal] = useState(null);
  const didFetch = useRef(false);
  const pageSize = 20;

  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stock: "",
    categoryId: "",
  });
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    if (didFetch.current) return;
    didFetch.current = true;
    fetchProducts(0);
  }, []);

  const fetchProducts = async (pageNumber) => {
    try {
      const res = await api.get(
        `/api/products/search?page=${pageNumber}&size=${pageSize}`
      );
      setProducts(res.data.content || []);
      setTotalPages(res.data.totalPages || 1);
      const cardItem = await api.get("/api/products/card");
      setCardData(cardItem.data || []);
      const wishRes = await api.get("/api/products/wishlist").catch(() => ({ data: [] }));
      setWishlistIds(new Set((wishRes.data || []).map((i) => i.productId)));
    } catch {
      setError("Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  const toggleWishlist = async (productId) => {
    const inList = wishlistIds.has(productId);
    try {
      if (inList) await api.delete(`/api/products/wishlist/${productId}`);
      else await api.post(`/api/products/wishlist/${productId}`);
      setWishlistIds((prev) => {
        const next = new Set(prev);
        if (inList) next.delete(productId);
        else next.add(productId);
        return next;
      });
    } catch (e) {
      setError(e.response?.data?.message || "Wishlist update failed");
    }
  };

  const nextPage = () => {
    if (page < totalPages - 1) {
      const next = page + 1;
      setPage(next);
      fetchProducts(next);
    }
  };

  const prevPage = () => {
    if (page > 0) {
      const prev = page - 1;
      setPage(prev);
      fetchProducts(prev);
    }
  };

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const resetForm = () => {
    setForm({
      name: "",
      description: "",
      price: "",
      stock: "",
      categoryId: "",
    });
    setEditingId(null);
  };

  const saveProduct = async () => {
    const payload = {
      ...form,
      price: Number(form.price),
      stock: Number(form.stock),
      categoryId: Number(form.categoryId),
    };
    editingId
      ? await api.put(`/api/products/${editingId}`, payload)
      : await api.post("/api/products", payload);
    resetForm();
    fetchProducts(page);
  };

  const editProduct = (p) => {
    setForm({
      name: p.name,
      description: p.description,
      price: p.price,
      stock: p.stock,
      categoryId: p.category?.id || "",
    });
    setEditingId(p.id);
  };

  const deleteProduct = async (id) => {
    if (window.confirm("Delete this product?")) {
      await api.delete(`/api/products/${id}`);
      fetchProducts(page);
    }
  };

  const addToCart = async (p) => {
    await api.post(`/api/products/card/${p.id}`);
    fetchProducts(page);
  };

  const bookOrder = async () => {
    alert("Do you want to book?");
    await api.post("/api/order/card");
    fetchProducts(page);
    alert("Order booked successfully");
  };

  const filteredProducts = products.filter((p) =>
    `${p.id} ${p.name} ${p.description} `.toLowerCase().includes(search.toLowerCase())
  );

  const totalAmount = cardData.reduce(
    (sum, item) => sum + Number(item.amount || 0),
    0
  );

  const removeCartItem = async (c) => {
    if (!window.confirm("Remove item from cart?")) return;
    await api.delete(`/api/products/card/${c.id}`);
    fetchProducts(page);
  };

  const increaseQty = async (c) => {
    await api.post(`/api/products/card/${c.id}/increase`);
    fetchProducts(page);
  };

  const decreaseQty = async (c) => {
    await api.post(`/api/products/card/${c.id}/decrease`);
    fetchProducts(page);
  };

  if (loading) return <p className="page-loading">Loading...</p>;
  if (error) return <p className="page-error">{error}</p>;

  return (
    <div className="products-wrap">
      <h1 className="page-title">Products</h1>

      <div className="products-top-grid">
        <div className="page-card products-form-card">
          <h2>{editingId ? "Edit Product" : "Add Product"}</h2>
          <div className="form-grid">
            <input
              className="page-input full-width"
              name="name"
              placeholder="Name"
              value={form.name}
              onChange={handleChange}
            />
            <input
              className="page-input full-width"
              name="description"
              placeholder="Description"
              value={form.description}
              onChange={handleChange}
            />
            <input
              className="page-input"
              type="number"
              name="price"
              placeholder="Price"
              value={form.price}
              onChange={handleChange}
            />
            <input
              className="page-input"
              type="number"
              name="stock"
              placeholder="Stock"
              value={form.stock}
              onChange={handleChange}
            />
            <input
              className="page-input full-width"
              type="number"
              name="categoryId"
              placeholder="Category ID"
              value={form.categoryId}
              onChange={handleChange}
            />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-primary" onClick={saveProduct}>
              {editingId ? "Update" : "Add"}
            </button>
            {editingId && (
              <button type="button" className="btn btn-secondary" onClick={resetForm}>
                Cancel
              </button>
            )}
          </div>
        </div>

        <div className="page-card cart-card">
          <h2>Cart</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Qty</th>
                <th>Amount</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {cardData.map((c) => (
                <tr key={c.id}>
                  <td>{c.productName}</td>
                  <td>
                    <div className="cart-qty-cell">
                      <AiOutlineMinus onClick={() => decreaseQty(c)} />
                      <span>{c.quantity}</span>
                      <AiOutlinePlus onClick={() => increaseQty(c)} />
                    </div>
                  </td>
                  <td className="num">{Number(c.amount).toFixed(2)}</td>
                  <td className="cart-actions-cell">
                    <FaTrash onClick={() => removeCartItem(c)} />
                  </td>
                </tr>
              ))}
              <tr className="cart-total-row">
                <td colSpan="2"><strong>Total</strong></td>
                <td className="num"><strong>₹ {Number(totalAmount).toFixed(2)}</strong></td>
                <td></td>
              </tr>
            </tbody>
          </table>
          <button
            type="button"
            className="btn btn-primary full-width cart-book-btn"
            onClick={bookOrder}
          >
            Book Order
          </button>
        </div>
      </div>

      <h2 className="products-section-title">Product list</h2>
      <input
        className="search-bar"
        placeholder="Search products..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <div className="page-card products-list-card">
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Version</th>
              <th>Desc</th>
              <th>Details</th>
              <th className="num">Price ₹</th>
              <th className="num">Stock</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredProducts.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td style={{ fontWeight: 600 }}>{p.name}</td>
                <td>{p.version}</td>
                <td>{p.description}</td>
                <td className="product-details">
                  RAM: {p.ramSize}, Storage: {p.hardDiskSize}, Size: {p.screenSize}, Color: {p.color}
                </td>
                <td className="num">{Number(p.price).toFixed(2)}</td>
                <td className="num">{p.stock}</td>
                <td>
                  <div className="actions-cell">
                    <FaEdit onClick={() => editProduct(p)} data-action="edit" />
                    <FaTrash onClick={() => deleteProduct(p.id)} data-action="delete" />
                    <FaCartPlus onClick={() => addToCart(p)} data-action="cart" />
                    <FaHeart
                      className={wishlistIds.has(p.id) ? "wishlist-on" : "wishlist-off"}
                      onClick={() => toggleWishlist(p.id)}
                      title={wishlistIds.has(p.id) ? "Remove from wishlist" : "Add to wishlist"}
                      data-action="wishlist"
                    />
                    <button type="button" className="btn btn-secondary btn-sm" onClick={() => setReviewsModal(p.id)}>
                      Reviews
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {reviewsModal != null && (
        <ProductReviewsModal
          productId={reviewsModal}
          onClose={() => setReviewsModal(null)}
          onSaved={() => setReviewsModal(null)}
        />
      )}

      <div className="products-pagination">
        <button
          type="button"
          className="btn btn-secondary"
          onClick={prevPage}
          disabled={page === 0}
        >
          Previous
        </button>
        <span>Page {page + 1} of {totalPages}</span>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={nextPage}
          disabled={page >= totalPages - 1}
        >
          Next
        </button>
      </div>
    </div>
  );
}

export default Products;
