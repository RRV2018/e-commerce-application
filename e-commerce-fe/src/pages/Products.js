import { useEffect, useState, useRef } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import { FaTrash, FaEdit, FaCartPlus, FaHeart } from "react-icons/fa";
import "./css/PagesCommon.css";
import "./Products.css";

function AddEditProductModal({ editingId, initialForm, onClose, onSaved }) {
  const defaultForm = { name: "", description: "", price: "", stock: "", categoryId: "" };
  const [form, setForm] = useState(initialForm || defaultForm);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    setForm(initialForm || defaultForm);
  }, [editingId, initialForm]);

  const handleChange = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const payload = {
        ...form,
        price: Number(form.price),
        stock: Number(form.stock),
        categoryId: form.categoryId ? Number(form.categoryId) : null,
      };
      if (editingId) await api.put(`/api/products/${editingId}`, payload);
      else await api.post("/api/products", payload);
      onSaved();
      onClose();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to save product");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content add-product-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{editingId ? "Edit Product" : "Add Product"}</h3>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Close">&times;</button>
        </div>
        <form onSubmit={handleSubmit} className="add-product-form">
          <label>Name</label>
          <input className="page-input" name="name" placeholder="Name" value={form.name} onChange={handleChange} required />
          <label>Description</label>
          <input className="page-input" name="description" placeholder="Description" value={form.description} onChange={handleChange} />
          <div className="form-row">
            <div>
              <label>Price</label>
              <input className="page-input" type="number" name="price" placeholder="Price" value={form.price} onChange={handleChange} required />
            </div>
            <div>
              <label>Stock</label>
              <input className="page-input" type="number" name="stock" placeholder="Stock" value={form.stock} onChange={handleChange} />
            </div>
          </div>
          <label>Category ID</label>
          <input className="page-input" type="number" name="categoryId" placeholder="Category ID" value={form.categoryId} onChange={handleChange} />
          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? "Saving..." : editingId ? "Update" : "Add"}</button>
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
}

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
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortBy, setSortBy] = useState("name");
  const [sortOrder, setSortOrder] = useState("asc");
  const [wishlistIds, setWishlistIds] = useState(new Set());
  const [reviewsModal, setReviewsModal] = useState(null);
  const [productModal, setProductModal] = useState(null);
  const didFetch = useRef(false);
  const pageSize = 20;

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

  const sortOptions = [
    { value: "name_asc", label: "Name A–Z", by: "name", order: "asc" },
    { value: "name_desc", label: "Name Z–A", by: "name", order: "desc" },
    { value: "price_asc", label: "Price low to high", by: "price", order: "asc" },
    { value: "price_desc", label: "Price high to low", by: "price", order: "desc" },
    { value: "id_asc", label: "ID ascending", by: "id", order: "asc" },
    { value: "id_desc", label: "ID descending", by: "id", order: "desc" },
  ];

  const sortProducts = (list) => {
    if (!list.length) return list;
    const order = sortOrder === "asc" ? 1 : -1;
    return [...list].sort((a, b) => {
      let va = a[sortBy];
      let vb = b[sortBy];
      if (sortBy === "name" || sortBy === "description") {
        va = (va || "").toString().toLowerCase();
        vb = (vb || "").toString().toLowerCase();
        return order * (va < vb ? -1 : va > vb ? 1 : 0);
      }
      va = Number(va) ?? 0;
      vb = Number(vb) ?? 0;
      return order * (va - vb);
    });
  };

  const openAddProductModal = () => setProductModal({ editingId: null, initialForm: null });
  const openEditProductModal = (p) => setProductModal({
    editingId: p.id,
    initialForm: {
      name: p.name || "",
      description: p.description || "",
      price: p.price ?? "",
      stock: p.stock ?? "",
      categoryId: p.category?.id || "",
    },
  });
  const closeProductModal = () => setProductModal(null);
  const onProductSaved = () => fetchProducts(page);

  const deleteProduct = async (id) => {
    if (window.confirm("Delete this product?")) {
      await api.delete(`/api/products/${id}`);
      fetchProducts(page);
    }
  };

  const addToCart = async (p) => {
    try {
      await api.post(`/api/products/card/${p.id}`);
    } catch (err) {
      alert(err.response?.data?.message || "Failed to add to cart");
    }
  };

  const filteredProducts = products.filter((p) =>
    `${p.id} ${p.name} ${p.description} `.toLowerCase().includes(search.toLowerCase())
  );
  const sortedProducts = sortProducts(filteredProducts);

  if (loading) return <p className="page-loading">Loading...</p>;
  if (error) return <p className="page-error">{error}</p>;

  return (
    <div className="products-wrap">
      <h1 className="page-title">Products</h1>

      <h2 className="products-section-title">Product list</h2>
      <div className="products-toolbar">
        <button type="button" className="btn btn-primary" onClick={openAddProductModal}>
          Add Product
        </button>
        <Link to="/cart" className="btn btn-secondary">Cart & Checkout</Link>
        <input
          className="search-bar"
          placeholder="Search products..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <label className="sort-label">
          Sort:
          <select
            className="sort-select"
            value={`${sortBy}_${sortOrder}`}
            onChange={(e) => {
              const opt = sortOptions.find((o) => o.value === e.target.value);
              if (opt) {
                setSortBy(opt.by);
                setSortOrder(opt.order);
              }
            }}
          >
            {sortOptions.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </label>
      </div>

      <div className="products-grid">
        {sortedProducts.map((p) => (
          <div key={p.id} className="product-card">
            <div className="product-card-header">
              <span className="product-card-id">#{p.id}</span>
              <span className="product-card-version">{p.version || "—"}</span>
            </div>
            <h3 className="product-card-name">{p.name}</h3>
            <p className="product-card-desc">{p.description || "—"}</p>
            <dl className="product-card-details">
              <dt>RAM</dt><dd>{p.ramSize ?? "—"} GB</dd>
              <dt>Storage</dt><dd>{p.hardDiskSize ?? "—"} GB</dd>
              <dt>Screen</dt><dd>{p.screenSize ?? "—"} inch</dd>
              <dt>Color</dt><dd>{p.color ?? "—"}</dd>
            </dl>
            <div className="product-card-price-row">
              <span className="product-card-price">₹ {Number(p.price).toFixed(2)}</span>
              <span className="product-card-stock">Stock: {p.stock ?? 0}</span>
            </div>
            <div className="product-card-actions">
              <button type="button" className="btn btn-primary btn-sm" onClick={() => addToCart(p)} title="Add to cart">
                <FaCartPlus /> Cart
              </button>
              <button
                type="button"
                className={`btn btn-sm ${wishlistIds.has(p.id) ? "wishlist-btn on" : "wishlist-btn"}`}
                onClick={() => toggleWishlist(p.id)}
                title={wishlistIds.has(p.id) ? "Remove from wishlist" : "Add to wishlist"}
              >
                <FaHeart />
              </button>
              <button type="button" className="btn btn-secondary btn-sm" onClick={() => setReviewsModal(p.id)}>
                Reviews
              </button>
              <button type="button" className="btn btn-secondary btn-sm" onClick={() => openEditProductModal(p)} title="Edit">
                <FaEdit />
              </button>
              <button type="button" className="btn btn-danger btn-sm" onClick={() => deleteProduct(p.id)} title="Delete">
                <FaTrash />
              </button>
            </div>
          </div>
        ))}
      </div>
      {sortedProducts.length === 0 && (
        <p className="page-empty">No products match your search.</p>
      )}

      {reviewsModal != null && (
        <ProductReviewsModal
          productId={reviewsModal}
          onClose={() => setReviewsModal(null)}
          onSaved={() => setReviewsModal(null)}
        />
      )}

      {productModal != null && (
        <AddEditProductModal
          editingId={productModal.editingId}
          initialForm={productModal.initialForm}
          onClose={closeProductModal}
          onSaved={onProductSaved}
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
