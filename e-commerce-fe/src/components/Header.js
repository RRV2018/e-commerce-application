import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const Header = () => {
    const [userMenu, setUserMenu] = useState(false);
    const [productsMenu, setProductsMenu] = useState(false);
    const [ordersMenu, setOrdersMenu] = useState(false);
    const [reportMenu, setReportMenu] = useState(false);
    const navigate = useNavigate();
    const toggleMenu = (menu) => {
        setUserMenu(menu === "user" ? !userMenu : false);
        setProductsMenu(menu === "products" ? !productsMenu : false);
        setOrdersMenu(menu === "orders" ? !ordersMenu : false);
        setReportMenu(menu === "report" ? !reportMenu : false);
    };

  const handleLogout = () => {
    // ✅ remove token
    sessionStorage.removeItem("token");

    // ✅ redirect to login
    navigate("/login", { replace: true });
  };
    return (
    <header className = "bg-gray-800 text-white shadow-md" >
        <div className = "container mx-auto flex justify-between items-center p-4" >
        <nav className = "flex space-x-4 relative" >
        <table width="80%">
        <tr>
        <td>
        <Link to = "/users"
        className = "block px-4 py-2 hover:bg-gray-100" >
        Users Management </Link>
        </td>

        <td >
            <Link to = "/products" className = "block px-4 py-2 hover:bg-gray-100" >
                Products Management
            </Link>
        </td>
        <td >
            <Link to = "/orders" className = "block px-4 py-2 hover:bg-gray-100" >
         Orders Management
            </Link>
        </td>
        <td >
            <Link to = "/report" className = "block px-4 py-2 hover:bg-gray-100" >
                Analysis Report
            </Link>
        </td >
        <td>
        <button
              type="button"
              onClick={handleLogout}
               className="text-blue-600 hover:underline bg-transparent p-0">
              Logout
            </button>
        </td>

        </tr>

        </table>

        </nav>
      </div>
      </header>
    );
};

export default Header;