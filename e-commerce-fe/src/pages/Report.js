import React, { useEffect, useState } from "react";
import api from "../api/axios";
import "./css/PagesCommon.css";

const Report = () => {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    api
      .get("/api/order/reports/summary")
      .then((res) => setSummary(res.data))
      .catch((err) => setError(err.response?.data?.message || err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page-card"><p>Loading report...</p></div>;
  if (error) return <div className="page-card"><p className="text-red-600">Error: {error}</p></div>;
  if (!summary) return null;

  const { totalOrders, totalRevenue, ordersByStatus } = summary;

  return (
    <div className="page-card">
      <h1 className="page-title">Sales Report</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <div className="border rounded p-4 bg-gray-50">
          <h3 className="font-semibold text-gray-600">Total Orders</h3>
          <p className="text-2xl font-bold">{totalOrders}</p>
        </div>
        <div className="border rounded p-4 bg-gray-50">
          <h3 className="font-semibold text-gray-600">Total Revenue</h3>
          <p className="text-2xl font-bold">
            ${totalRevenue != null ? Number(totalRevenue).toFixed(2) : "0.00"}
          </p>
        </div>
      </div>
      <h3 className="font-semibold mb-2">Orders by Status</h3>
      <ul className="list-disc list-inside space-y-1">
        {ordersByStatus &&
          Object.entries(ordersByStatus).map(([status, count]) => (
            <li key={status}>
              <span className="capitalize">{status.toLowerCase()}</span>: {count}
            </li>
          ))}
      </ul>
    </div>
  );
};

export default Report;
