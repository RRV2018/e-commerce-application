import React, { useState } from "react";
import api from "../api/axios";

const API_BASE = "http://localhost:8081/api/orders";

// Simple reusable Card component (no shadcn dependency)
const Card = ({ title, children }) => (
  <div style={{ border: "1px solid #e5e7eb", borderRadius: 12, padding: 16, boxShadow: "0 2px 6px rgba(0,0,0,0.05)" }}>
    <h3 style={{ fontWeight: 600, marginBottom: 12 }}>{title}</h3>
    {children}
  </div>
);

export default function Orders() {
  const [orderId, setOrderId] = useState("");
  const [result, setResult] = useState(null);
 // replace with real token

  const headers = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };

  // 1. Book Order
  const bookOrder = async () => {
    const payload = {
      items: [
        { productId: 1, quantity: 1, price: 55999 },
      ],
    };

    const saveProduct = async () => {
        const payload = {
          ...form,
          price: Number(form.price),
          stock: Number(form.stock),
          categoryId: Number(form.categoryId)
        };

    const res = await fetch(API_BASE, {
      method: "POST",
      headers,
      body: JSON.stringify(payload),
    });

    setResult(await res.json());
  };

  // 2. Get all orders
  const getOrders = async () => {
  const res = await api.get("/api/orders");
  setResult(res.data);
  };

  // 3. Get single order
  const getSingleOrder = async () => {
    if (!orderId) return;
    const res = await api.get(`/api/orders/${orderId}`);
    setResult(await res.json());
  };

  return (
    <div style={{ padding: 24 }}>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>
        Order Management
      </h1>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 16 }}>
        <Card title="Book Order">
          <button onClick={bookOrder}>Create Order</button>
        </Card>

        <Card title="Search Orders">
          <button onClick={getOrders}>Get All Orders</button>
        </Card>

        <Card title="Cancel Order">
          <input
            placeholder="Order ID"
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
          />
          <button disabled style={{ marginTop: 8 }}>
            Cancel (API Pending)
          </button>
        </Card>

        <Card title="Modify Order">
          <input
            placeholder="Order ID"
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
          />
          <button disabled style={{ marginTop: 8 }}>
            Modify (API Pending)
          </button>
        </Card>
      </div>

      <div style={{ marginTop: 32, maxWidth: 400 }}>
        <h3>Search Single Order</h3>
        <input
          placeholder="Enter Order ID"
          value={orderId}
          onChange={(e) => setOrderId(e.target.value)}
        />
        <button onClick={getSingleOrder} style={{ marginLeft: 8 }}>
          Search
        </button>
      </div>
      <div style={styles.card}>
              <table style={styles.table}>
                <thead>
                  <tr>
                    <th>Order ID</th>
                    <th>User ID</th>
                    <th>Order Status</th>
                    <th>Total Amount (₹)</th>
                  </tr>
                </thead>

                <tbody>
                  {result.map((o) => (
                    <tr key={o.orderId}>
                      <td>{o.orderId}</td>
                      <td>{o.userId}</td>
                      <td>{o.status}</td>
                      <td>{o.totalAmount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {result.length === 0 && (
                <p style={styles.noData}>No order found</p>
              )}
            </div>



    </div>
  );
}
