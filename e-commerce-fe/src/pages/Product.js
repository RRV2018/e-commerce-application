import { useState } from "react";
import api from "../api/axios";


export default function Product() {
const [product, setProduct] = useState({
name: "",
description: "",
price: 0,
stock: 0,
categoryId: 1,
});


const addProduct = async () => {
const res = await api.post("api/products", product);
alert("Product Created with ID: " + res.data.id);
};


return (
<div>
<h3>Product Management</h3>
<input placeholder="Name" onChange={(e) => setProduct({ ...product, name: e.target.value })} />
<input placeholder="Description" onChange={(e) => setProduct({ ...product, description: e.target.value })} />
<input placeholder="Price" type="number" onChange={(e) => setProduct({ ...product, price: e.target.value })} />
<input placeholder="Stock" type="number" onChange={(e) => setProduct({ ...product, stock: e.target.value })} />
<input placeholder="Category Id" type="number" onChange={(e) => setProduct({ ...product, categoryId: e.target.value })} />
<button onClick={addProduct}>Add Product</button>
</div>
);
}