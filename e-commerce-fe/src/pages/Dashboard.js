import { useNavigate, Link } from "react-router-dom";
import Category from "./Category";
import "./css/Dashboard.css";

function Dashboard() {
  const navigate = useNavigate();

  return (
    <div className="dashboard-wrap">
      <div className="dashboard-hero">
        <h1>Welcome to your dashboard</h1>
        <p>Manage categories and access the admin sections from the menu.</p>
      </div>
      <Category />
    </div>
  );
}

export default Dashboard;
