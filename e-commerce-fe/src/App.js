import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Header from "./components/Header";
import Dashboard from "./pages/Dashboard";
import Users from "./pages/Users";
import Products from "./pages/Products";
import Orders from "./pages/Orders";
import FileOperation from "./pages/FileOperation";
import Report from "./pages/Report";
import Wishlist from "./pages/Wishlist";
import Notifications from "./pages/Notifications";
import Cart from "./pages/Cart";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import ProtectedRoute from "./routes/ProtectedRoute";
import HeaderWrapper from "./components/HeaderWrapper";



const App = () => {
  return (
    <Router>
      <div className="container mx-auto mt-6">
        <HeaderWrapper>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />  {/* redirect / to dashboard */}
              <Route path="/dashboard" element={ <ProtectedRoute> <Dashboard /></ProtectedRoute>} />
              <Route path="/products" element={ <ProtectedRoute> <Products /></ProtectedRoute>} />
              <Route path="/dashboard" element={ <ProtectedRoute> <Dashboard /></ProtectedRoute>} />
              <Route path="/users" element={ <ProtectedRoute> <Users /></ProtectedRoute>} />
              <Route path="/orders" element={ <ProtectedRoute><Orders /></ProtectedRoute>} />
              <Route path="/fileOperation" element={ <ProtectedRoute><FileOperation /></ProtectedRoute>} />
              <Route path="/report" element={ <ProtectedRoute> <Report /></ProtectedRoute>} />
              <Route path="/wishlist" element={ <ProtectedRoute> <Wishlist /></ProtectedRoute>} />
              <Route path="/notifications" element={ <ProtectedRoute> <Notifications /></ProtectedRoute>} />
              <Route path="/cart" element={ <ProtectedRoute> <Cart /></ProtectedRoute>} />
            </Routes>
        </HeaderWrapper>
      </div>
    </Router>
  );
};

export default App;
