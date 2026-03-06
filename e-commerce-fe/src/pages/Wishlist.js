import React, { useEffect, useState } from "react";
import api from "../api/axios";
import { FaHeart, FaTrash } from "react-icons/fa";
import "./css/PagesCommon.css";

const Wishlist = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchWishlist = async () => {
    try {
      setError("");
      const res = await api.get("/api/products/wishlist");
      setItems(res.data || []);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load wishlist.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWishlist();
  }, []);

  const remove = async (productId) => {
    try {
      await api.delete(`/api/products/wishlist/${productId}`);
      fetchWishlist();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to remove.");
    }
  };

  if (loading) return <div className="page-card"><p>Loading wishlist...</p></div>;
  if (error) return <div className="page-card"><p className="form-error">{error}</p></div>;

  return (
    <div className="page-card">
      <h1 className="page-title">
        <FaHeart className="inline-icon" /> My Wishlist
      </h1>
      {items.length === 0 ? (
        <p className="page-empty">Your wishlist is empty. Add products from the Products page.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Product</th>
              <th>Price</th>
              <th>Added</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.product?.name ?? `Product #${item.productId}`}</td>
                <td className="num">{item.product?.price != null ? `₹ ${Number(item.product.price).toFixed(2)}` : "—"}</td>
                <td>{item.addedAt ? new Date(item.addedAt).toLocaleDateString() : "—"}</td>
                <td>
                  <button
                    type="button"
                    className="btn btn-danger btn-sm"
                    onClick={() => remove(item.productId)}
                    title="Remove from wishlist"
                  >
                    <FaTrash /> Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default Wishlist;
