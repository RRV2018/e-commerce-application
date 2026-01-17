import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Header from "./components/Header";
import Dashboard from "./pages/Dashboard";
import Users from "./pages/Users";
import Products from "./pages/Products";
import Orders from "./pages/Orders";
import Report from "./pages/Report";
import Login from "./pages/Login";
import ProtectedRoute from "./routes/ProtectedRoute";
import HeaderWrapper from "./components/HeaderWrapper";



const App = () => {
  return (
    <Router>
      <div className="container mx-auto mt-6">
        <HeaderWrapper>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />  {/* redirect / to dashboard */}
              <Route path="/dashboard" element={ <ProtectedRoute> <Dashboard /></ProtectedRoute>} />
              <Route path="/products" element={ <ProtectedRoute> <Products /></ProtectedRoute>} />
              <Route path="/dashboard" element={ <ProtectedRoute> <Dashboard /></ProtectedRoute>} />
              <Route path="/users" element={ <ProtectedRoute> <Users /></ProtectedRoute>} />
              <Route path="/orders" element={ <ProtectedRoute><Orders /></ProtectedRoute>} />
              <Route path="/report" element={ <ProtectedRoute> <Report /></ProtectedRoute>} />
            </Routes>
        </HeaderWrapper>
      </div>
    </Router>
  );
};

export default App;
