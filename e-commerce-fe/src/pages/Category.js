import { useState } from "react";
import api from "../api/axios";


export default function Category() {
const [name, setName] = useState("");


const addCategory = async () => {
await api.post("api/products/category", { name });
alert("Category Added");
};


return (
<div>
<h3>Category Management</h3>
<input placeholder="Category Name" onChange={(e) => setName(e.target.value)} />
<button onClick={addCategory}>Add</button>
</div>
);
}