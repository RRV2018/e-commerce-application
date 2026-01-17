import { useNavigate, Link } from "react-router-dom";
import Category from "./Category";

function Dashboard() {
  const navigate = useNavigate();


  return (
    <div style={styles.container}>

      <p>Welcome to your dashboard 🎉</p>
      <Category />
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
