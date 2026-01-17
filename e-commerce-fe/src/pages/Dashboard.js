import { useNavigate, Link } from "react-router-dom";
import Category from "./Category";

function Dashboard() {
  const navigate = useNavigate();

  const handleLogout = () => {
    // ✅ remove token
    localStorage.removeItem("token");

    // ✅ redirect to login
    navigate("/login", { replace: true });
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2>Dashboard</h2>

        <button style={styles.logoutBtn} onClick={handleLogout}>
          Logout
        </button>
      </div>

      <p>Welcome to your dashboard 🎉</p>
      <Category />
      <Link to="/products">View Products</Link>
    </div>
  );
}

const styles = {
  container: {
    padding: "20px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "20px",
  },
  logoutBtn: {
    background: "#e63946",
    color: "#fff",
    border: "none",
    padding: "10px 16px",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
  },
};

export default Dashboard;
