import React, { useCallback, useEffect, useMemo, useState } from "react";
import api from "../api/axios";
import "./css/PagesCommon.css";
import "./css/Report.css";
import { FaShoppingBag, FaDollarSign, FaChartLine, FaSyncAlt } from "react-icons/fa";
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

const STATUS_ORDER = ["CREATED", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"];

const CHART_COLORS = [
  "#6366f1",
  "#8b5cf6",
  "#a855f7",
  "#ec4899",
  "#f97316",
  "#0ea5e9",
  "#14b8a6",
  "#84cc16",
];

function formatStatusLabel(key) {
  if (!key) return "";
  return String(key)
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

function buildChartRows(ordersByStatus) {
  if (!ordersByStatus || typeof ordersByStatus !== "object") return [];
  const seen = new Set();
  const rows = [];
  for (const s of STATUS_ORDER) {
    if (ordersByStatus[s] != null) {
      seen.add(s);
      rows.push({
        key: s,
        name: formatStatusLabel(s),
        value: Number(ordersByStatus[s]) || 0,
      });
    }
  }
  for (const [k, v] of Object.entries(ordersByStatus)) {
    if (seen.has(k)) continue;
    rows.push({
      key: k,
      name: formatStatusLabel(k),
      value: Number(v) || 0,
    });
  }
  return rows;
}

const currencyFmt = new Intl.NumberFormat(undefined, {
  style: "currency",
  currency: "USD",
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
});

const Report = () => {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchSummary = useCallback(() => {
    setLoading(true);
    setError(null);
    api
      .get("/api/order/reports/summary")
      .then((res) => setSummary(res.data))
      .catch((err) => setError(err.response?.data?.message || err.message))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    fetchSummary();
  }, [fetchSummary]);

  const chartData = useMemo(() => buildChartRows(summary?.ordersByStatus), [summary]);

  const totalCount = useMemo(
    () => chartData.reduce((acc, d) => acc + d.value, 0),
    [chartData]
  );

  const tableRows = useMemo(() => {
    if (totalCount <= 0) return chartData.map((d) => ({ ...d, pct: 0 }));
    return chartData.map((d) => ({
      ...d,
      pct: (d.value / totalCount) * 100,
    }));
  }, [chartData, totalCount]);

  if (loading) {
    return (
      <div className="report-page">
        <div className="page-loading">Loading report data…</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="report-page">
        <div className="page-card">
          <p className="page-error" style={{ textAlign: "left", padding: 0 }}>
            {error}
          </p>
          <button type="button" className="btn btn-primary" style={{ marginTop: 16 }} onClick={fetchSummary}>
            Try again
          </button>
        </div>
      </div>
    );
  }

  if (!summary) return null;

  const totalOrders = Number(summary.totalOrders) || 0;
  const totalRevenue = summary.totalRevenue != null ? Number(summary.totalRevenue) : 0;
  const avgOrder =
    totalOrders > 0 && Number.isFinite(totalRevenue) ? totalRevenue / totalOrders : 0;

  const pieLabel = ({ name, percent }) =>
    `${name} ${percent != null ? (percent * 100).toFixed(0) : 0}%`;

  return (
    <div className="report-page">
      <header className="report-header">
        <div className="report-header-text">
          <h1>Sales &amp; orders report</h1>
          <p>
            Order volume, revenue, and status breakdown from your store. Data comes from the order service summary
            endpoint.
          </p>
        </div>
        <button type="button" className="btn btn-secondary btn-sm" onClick={fetchSummary} title="Refresh data">
          <FaSyncAlt className="inline-icon" style={{ marginRight: 6 }} />
          Refresh
        </button>
      </header>

      <div className="report-kpi-grid">
        <div className="report-kpi-card">
          <div className="report-kpi-icon report-kpi-icon--orders" aria-hidden>
            <FaShoppingBag />
          </div>
          <div className="report-kpi-body">
            <div className="report-kpi-label">Total orders</div>
            <div className="report-kpi-value">{totalOrders.toLocaleString()}</div>
          </div>
        </div>
        <div className="report-kpi-card">
          <div className="report-kpi-icon report-kpi-icon--revenue" aria-hidden>
            <FaDollarSign />
          </div>
          <div className="report-kpi-body">
            <div className="report-kpi-label">Total revenue</div>
            <div className="report-kpi-value">{currencyFmt.format(totalRevenue)}</div>
          </div>
        </div>
        <div className="report-kpi-card">
          <div className="report-kpi-icon report-kpi-icon--avg" aria-hidden>
            <FaChartLine />
          </div>
          <div className="report-kpi-body">
            <div className="report-kpi-label">Avg order value</div>
            <div className="report-kpi-value">{currencyFmt.format(avgOrder)}</div>
          </div>
        </div>
      </div>

      {chartData.length === 0 ? (
        <div className="report-chart-card">
          <h2>No status breakdown</h2>
          <p className="report-chart-sub">There are no orders by status to chart yet.</p>
          <div className="report-empty-charts">Place some orders to see charts and the breakdown table.</div>
        </div>
      ) : (
        <>
          <div className="report-charts-row">
            <div className="report-chart-card">
              <h2>Orders by status</h2>
              <p className="report-chart-sub">Count per lifecycle stage</p>
              <div className="report-chart-area">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={chartData} margin={{ top: 8, right: 12, left: 0, bottom: 4 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" vertical={false} />
                    <XAxis dataKey="name" tick={{ fontSize: 12, fill: "#64748b" }} axisLine={{ stroke: "#e2e8f0" }} />
                    <YAxis
                      allowDecimals={false}
                      tick={{ fontSize: 12, fill: "#64748b" }}
                      axisLine={false}
                      tickLine={false}
                    />
                    <Tooltip
                      cursor={{ fill: "rgba(99, 102, 241, 0.06)" }}
                      contentStyle={{
                        borderRadius: 10,
                        border: "1px solid #e2e8f0",
                        boxShadow: "0 8px 24px rgba(30, 41, 59, 0.12)",
                      }}
                      formatter={(value) => [value, "Orders"]}
                    />
                    <Bar dataKey="value" radius={[8, 8, 0, 0]} maxBarSize={56}>
                      {chartData.map((_, i) => (
                        <Cell key={`bar-${i}`} fill={CHART_COLORS[i % CHART_COLORS.length]} />
                      ))}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>

            <div className="report-chart-card">
              <h2>Status mix</h2>
              <p className="report-chart-sub">Share of all orders</p>
              <div className="report-chart-area report-chart-area--compact">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={chartData}
                      dataKey="value"
                      nameKey="name"
                      cx="50%"
                      cy="50%"
                      innerRadius={58}
                      outerRadius={92}
                      paddingAngle={2}
                      label={pieLabel}
                      labelLine={{ stroke: "#94a3b8" }}
                    >
                      {chartData.map((_, i) => (
                        <Cell key={`pie-${i}`} fill={CHART_COLORS[i % CHART_COLORS.length]} stroke="#fff" strokeWidth={2} />
                      ))}
                    </Pie>
                    <Tooltip
                      formatter={(value) => {
                        const pct = totalCount > 0 ? ((Number(value) / totalCount) * 100).toFixed(1) : "0";
                        return [`${value} (${pct}%)`, "Orders"];
                      }}
                      contentStyle={{
                        borderRadius: 10,
                        border: "1px solid #e2e8f0",
                        boxShadow: "0 8px 24px rgba(30, 41, 59, 0.12)",
                      }}
                    />
                    <Legend
                      verticalAlign="bottom"
                      height={36}
                      formatter={(value) => <span style={{ color: "#475569", fontSize: 12 }}>{value}</span>}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>

          <div className="report-chart-card" style={{ marginBottom: 20 }}>
            <h2>Volume curve by status</h2>
            <p className="report-chart-sub">Same counts as an area series for quick comparison</p>
            <div className="report-chart-area report-chart-area--wide">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData} margin={{ top: 8, right: 16, left: 0, bottom: 0 }}>
                  <defs>
                    <linearGradient id="reportAreaFill" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0%" stopColor="#6366f1" stopOpacity={0.35} />
                      <stop offset="100%" stopColor="#6366f1" stopOpacity={0.02} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" vertical={false} />
                  <XAxis dataKey="name" tick={{ fontSize: 12, fill: "#64748b" }} axisLine={{ stroke: "#e2e8f0" }} />
                  <YAxis allowDecimals={false} tick={{ fontSize: 12, fill: "#64748b" }} axisLine={false} tickLine={false} />
                  <Tooltip
                    contentStyle={{
                      borderRadius: 10,
                      border: "1px solid #e2e8f0",
                      boxShadow: "0 8px 24px rgba(30, 41, 59, 0.12)",
                    }}
                    formatter={(value) => [value, "Orders"]}
                  />
                  <Area
                    type="monotone"
                    dataKey="value"
                    stroke="#6366f1"
                    strokeWidth={2}
                    fill="url(#reportAreaFill)"
                    dot={{ fill: "#6366f1", strokeWidth: 2, r: 4, stroke: "#fff" }}
                    activeDot={{ r: 6 }}
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="report-chart-card" style={{ marginBottom: 20 }}>
            <h2>Horizontal comparison</h2>
            <p className="report-chart-sub">Easier to scan when labels are long</p>
            <div className="report-chart-area report-chart-area--wide">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart layout="vertical" data={chartData} margin={{ top: 4, right: 24, left: 8, bottom: 4 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" horizontal={false} />
                  <XAxis type="number" allowDecimals={false} tick={{ fontSize: 12, fill: "#64748b" }} axisLine={false} />
                  <YAxis
                    type="category"
                    dataKey="name"
                    width={100}
                    tick={{ fontSize: 12, fill: "#64748b" }}
                    axisLine={false}
                    tickLine={false}
                  />
                  <Tooltip
                    cursor={{ fill: "rgba(99, 102, 241, 0.06)" }}
                    contentStyle={{
                      borderRadius: 10,
                      border: "1px solid #e2e8f0",
                      boxShadow: "0 8px 24px rgba(30, 41, 59, 0.12)",
                    }}
                    formatter={(value) => [value, "Orders"]}
                  />
                  <Bar dataKey="value" radius={[0, 8, 8, 0]} barSize={22}>
                    {chartData.map((_, i) => (
                      <Cell key={`hbar-${i}`} fill={CHART_COLORS[i % CHART_COLORS.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </>
      )}

      {chartData.length > 0 && (
        <div className="report-table-card">
          <h2>Breakdown table</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Status</th>
                <th className="right">Orders</th>
                <th className="right">Share</th>
                <th>Distribution</th>
              </tr>
            </thead>
            <tbody>
              {tableRows.map((row, i) => (
                <tr key={row.key}>
                  <td>{row.name}</td>
                  <td className="right num">{row.value.toLocaleString()}</td>
                  <td className="right num">{row.pct.toFixed(1)}%</td>
                  <td>
                    <div className="report-progress-cell">
                      <div className="report-progress-track">
                        <div
                          className="report-progress-fill"
                          style={{
                            width: `${Math.min(100, row.pct)}%`,
                            background: CHART_COLORS[i % CHART_COLORS.length],
                          }}
                        />
                      </div>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default Report;
