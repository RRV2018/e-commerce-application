import React from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import "./Header.css";

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    sessionStorage.removeItem("token");
    navigate("/login", { replace: true });
  };

  const isActive = (path) => location.pathname === path;

  return (
    <header className="header">
      <div className="header-container">
        <div className="logo">
          <span>🛒 E-Commerce Admin</span>
        </div>

        <nav className="nav">
          <Link className={`nav-link ${isActive("/users") ? "active" : ""}`} to="/users">
            Users
          </Link>

          <Link className={`nav-link ${isActive("/products") ? "active" : ""}`} to="/products">
            Products
          </Link>

          <Link className={`nav-link ${isActive("/orders") ? "active" : ""}`} to="/orders">
            Orders
          </Link>

          <Link className={`nav-link ${isActive("/report") ? "active" : ""}`} to="/report">
            Reports
          </Link>
        </nav>

        <button className="logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </header>
  );
};

export default Header;
