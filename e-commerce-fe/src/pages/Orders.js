import React, { useState, useEffect } from "react";
import api from "../api/axios";
import "./css/PagesCommon.css";
import "./css/Orders.css";

const ORDER_STATUSES = ["CREATED", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"];

/** Backend uses numeric id for DELETE/PATCH; OrderResponse only has orderId string (e.g. ORD00001). */
function orderIdToNumeric(orderId) {
  if (orderId == null) return null;
  if (typeof orderId === "number") return orderId;
  const m = String(orderId).match(/^ORD0*(\d+)$/);
  return m ? parseInt(m[1], 10) : null;
}

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [shippingOptions, setShippingOptions] = useState([]);
  const [couponCode, setCouponCode] = useState("");
  const [couponAmount, setCouponAmount] = useState("100");
  const [couponResult, setCouponResult] = useState(null);
  const [couponLoading, setCouponLoading] = useState(false);
  const [updatingId, setUpdatingId] = useState(null);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const res = await api.get("/api/order");
      setOrders(res.data || []);
    } catch (err) {
      console.error(err);
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
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
      (o.items && o.items.some((item) => String(item.productId).includes(search)))
    );
  });

  const cancelOrder = async (order) => {
    const id = orderIdToNumeric(order.orderId || order.id);
    if (id == null) {
      alert("Cannot cancel: order ID not recognized.");
      return;
    }
    if (!window.confirm("Cancel this order?")) return;
    try {
      await api.delete(`/api/order/${id}`);
      fetchOrders();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to cancel order.");
    }
  };

  const updateOrderStatus = async (order, status) => {
    const id = orderIdToNumeric(order.orderId || order.id);
    if (id == null) {
      alert("Cannot update: order ID not recognized.");
      return;
    }
    setUpdatingId(order.orderId || order.id);
    try {
      await api.patch(`/api/order/${id}/status`, { status });
      fetchOrders();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to update status.");
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="orders-wrap">
      <h1 className="page-title">Order Management</h1>

      <div className="orders-layout">
        <section className="orders-main">
          <div className="orders-toolbar">
            <input
              type="text"
              className="search-bar"
              placeholder="Search by Order ID, User ID, Amount, Status..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          {loading && <p className="page-loading">Loading orders...</p>}
          {!loading && filteredOrders.length === 0 && (
            <p className="page-empty">No orders found</p>
          )}

          {!loading && filteredOrders.length > 0 && (
            <div className="orders-list">
              {filteredOrders.map((o) => (
                <div key={o.orderId || o.id} className="order-card">
                  <div className="order-card-header">
                    <span className="order-card-id">Order #{o.orderId || o.id}</span>
                    <span className={`order-card-status status-${(o.status || "").toLowerCase()}`}>
                      {o.status || "—"}
                    </span>
                  </div>
                  <div className="order-card-body">
                    <div className="order-card-row">
                      <span className="order-card-label">User ID</span>
                      <span className="order-card-value">{o.userId}</span>
                    </div>
                    <div className="order-card-row">
                      <span className="order-card-label">Total</span>
                      <span className="order-card-value order-card-total">
                        ₹ {o.totalAmount != null ? Number(o.totalAmount).toFixed(2) : "0.00"}
                      </span>
                    </div>
                    {o.items && o.items.length > 0 && (
                      <div className="order-card-items">
                        <span className="order-card-label">Items</span>
                        <ul>
                          {o.items.map((item, idx) => (
                            <li key={idx}>
                              Product #{item.productId} × {item.quantity} — ₹{Number(item.price || 0).toFixed(2)}
                            </li>
                          ))}
                        </ul>
                      </div>
                    )}
                    <div className="order-card-row">
                      <span className="order-card-label">Created</span>
                      <span className="order-card-value">
                        {o.createdAt ? new Date(o.createdAt).toLocaleString() : "—"}
                      </span>
                    </div>
                  </div>
                  <div className="order-card-actions">
                    <label className="order-status-select-wrap">
                      <span className="order-status-label">Status</span>
                      <select
                        className="order-status-select"
                        value={o.status || ""}
                        onChange={(e) => updateOrderStatus(o, e.target.value)}
                        disabled={updatingId === (o.orderId || o.id)}
                      >
                        {ORDER_STATUSES.map((s) => (
                          <option key={s} value={s}>{s}</option>
                        ))}
                      </select>
                    </label>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => cancelOrder(o)}
                      disabled={updatingId === (o.orderId || o.id) || (o.status && o.status.toUpperCase() === "CANCELLED")}
                      title="Cancel order"
                    >
                      Cancel order
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>

        <aside className="orders-sidebar">
          <div className="page-card orders-sidebar-card">
            <h3>Shipping options</h3>
            {shippingOptions.length === 0 ? (
              <p className="page-empty">No shipping options configured.</p>
            ) : (
              <ul className="shipping-options-list">
                {shippingOptions.map((opt) => (
                  <li key={opt.id}>
                    <strong>{opt.name}</strong> — ₹{Number(opt.cost).toFixed(2)}
                    {opt.estimatedDays != null ? ` (${opt.estimatedDays} days)` : ""}
                    {opt.isDefault ? " · default" : ""}
                  </li>
                ))}
              </ul>
            )}
          </div>
          <div className="page-card orders-sidebar-card">
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
            <button
              type="button"
              className="btn btn-secondary"
              onClick={validateCoupon}
              disabled={couponLoading}
            >
              {couponLoading ? "Checking..." : "Validate"}
            </button>
            {couponResult && (
              <div className={`coupon-result ${couponResult.valid ? "valid" : "invalid"}`}>
                {couponResult.valid
                  ? `Valid — Discount: ₹${Number(couponResult.discountAmount).toFixed(2)}`
                  : couponResult.message}
              </div>
            )}
          </div>
        </aside>
      </div>
    </div>
  );
}
