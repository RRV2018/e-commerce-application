import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import { FaTrash } from "react-icons/fa";
import { AiOutlinePlus, AiOutlineMinus } from "react-icons/ai";
import "./css/PagesCommon.css";
import "./css/Cart.css";

export default function Cart() {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [checkoutLoading, setCheckoutLoading] = useState(false);

  const fetchCart = async () => {
    try {
      setError("");
      const res = await api.get("/api/products/card");
      setCartItems(res.data || []);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load cart.");
      setCartItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  const totalAmount = cartItems.reduce((sum, item) => sum + Number(item.amount || 0), 0);

  const removeItem = async (c) => {
    if (!window.confirm("Remove this item from cart?")) return;
    try {
      await api.delete(`/api/products/card/${c.id}`);
      fetchCart();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to remove.");
    }
  };

  const increaseQty = async (c) => {
    try {
      await api.post(`/api/products/card/${c.id}/increase`);
      fetchCart();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update.");
    }
  };

  const decreaseQty = async (c) => {
    try {
      await api.post(`/api/products/card/${c.id}/decrease`);
      fetchCart();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update.");
    }
  };

  const checkout = async () => {
    if (cartItems.length === 0) {
      alert("Your cart is empty.");
      return;
    }
    if (!window.confirm("Proceed to checkout and place order?")) return;
    setCheckoutLoading(true);
    try {
      await api.post("/api/order/card");
      alert("Order placed successfully.");
      fetchCart();
    } catch (err) {
      alert(err.response?.data?.message || "Checkout failed.");
    } finally {
      setCheckoutLoading(false);
    }
  };

  if (loading) return <div className="page-card"><p>Loading cart...</p></div>;
  if (error) return <div className="page-card"><p className="form-error">{error}</p></div>;

  return (
    <div className="cart-page">
      <h1 className="page-title">Cart & Checkout</h1>
      {cartItems.length === 0 ? (
        <div className="page-card cart-empty">
          <p className="page-empty">Your cart is empty.</p>
          <Link to="/products" className="btn btn-primary">Browse Products</Link>
        </div>
      ) : (
        <>
          <div className="page-card cart-list-card">
            <table className="data-table cart-table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Quantity</th>
                  <th>Amount</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {cartItems.map((c) => (
                  <tr key={c.id}>
                    <td>{c.productName}</td>
                    <td>
                      <div className="cart-qty-cell">
                        <AiOutlineMinus onClick={() => decreaseQty(c)} />
                        <span>{c.quantity}</span>
                        <AiOutlinePlus onClick={() => increaseQty(c)} />
                      </div>
                    </td>
                    <td className="num">₹ {Number(c.amount).toFixed(2)}</td>
                    <td>
                      <button type="button" className="btn btn-danger btn-sm" onClick={() => removeItem(c)} title="Remove">
                        <FaTrash />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="page-card cart-checkout-card">
            <div className="cart-total-row">
              <strong>Total</strong>
              <strong className="cart-total-amount">₹ {Number(totalAmount).toFixed(2)}</strong>
            </div>
            <div className="cart-checkout-actions">
              <Link to="/products" className="btn btn-secondary">Continue shopping</Link>
              <button type="button" className="btn btn-primary" onClick={checkout} disabled={checkoutLoading}>
                {checkoutLoading ? "Placing order..." : "Checkout"}
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
