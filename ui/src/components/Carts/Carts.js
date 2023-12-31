
import React from 'react'
import './Carts.css'
import { useState } from 'react'
import { useCookies } from 'react-cookie'

const Carts = () => {
    const [cookies, setCookie, removeCookie] = useCookies(['userSelectedCarts']);
    const [selectedCarts, setSelectedCarts] = useState(0); 

    const handleRemoveCart = (ind) => {
        const temp = cookies.userSelectedCarts;
        temp.splice(ind, 1);
        if (temp.length === 0) {
            removeCookie('userSelectedCarts', { path: '/' });
            window.location.href = "/";
            return;
        }
        selectedCarts === ind ? setSelectedCarts(0) : setSelectedCarts(selectedCarts);
        setCookie('userSelectedCarts', temp, { path: '/' });
    }

    const handleRemoveItem = (ind) => {
        const temp = cookies.userSelectedCarts;
        const itemCost = temp[selectedCarts].cart[ind].cost;
        temp[selectedCarts].cart.splice(ind, 1);
        temp[selectedCarts].totalCost = temp[selectedCarts].totalCost - itemCost;
        if (temp[selectedCarts].cart.length === 0) {
            handleRemoveCart(selectedCarts);
            return;
        }
        setCookie('userSelectedCarts', temp, { path: '/' });
    }
    const formatTotalCost = (value) => {
        const stringValue = value.toString();
        if (stringValue.length <= 2) {
            // Handle values less than 100
            return `0.${stringValue.padStart(2, '0')}`;
        } else {
            return `${stringValue.slice(0, -2)}.${stringValue.slice(-2)}`;
        }
    }




  return (
    <div className='saved-carts-wrapper'>
        <div className='saved-carts-title'>Previously Saved Carts</div>
        <div className='saved-carts-content'>       
            <div className="saved-carts-result">
                        {
                            cookies.userSelectedCarts.map((cart, ind) => (
                                <div className={`saved-carts-store ${selectedCarts === ind ? "selected" : ""}`}
                                    key={cart.storeId+ind}
                                    onClick={() => {
                                      setSelectedCarts(ind);
                                    }}>
                                    <div className="saved-carts-store-small">
                                        <div className="saved-carts-store-small-name">
                                         {cart.storeName}
                                        </div>
                                        <div> id: {cart.storeId}</div>
                                    </div>
                                </div>
                            ))
                        }
            </div>
            <div className='saved-carts-selected-store-cart'>
                <div className='saved-carts-selected-store-cart-content'>
                    {
                        cookies.userSelectedCarts[selectedCarts].cart.map((item, ind) => (
                            <div className='saved-carts-selected-store-cart-item' key={JSON.stringify(item)+item.id}>
                                <div className='saved-carts-selected-store-cart-item-name'>
                                    {item.item.name}
                                </div>
                                <div className='saved-carts-selected-store-cart-item-amount'>
                                    Amount:{item.item.amount} ${formatTotalCost(item.cost)}
                                    <div className="saved-carts-selected-store-cart-item-amount-remove" 
                                        onClick={()=>handleRemoveItem(ind)}
                                        >-
                                    </div>
                                </div>
                            </div>
                        ))
                    }
                </div>

                <div className='saved-carts-selected-store-cart-summary'>
                    <div className='saved-carts-selected-store-cart-summary-total'>
                        Total: ${formatTotalCost(cookies.userSelectedCarts[selectedCarts].totalCost)}

                    </div>
                    <button className='saved-carts-selected-store-cart-summary-button'
                            onClick={()=>handleRemoveCart(selectedCarts)}
                    >
                        Remove Cart
                    </button>
                </div>

            </div>
  

        </div>
    </div>
  )
}

export default Carts