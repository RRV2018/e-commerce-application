import React, { useState, useEffect } from "react";
import api from "../api/axios";
import "./css/PagesCommon.css";
import "./css/Orders.css";

export default function Orders() {
  const [orderId, setOrderId] = useState("");
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [shippingOptions, setShippingOptions] = useState([]);
  const [couponCode, setCouponCode] = useState("");
  const [couponAmount, setCouponAmount] = useState("100");
  const [couponResult, setCouponResult] = useState(null);
  const [couponLoading, setCouponLoading] = useState(false);

  const bookOrder = async () => {
    try {
      setLoading(true);
      const payload = {
        items: [{ productId: 1, quantity: 1, price: 55999 }],
      };
      const res = await api.post("/api/order", payload);
      setOrders([res.data]);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const getOrders = async () => {
    try {
      setLoading(true);
      const res = await api.get("/api/order");
      setOrders(res.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getOrders();
  }, []);

  useEffect(() => {
    api.get("/api/order/shipping-options").then((res) => setShippingOptions(res.data || [])).catch(() => setShippingOptions([]));
  }, []);

  const validateCoupon = async () => {
    if (!couponCode.trim()) return;
    setCouponLoading(true);
    setCouponResult(null);
    try {
      const res = await api.get("/api/order/coupons/validate", {
        params: { code: couponCode.trim(), amount: Number(couponAmount) || 0 },
      });
      setCouponResult(res.data);
    } catch (err) {
      setCouponResult({ valid: false, message: err.response?.data?.message || "Validation failed", discountAmount: 0, couponCode: couponCode });
    } finally {
      setCouponLoading(false);
    }
  };

  const filteredOrders = orders.filter((o) => {
    const search = searchTerm.toLowerCase();
    return (
      String(o.orderId || o.id).includes(search) ||
      String(o.userId).includes(search) ||
      String(o.totalAmount).includes(search) ||
      (o.status && o.status.toLowerCase().includes(search)) ||
      (o.items &&
        o.items.some((item) => String(item.productId).includes(search)))
    );
  });

  const getSingleOrder = async () => {
    if (!orderId) return;
    try {
      setLoading(true);
      const res = await api.get(`/api/order/${orderId}`);
      setOrders([res.data]);
    } catch (err) {
      console.error(err);
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="orders-wrap">
      <h1 className="page-title">Order Management</h1>

      <div className="orders-actions-grid">
        <div className="orders-action-card">
          <h3>Book order</h3>
          <button
            type="button"
            className="btn btn-primary"
            onClick={bookOrder}
            disabled={loading}
          >
            Create order
          </button>
        </div>

        <div className="orders-action-card">
          <h3>All orders</h3>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={getOrders}
            disabled={loading}
          >
            Get all orders
          </button>
        </div>

        <div className="orders-action-card">
          <h3>Search by order ID</h3>
          <input
            className="page-input"
            placeholder="Order ID"
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
          />
          <button
            type="button"
            className="btn btn-secondary"
            onClick={getSingleOrder}
            disabled={loading}
          >
            Search
          </button>
        </div>
      </div>

      <div className="orders-actions-grid orders-extras">
        <div className="page-card">
          <h3>Shipping options</h3>
          {shippingOptions.length === 0 ? (
            <p className="page-empty">No shipping options configured.</p>
          ) : (
            <ul className="shipping-options-list">
              {shippingOptions.map((opt) => (
                <li key={opt.id}>
                  <strong>{opt.name}</strong> — ₹{Number(opt.cost).toFixed(2)} {opt.estimatedDays != null ? `(${opt.estimatedDays} days)` : ""} {opt.isDefault ? "(default)" : ""}
                </li>
              ))}
            </ul>
          )}
        </div>
        <div className="page-card">
          <h3>Coupon validation</h3>
          <input
            className="page-input"
            placeholder="Coupon code"
            value={couponCode}
            onChange={(e) => setCouponCode(e.target.value)}
          />
          <input
            className="page-input"
            type="number"
            placeholder="Order amount"
            value={couponAmount}
            onChange={(e) => setCouponAmount(e.target.value)}
          />
          <button type="button" className="btn btn-secondary" onClick={validateCoupon} disabled={couponLoading}>
            {couponLoading ? "Checking..." : "Validate"}
          </button>
          {couponResult && (
            <div className={`coupon-result ${couponResult.valid ? "valid" : "invalid"}`}>
              {couponResult.valid ? `Valid — Discount: ₹${Number(couponResult.discountAmount).toFixed(2)}` : couponResult.message}
            </div>
          )}
        </div>
      </div>

      <div className="page-card orders-table-wrap">
        <h2>Orders</h2>
        <input
          type="text"
          className="search-bar"
          placeholder="Search by Order ID, Product ID, Amount, Status..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />

        {loading && <p className="page-loading">Loading...</p>}
        {!loading && orders.length === 0 && (
          <p className="page-empty">No orders found</p>
        )}

        {!loading && filteredOrders.length > 0 && (
          <table className="data-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>User ID</th>
                <th>Status</th>
                <th>Total amount</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map((o) => (
                <tr key={o.orderId || o.id}>
                  <td>{o.orderId || o.id}</td>
                  <td>{o.userId}</td>
                  <td>{o.status}</td>
                  <td>{o.totalAmount}</td>
                  <td>{o.createdAt}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
