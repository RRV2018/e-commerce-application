import React, { useState, useEffect } from "react";
import api from "../api/axios";
import "./css/PagesCommon.css";
import "./css/Orders.css";

export default function Orders() {
  const [orderId, setOrderId] = useState("");
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

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
