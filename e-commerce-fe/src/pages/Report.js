import React, { useEffect, useState } from "react";
import api from "../api/axios";
import "./css/PagesCommon.css";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from "recharts";

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

  if (loading)
    return (
      <div className="page-card">
        <p>Loading report...</p>
      </div>
    );

  if (error)
    return (
      <div className="page-card">
        <p className="text-red-600">Error: {error}</p>
      </div>
    );

  if (!summary) return null;

  const { totalOrders, totalRevenue, ordersByStatus } = summary;

  const chartData = ordersByStatus
    ? Object.entries(ordersByStatus).map(([status, count]) => ({
        name: status,
        value: count,
      }))
    : [];

  return (
    <div className="page-card">
      <h1 className="page-title">Sales Report Dashboard</h1>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        <div className="border rounded-2xl p-6 shadow bg-white">
          <h3 className="text-gray-500">Total Orders</h3>
          <p className="text-3xl font-bold">{totalOrders}</p>
        </div>

        <div className="border rounded-2xl p-6 shadow bg-white">
          <h3 className="text-gray-500">Total Revenue</h3>
          <p className="text-3xl font-bold">
            ${totalRevenue != null ? Number(totalRevenue).toFixed(2) : "0.00"}
          </p>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Bar Chart */}
        <div className="border rounded-2xl p-4 shadow bg-white">
          <h3 className="font-semibold mb-4">Orders by Status (Bar)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={chartData}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="value" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Pie Chart */}
        <div className="border rounded-2xl p-4 shadow bg-white">
          <h3 className="font-semibold mb-4">Orders Distribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={chartData}
                dataKey="value"
                nameKey="name"
                outerRadius={100}
                label
              >
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Table View */}
      <div className="mt-6 border rounded-2xl p-4 shadow bg-white">
        <h3 className="font-semibold mb-2">Orders by Status</h3>
        <ul className="space-y-1">
          {chartData.map((item) => (
            <li key={item.name} className="flex justify-between">
              <span className="capitalize">{item.name.toLowerCase()}</span>
              <span className="font-semibold">{item.value}</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Report;