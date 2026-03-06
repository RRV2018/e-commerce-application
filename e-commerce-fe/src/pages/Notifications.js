import React, { useEffect, useState } from "react";
import api from "../api/axios";
import "./css/PagesCommon.css";

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchNotifications = async () => {
    try {
      setError("");
      const res = await api.get("/api/user/notifications");
      setNotifications(res.data || []);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load notifications.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const markAsRead = async (id) => {
    try {
      await api.patch(`/api/user/notifications/${id}/read`);
      fetchNotifications();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update.");
    }
  };

  if (loading) return <div className="page-card"><p>Loading notifications...</p></div>;
  if (error) return <div className="page-card"><p className="form-error">{error}</p></div>;

  return (
    <div className="page-card">
      <h1 className="page-title">Notifications</h1>
      {notifications.length === 0 ? (
        <p className="page-empty">No notifications yet.</p>
      ) : (
        <ul className="notifications-list">
          {notifications.map((n) => (
            <li key={n.id} className={n.read ? "notification-item read" : "notification-item unread"}>
              <div className="notification-header">
                <strong>{n.title}</strong>
                <span className="notification-date">{n.createdAt ? new Date(n.createdAt).toLocaleString() : ""}</span>
              </div>
              <p className="notification-message">{n.message}</p>
              {!n.read && (
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => markAsRead(n.id)}
                >
                  Mark as read
                </button>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Notifications;
