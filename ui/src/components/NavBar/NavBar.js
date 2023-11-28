import React from 'react';
import "./NavBar.css";


const NavBar = () => {

  const handleReload = () => {
    window.location.reload()
  }
  
    return (
      <nav className="top-nav-bar">
          <div className="logo" onClick={handleReload}>BudgetBites</div>
          <div className="nav-links">
              <button>About us</button>
              <button>Groceries</button>
              <button>Carts(0)</button>
          </div>
          <button className="support-button">Support</button>
      </nav>
    );
  }

export default NavBar;