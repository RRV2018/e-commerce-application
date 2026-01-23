import React, { useState, useEffect } from "react";
import api from "../api/axios";

const Card = ({ title, children }) => (
  <div style={{
    border: "1px solid #e5e7eb",
    borderRadius: 12,
    padding: 16,
    boxShadow: "0 2px 6px rgba(0,0,0,0.05)"
  }}>
    <h3 style={{ fontWeight: 600, marginBottom: 12 }}>{title}</h3>
    {children}
  </div>
);

export default function Orders() {
  const [orderId, setOrderId] = useState("");
  const [orders, setOrders] = useState([]); // ✅ always array
  const [loading, setLoading] = useState(false);

const [searchTerm, setSearchTerm] = useState("");

  // 1. Book Order
  const bookOrder = async () => {
    try {
      setLoading(true);
      const payload = {
        items: [{ productId: 1, quantity: 1, price: 55999 }],
      };

      const res = await api.post("/api/order", payload);
      setOrders([res.data]); // show created order
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // 2. Get all orders
  const getOrders = async () => {
    try {
      setLoading(true);
      const res = await api.get("/api/order");
      setOrders(res.data);
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
          o.items.some(item =>
            String(item.productId).includes(search)
          ))
      );
    });

  // 3. Get single order
  const getSingleOrder = async () => {
    if (!orderId) return;
    try {
      setLoading(true);
      const res = await api.get(`/api/order/${orderId}`);
      setOrders([res.data]); // ✅ wrap in array
    } catch (err) {
      console.error(err);
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>
        Order Management
      </h1>

      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
        gap: 16
      }}>
        <Card title="Book Order">
          <button onClick={bookOrder}>Create Order</button>
        </Card>

        <Card title="Search Orders">
          <button onClick={getOrders}>Get All Orders</button>
        </Card>

        <Card title="Search Single Order">
          <input
            placeholder="Order ID"
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
          />
          <button onClick={getSingleOrder} style={{ marginTop: 8 }}>
            Search
          </button>
        </Card>
      </div>

      {/* RESULT TABLE */}
      <div style={{ marginTop: 32 }}>
        <h3>Orders</h3>
            <input
              type="text"
              placeholder="Search by Order ID, Product ID, Amount, Status..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ marginBottom: 12, padding: 6, width: 300 }}
            />
        {loading && <p>Loading...</p>}

        {!loading && orders.length === 0 && (
          <p>No orders found</p>
        )}


        {filteredOrders.length > 0 && (
          <table border="1" cellPadding="8" cellSpacing="0" width="100%">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>User ID</th>
                <th>Status</th>
                <th>Total Amount</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map((o) => (
                <tr key={o.orderId || o.id}>
                  <td>{o.orderId || o.id}</td>
                  <td>{o.userId}</td>
                  <td>{o.status}</td>
                  <td>{o.totalAmount}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
