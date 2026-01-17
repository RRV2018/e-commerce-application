import React, { useState } from "react";
import { Link } from "react-router-dom";

const Header = () => {
  const [userMenu, setUserMenu] = useState(false);
  const [productsMenu, setProductsMenu] = useState(false);
  const [ordersMenu, setOrdersMenu] = useState(false);
  const [reportMenu, setReportMenu] = useState(false);

  const toggleMenu = (menu) => {
    setUserMenu(menu === "user" ? !userMenu : false);
    setProductsMenu(menu === "products" ? !productsMenu : false);
    setOrdersMenu(menu === "orders" ? !ordersMenu : false);
    setReportMenu(menu === "report" ? !reportMenu : false);
  };

  return (
    <header className="bg-gray-800 text-white shadow-md">
      <div className="container mx-auto flex justify-between items-center p-4">
        <h1 className="text-xl font-bold">Admin Dashboard</h1>

        <nav className="flex space-x-4 relative">
          {/* User Menu */}
          <div className="relative">
            <button onClick={() => toggleMenu("user")}>User ▼</button>
            {userMenu && (
              <div className="absolute bg-white text-black mt-2 rounded shadow-md">
                <Link to="/users" className="block px-4 py-2 hover:bg-gray-100">
                  All Users
                </Link>
              </div>
            )}
          </div>

          {/* Products Menu */}
          <div className="relative">
            <button onClick={() => toggleMenu("products")}>Products ▼</button>
            {productsMenu && (
              <div className="absolute bg-white text-black mt-2 rounded shadow-md">
                <Link to="/products" className="block px-4 py-2 hover:bg-gray-100">
                  All Products
                </Link>
              </div>
            )}
          </div>

          {/* Orders Menu */}
          <div className="relative">
            <button onClick={() => toggleMenu("orders")}>Orders ▼</button>
            {ordersMenu && (
              <div className="absolute bg-white text-black mt-2 rounded shadow-md">
                <Link to="/orders" className="block px-4 py-2 hover:bg-gray-100">
                  All Orders
                </Link>
              </div>
            )}
          </div>

          {/* Report Menu */}
          <div className="relative">
            <button onClick={() => toggleMenu("report")}>Report ▼</button>
            {reportMenu && (
              <div className="absolute bg-white text-black mt-2 rounded shadow-md">
                <Link to="/report" className="block px-4 py-2 hover:bg-gray-100">
                  Analysis Report
                </Link>
              </div>
            )}
          </div>
        </nav>
      </div>
    </header>
  );
};

export default Header;
